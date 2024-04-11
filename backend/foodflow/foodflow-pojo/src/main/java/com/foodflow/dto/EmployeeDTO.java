package com.foodflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema($schema = "Employee data transfer object")
public class EmployeeDTO implements Serializable {

    private Long id;

    private String username;

    private String name;

    private String phone;

    private String sex;

    @Schema($schema = "Citizen Unique Id")
    private String idNumber;

}
