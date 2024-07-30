/*
*  Copyright 2019-2023 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package com.mh.mock.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
* @description /
* @author Max
* @date 2024-06-25
**/
@Data
@TableName("mock_server.nacos_mock_server")
public class NacosMockServer implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "服务名")
    private String serviceName;

    @ApiModelProperty(value = "nacos地址")
    private String nacosUrl;

    @ApiModelProperty(value = "namespaceShowName名称")
    private String namespaceShowName;

    @ApiModelProperty(value = "namespace_id")
    private String namespaceId;

    @ApiModelProperty(value = "mockser的id")
    private String mockServerId;

    @ApiModelProperty(value = "mock_server_ip")
    private String mockServerIp;

    @ApiModelProperty(value = "mock_server_port")
    private String mockServerPort;

    public void copy(NacosMockServer source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
