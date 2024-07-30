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
package com.mh.mock.mapper;

import com.mh.mock.domain.MockEndpoint;
import com.mh.mock.domain.vo.MockEndpointQueryCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
* @author Max
* @date 2024-07-29
**/
@Mapper
public interface MockEndpointMapper extends BaseMapper<MockEndpoint> {

    IPage<MockEndpoint> findAll(@Param("criteria") MockEndpointQueryCriteria criteria, Page<Object> page);

    List<MockEndpoint> findAll(@Param("criteria") MockEndpointQueryCriteria criteria);
}