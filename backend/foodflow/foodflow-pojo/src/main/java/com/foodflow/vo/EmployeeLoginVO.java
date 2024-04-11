package com.foodflow.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "员工登录返回的数据格式")
public class EmployeeLoginVO implements Serializable {

    @Schema($schema = "主键值")
    private Long id;

    @Schema($schema = "用户名")
    private String userName;

    @Schema($schema = "姓名")
    private String name;

    @Schema($schema = "jwt令牌")
    private String token;

}
