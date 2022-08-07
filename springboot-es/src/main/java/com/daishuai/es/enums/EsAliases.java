package com.daishuai.es.enums;

/**
 * @author Daishuai
 * @version 1.0.0
 * @description ES索引
 * @createTime 2022年05月27日 13:59:00
 */
public interface EsAliases {

    /**
     * 获取索引名
     *
     * @return  索引名
     */
    String getIndex();

    String getRead();

    String getWrite();

    String getType();

    String getDesc();
}
