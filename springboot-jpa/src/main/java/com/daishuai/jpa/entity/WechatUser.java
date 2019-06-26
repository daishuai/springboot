package com.daishuai.jpa.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Daishuai
 * @description 用户
 * @date 2019/6/26 10:07
 */
@Table(name = "wechat_user")
@Entity
@Data
public class WechatUser {
    
    @Id
    private String uuid;
    
    private String name;
    
    private String password;
    
    private String address;
}
