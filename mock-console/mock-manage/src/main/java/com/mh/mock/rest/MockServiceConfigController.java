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
package com.mh.mock.rest;

import me.zhengjie.annotation.Log;
import com.mh.mock.domain.MockServiceConfig;
import com.mh.mock.service.MockServiceConfigService;
import com.mh.mock.domain.vo.MockServiceConfigQueryCriteria;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.zhengjie.utils.PageResult;

/**
* @author max
* @date 2024-07-29
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "mock服务基础配置管理")
@RequestMapping("/api/mockServiceConfig")
public class MockServiceConfigController {

    private final MockServiceConfigService mockServiceConfigService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('mockServiceConfig:list')")
    public void exportMockServiceConfig(HttpServletResponse response, MockServiceConfigQueryCriteria criteria) throws IOException {
        mockServiceConfigService.download(mockServiceConfigService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询mock服务基础配置")
    @ApiOperation("查询mock服务基础配置")
    @PreAuthorize("@el.check('mockServiceConfig:list')")
    public ResponseEntity<PageResult<MockServiceConfig>> queryMockServiceConfig(MockServiceConfigQueryCriteria criteria, Page<Object> page){
        return new ResponseEntity<>(mockServiceConfigService.queryAll(criteria,page),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增mock服务基础配置")
    @ApiOperation("新增mock服务基础配置")
    @PreAuthorize("@el.check('mockServiceConfig:add')")
    public ResponseEntity<Object> createMockServiceConfig(@Validated @RequestBody MockServiceConfig resources){
        mockServiceConfigService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改mock服务基础配置")
    @ApiOperation("修改mock服务基础配置")
    @PreAuthorize("@el.check('mockServiceConfig:edit')")
    public ResponseEntity<Object> updateMockServiceConfig(@Validated @RequestBody MockServiceConfig resources){
        mockServiceConfigService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除mock服务基础配置")
    @ApiOperation("删除mock服务基础配置")
    @PreAuthorize("@el.check('mockServiceConfig:del')")
    public ResponseEntity<Object> deleteMockServiceConfig(@RequestBody List<Long> ids) {
        mockServiceConfigService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}