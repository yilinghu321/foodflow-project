package com.foodflow.service;

import com.foodflow.dto.EmployeeLoginDTO;
import com.foodflow.entity.Employee;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

}
