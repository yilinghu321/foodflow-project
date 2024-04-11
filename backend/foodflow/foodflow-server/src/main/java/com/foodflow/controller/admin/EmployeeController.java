package com.foodflow.controller.admin;

import com.foodflow.constant.JwtClaimsConstant;
import com.foodflow.dto.EmployeeDTO;
import com.foodflow.dto.EmployeeLoginDTO;
import com.foodflow.entity.Employee;
import com.foodflow.properties.JwtProperties;
import com.foodflow.result.Result;
import com.foodflow.service.EmployeeService;
import com.foodflow.utils.JwtUtil;
import com.foodflow.vo.EmployeeLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Tag(name = "Employee Management Api")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;


    /**
     * Employee login process.
     *
     * @param employeeLoginDTO the DTO of employee login data
     * @return Result of login
     */
    @PostMapping("/login")
    @Operation(summary = "Employee Login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
            jwtProperties.getAdminSecretKey(),
            jwtProperties.getAdminTtl(),
            claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
            .id(employee.getId())
            .userName(employee.getUsername())
            .name(employee.getName())
            .token(token)
            .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * Save new employee.
     *
     * @param employeeDTO DTO of new employee to save
     * @return Result of saving
     */
    @PostMapping
    @Operation(summary = "Add employee")
    public Result saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        log.info("Add new employee: {}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

}
