package com.daishuai.validator.controller;

import com.daishuai.validator.dto.UserInfoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 校验器
 * @createTime 2023年01月05日 19:03:00
 */
@RestController
public class ValidatorController {

    public ResponseEntity<String> validator(@Valid UserInfoDto userInfoDto) {

        return ResponseEntity.ok("ok");
    }
}
