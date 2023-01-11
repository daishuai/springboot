package com.daishuai.validator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description 校验器
 * @createTime 2023年01月05日 19:03:00
 */
@RestController
public class ValidatorController {

    public ResponseEntity<String> validator() {

        return ResponseEntity.ok("ok");
    }
}
