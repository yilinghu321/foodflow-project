package com.foodflow.service.impl;

import com.foodflow.constant.PasswordConstant;
import com.foodflow.dto.EmployeeDTO;
import com.foodflow.mapper.EmployeeMapper;
import com.foodflow.constant.MessageConstant;
import com.foodflow.constant.StatusConstant;
import com.foodflow.dto.EmployeeLoginDTO;
import com.foodflow.entity.Employee;
import com.foodflow.exception.AccountLockedException;
import com.foodflow.exception.AccountNotFoundException;
import com.foodflow.exception.PasswordErrorException;
import com.foodflow.service.EmployeeService;
import com.foodflow.utils.BeanHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Value("${foodflow.salt}")
    private String cryptToken;

    private static final String LOGIN_ERROR_KEY = "login:error:";
    private static final String LOGIN_LOCK_KEY = "login:lock:";

    /**
     * Check if the given EmployeeLoginDTO is a valid Employee;
     * If credential is valid and the account is not locked,
     * return the Employee, otherwise throw Exceptions.
     *
     * @param employeeLoginDTO DTO of given Employee
     * @return the Employee
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        checkAccountLock(username);
        //1、mapping through the database based on given username
        Employee employee = employeeMapper.getByUsername(username);

        //2、handling exceptions（user not found、invalid password、account locked）
        if (employee == null) {
            //account not found
            log.info("Account not found.");
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //validate password
        password = DigestUtils.md5DigestAsHex((password + cryptToken).getBytes());

        if (!password.equals(employee.getPassword())) {
            //password invalid
            log.info("Invalid password.");

            //mark for invalid input of password
            redisTemplate.opsForValue().set(getKey(username), "-", 5, TimeUnit.MINUTES);
            Set<Object> keys = redisTemplate.keys(LOGIN_ERROR_KEY + username + ":*");

            if (keys != null && keys.size() >= 5) {
                log.info("5 attempts of invalid password in 5 min, locked for 1 hour.");
                redisTemplate.opsForValue().set(LOGIN_LOCK_KEY + username, "+", 1, TimeUnit.HOURS);
            }
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus().equals(StatusConstant.DISABLE)) {
            //account locked
            log.info("Account locked.");
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、return the employee
        return employee;
    }

    /**
     * Save the given Employee data to the database.
     *
     * @param employeeDTO DTO of given Employee
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = BeanHelper.copyProperties(employeeDTO, Employee.class);

        employee.setPassword(DigestUtils.md5DigestAsHex((PasswordConstant.DEFAULT_PASSWORD + cryptToken).getBytes()));
        employee.setStatus(StatusConstant.ENABLE);
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        // TODO
        employee.setCreateUser(1L);
        employee.setUpdateUser(1L);

        employeeMapper.insert(employee);
    }

    /**
     * Check if the given account to check is locked.
     * @param username username of the given account the check
     */
    private void checkAccountLock(String username) {
        Object lockFlag = redisTemplate.opsForValue().get(LOGIN_LOCK_KEY + username);
        if (!ObjectUtils.isEmpty(lockFlag)) {
            log.info("5 attempts of invalid password in 5 min, locked for 1 hour.");
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOGIN_LOCKED);
        }
    }

    /**
     * Generate key for redis storing user with invalid attempt of log in
     * @param username the username
     * @return String of redis key
     */
    private static String getKey(String username) {
        return LOGIN_ERROR_KEY + username + ":" + RandomStringUtils.randomAlphabetic(5);
    }

}
