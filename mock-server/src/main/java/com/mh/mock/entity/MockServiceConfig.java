package com.mh.mock.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("mock_service_config")
public class MockServiceConfig {
    
    @TableId
    private Long id;
    private String serviceName;
    private String configOptions;
    private String forwardUrl;
}
