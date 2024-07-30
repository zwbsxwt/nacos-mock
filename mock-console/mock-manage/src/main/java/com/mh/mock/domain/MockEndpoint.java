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
import java.sql.Timestamp;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
* @description /
* @author Max
* @date 2024-07-29
**/
@Data
@TableName("mock_server.mock_endpoint")
public class MockEndpoint implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "mock名称")
    private String mockName;

    @ApiModelProperty(value = "serviceName")
    private String serviceName;

    @ApiModelProperty(value = "method")
    private String method;

    @ApiModelProperty(value = "urlPattern")
    private String urlPattern;

    @ApiModelProperty(value = "expression")
    private String expression;

    @ApiModelProperty(value = "responseTemplate")
    private String responseTemplate;

    @ApiModelProperty(value = "responseHeader")
    private String responseHeader;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "权重")
    private Integer weight;

    @ApiModelProperty(value = "是否开启")
    private Integer isOpen;

    @ApiModelProperty(value = "createTime")
    private Timestamp createTime;

    public void copy(MockEndpoint source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
