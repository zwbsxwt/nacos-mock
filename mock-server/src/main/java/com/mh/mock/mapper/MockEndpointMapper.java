package com.mh.mock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mh.mock.entity.MockEndpoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MockEndpointMapper extends BaseMapper<MockEndpoint> {

    @Select("SELECT * FROM mock_endpoint WHERE service_name = #{serviceName} AND method = #{method} AND url = #{url} AND (params IS NULL OR params = #{params})")
    MockEndpoint findByServiceNameAndMethodAndUrlAndParams(@Param("serviceName") String serviceName, @Param("method") String method, @Param("url") String url, @Param("params") String params);

    @Select("SELECT * FROM mock_endpoint WHERE service_name = #{serviceName} AND method = #{method} AND is_open=1  ORDER BY weight DESC " )
    List<MockEndpoint> findByServiceNameAndMethod(@Param("serviceName") String serviceName, @Param("method") String method);


}

