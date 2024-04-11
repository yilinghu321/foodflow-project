package com.foodflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "员工登录时传递的数据模型")
public class EmployeeLoginDTO implements Serializable {

    @Schema($schema = "用户名")
    private String username;

    @Schema($schema = "密码")
    private String password;

}
