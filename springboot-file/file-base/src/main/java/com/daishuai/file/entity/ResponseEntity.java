package com.daishuai.file.entity;

import lombok.Data;

/**
 * @author Daishuai
 * @description 响应
 * @date 2019/7/4 12:42
 */
@Data
public class ResponseEntity {
    
    private String code;
    
    private String message;
    
    private Object result;
    
    public ResponseEntity(String code, String message, Object result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }
    
    public static ResponseEntity error(String message, Object result) {
        return new ResponseEntity("500", message, result);
    }
    
    public static ResponseEntity success(Object result) {
        return new ResponseEntity("200", "处理成功", result);
    }
    
    public static ResponseEntity success() {
        return success(null);
    }
}
