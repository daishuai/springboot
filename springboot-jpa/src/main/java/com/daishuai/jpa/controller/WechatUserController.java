package com.daishuai.jpa.controller;

import com.daishuai.jpa.entity.WechatUser;
import com.daishuai.jpa.repository.WechatUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author Daishuai
 * @description 用户
 * @date 2019/6/26 10:18
 */
@RestController
@RequestMapping("/wechatUser")
public class WechatUserController {
    
    @Autowired
    private WechatUserDao wechatUserDao;
    
    @GetMapping("/add")
    public Object addWechatUser() {
        WechatUser wechatUser = new WechatUser();
        wechatUser.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
        wechatUser.setName("Tom");
        wechatUser.setPassword("helloworld");
        wechatUser.setAddress("江苏省苏州市工业园区");
        wechatUserDao.insertWechatUser(wechatUser);
        return wechatUser;
    }
}
