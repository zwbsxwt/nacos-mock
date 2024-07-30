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

import com.mh.mock.domain.NacosMockServer;
import com.mh.mock.service.manager.HeartbeatManager;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mh.mock.service.NacosMockServerService;
import com.mh.mock.domain.vo.NacosMockServerQueryCriteria;
import com.mh.mock.mapper.NacosMockServerMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
* @date 2024-06-25
**/
@Service
@RequiredArgsConstructor
public class NacosMockServerServiceImpl extends ServiceImpl<NacosMockServerMapper, NacosMockServer> implements NacosMockServerService {

    private final NacosMockServerMapper nacosMockServerMapper;

    @Override
    public PageResult<NacosMockServer> queryAll(NacosMockServerQueryCriteria criteria, Page<Object> page){
        return PageUtil.toPage(nacosMockServerMapper.findAll(criteria, page));
    }

    @Override
    public List<NacosMockServer> queryAll(NacosMockServerQueryCriteria criteria){
        return nacosMockServerMapper.findAll(criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(NacosMockServer resources) {
        save(resources);
    }


    @Autowired
    private HeartbeatManager heartbeatManager;
    @Override
    public void activateMockService(NacosMockServer resources) {
        // 激活mock服务
        // 添加任务 激活
        HeartbeatManager manager = new HeartbeatManager();
        HeartbeatManager.HeartbeatTask task1 = new HeartbeatManager.HeartbeatTask(resources.getNacosUrl(),resources.getServiceName(), resources.getNamespaceId(), resources.getMockServerIp(), resources.getMockServerPort());
        manager.addTask(task1);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(NacosMockServer resources) {
        NacosMockServer nacosMockServer = getById(resources.getId());
        nacosMockServer.copy(resources);
        saveOrUpdate(nacosMockServer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Long> ids) {
        removeBatchByIds(ids);
    }

    @Override
    public void download(List<NacosMockServer> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (NacosMockServer nacosMockServer : all) {
//            Map<String,Object> map = new LinkedHashMap<>();
//            map.put("服务名", nacosMockServer.getServiceName());
//            map.put("nacos地址", nacosMockServer.getNacosUrl());
//            map.put("nacos名称", nacosMockServer.getNacosName());
//            map.put("namespaces_id", nacosMockServer.getNamespacesId());
//            map.put("mockser的id", nacosMockServer.getMockServerId());
//            map.put("mockserver地址", nacosMockServer.getMockServerUrl());
//            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}