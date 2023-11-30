package com.daishuai.demo.mapper;

import com.daishuai.demo.model.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Tom
 * @version 1.0.0
 * @description TODO
 * @createTime 2023年11月29日 15:13:00
 */
@Mapper
public interface JobConfigMapper {

    @Select(value = "select value from media_system_config where id = #{jobId} ")
    String queryJobConfig(String jobId);

    @Select(value = "select code, value, description from media_system_config where type = #{type} ")
    List<SystemConfig> queryByType(Integer type);
}
