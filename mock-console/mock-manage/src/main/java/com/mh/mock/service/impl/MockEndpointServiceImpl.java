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
package com.mh.mock.service.impl;

import com.mh.mock.domain.MockEndpoint;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mh.mock.service.MockEndpointService;
import com.mh.mock.domain.vo.MockEndpointQueryCriteria;
import com.mh.mock.mapper.MockEndpointMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import me.zhengjie.utils.PageUtil;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import me.zhengjie.utils.PageResult;

/**
* @description 服务实现
* @author Max
* @date 2024-07-29
**/
@Service
@RequiredArgsConstructor
public class MockEndpointServiceImpl extends ServiceImpl<MockEndpointMapper, MockEndpoint> implements MockEndpointService {

    private final MockEndpointMapper mockEndpointMapper;

    @Override
    public PageResult<MockEndpoint> queryAll(MockEndpointQueryCriteria criteria, Page<Object> page){
        return PageUtil.toPage(mockEndpointMapper.findAll(criteria, page));
    }

    @Override
    public List<MockEndpoint> queryAll(MockEndpointQueryCriteria criteria){
        return mockEndpointMapper.findAll(criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(MockEndpoint resources) {
        save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MockEndpoint resources) {
        MockEndpoint mockEndpoint = getById(resources.getId());
        mockEndpoint.copy(resources);
        saveOrUpdate(mockEndpoint);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Long> ids) {
        removeBatchByIds(ids);
    }

    @Override
    public void download(List<MockEndpoint> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MockEndpoint mockEndpoint : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("mock名称", mockEndpoint.getMockName());
            map.put(" serviceName",  mockEndpoint.getServiceName());
            map.put(" method",  mockEndpoint.getMethod());
            map.put(" urlPattern",  mockEndpoint.getUrlPattern());
            map.put(" expression",  mockEndpoint.getExpression());
            map.put(" responseTemplate",  mockEndpoint.getResponseTemplate());
            map.put(" responseHeader",  mockEndpoint.getResponseHeader());
            map.put("备注", mockEndpoint.getRemark());
            map.put("权重", mockEndpoint.getWeight());
            map.put(" createTime",  mockEndpoint.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}