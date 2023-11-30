package com.daishuai.demo.model;

import lombok.Data;

/**
 * @author Tom
 * @version 1.0.0
 * @description 配置信息
 * @createTime 2023年11月30日 14:06:00
 */
@Data
public class SystemConfig {

    private String code;

    private String value;

    private String description;
}
