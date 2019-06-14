package com.daishuai.es.service;

import com.daishuai.es.entity.EmployeeEntity;

/**
 * @author Daishuai
 * @description 雇员
 * @date 2019/6/7 12:59
 */
public interface EmployeeService {

    /**
     * 根据雇员id查找雇员信息
     * @param id
     * @return
     */
    EmployeeEntity findById(String id);
}
