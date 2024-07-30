package com.mh.mock.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("mock_endpoint")
public class MockEndpoint {

    private Long id;
    private String serviceName;
    private String method;
    private String urlPattern;
    private String expression; // 新增字段
    private String responseTemplate;
    private String responseHeader; // 新增字段
}
