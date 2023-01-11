package com.daishuai.validator.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 用户信息
 * @createTime 2023年01月11日 13:24:00
 */
@Data
public class UserInfoDto {

    private String username;

    private String password;

    private Date birthday;

    private String address;

    private String phone;

    private String email;
}
