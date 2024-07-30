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

import com.mh.mock.domain.MockServiceConfig;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mh.mock.service.MockServiceConfigService;
import com.mh.mock.domain.vo.MockServiceConfigQueryCriteria;
import com.mh.mock.mapper.MockServiceConfigMapper;
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
* @author max
* @date 2024-07-29
**/
@Service
@RequiredArgsConstructor
public class MockServiceConfigServiceImpl extends ServiceImpl<MockServiceConfigMapper, MockServiceConfig> implements MockServiceConfigService {

    private final MockServiceConfigMapper mockServiceConfigMapper;

    @Override
    public PageResult<MockServiceConfig> queryAll(MockServiceConfigQueryCriteria criteria, Page<Object> page){
        return PageUtil.toPage(mockServiceConfigMapper.findAll(criteria, page));
    }

    @Override
    public List<MockServiceConfig> queryAll(MockServiceConfigQueryCriteria criteria){
        return mockServiceConfigMapper.findAll(criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(MockServiceConfig resources) {
        save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MockServiceConfig resources) {
        MockServiceConfig mockServiceConfig = getById(resources.getId());
        mockServiceConfig.copy(resources);
        saveOrUpdate(mockServiceConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Long> ids) {
        removeBatchByIds(ids);
    }

    @Override
    public void download(List<MockServiceConfig> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MockServiceConfig mockServiceConfig : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put(" serviceName",  mockServiceConfig.getServiceName());
            map.put(" configOptions",  mockServiceConfig.getConfigOptions());
            map.put(" forwardUrl",  mockServiceConfig.getForwardUrl());
            map.put(" remark",  mockServiceConfig.getRemark());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}