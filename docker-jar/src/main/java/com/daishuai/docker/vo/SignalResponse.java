package com.daishuai.docker.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 信令服务器响应
 *
 * @author daishuai
 * @date 2024/5/10 09:57:25
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignalResponse {

    private Integer checkCode;

    private String message;
}
