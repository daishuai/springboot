package com.daishuai.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author Keda
 * @version 1.0.0
 * @description TODO
 * @createTime 2023年11月29日 15:13:00
 */
@Mapper
public interface JobConfigMapper {

    @Select(value = "select value from media_system_config where id = #{jobId} ")
    String queryJobConfig(String jobId);
}
