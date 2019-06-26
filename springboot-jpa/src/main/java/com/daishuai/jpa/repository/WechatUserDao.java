package com.daishuai.jpa.repository;

import com.daishuai.jpa.entity.WechatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Daishuai
 * @description 用户
 * @date 2019/6/26 10:13
 */
@Transactional(rollbackFor = Exception.class)
public interface WechatUserDao extends JpaRepository<WechatUser, String> {
    
    @Modifying
    @Query(value = "insert into wechat_user (uuid,name,password,address) values (:#{#user.uuid},:#{#user.name},:#{#user.password},:#{#user.address})", nativeQuery = true)
    void insertWechatUser(@Param("user") WechatUser wechatUser);
}
