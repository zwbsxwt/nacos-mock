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
import com.mh.mock.domain.MockEndpoint;
import com.mh.mock.service.MockEndpointService;
import com.mh.mock.domain.vo.MockEndpointQueryCriteria;
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
* @author Max
* @date 2024-07-29
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "mock-manage管理")
@RequestMapping("/api/mockEndpoint")
public class MockEndpointController {

    private final MockEndpointService mockEndpointService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('mockEndpoint:list')")
    public void exportMockEndpoint(HttpServletResponse response, MockEndpointQueryCriteria criteria) throws IOException {
        mockEndpointService.download(mockEndpointService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询mock-manage")
    @ApiOperation("查询mock-manage")
    @PreAuthorize("@el.check('mockEndpoint:list')")
    public ResponseEntity<PageResult<MockEndpoint>> queryMockEndpoint(MockEndpointQueryCriteria criteria, Page<Object> page){
        return new ResponseEntity<>(mockEndpointService.queryAll(criteria,page),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增mock-manage")
    @ApiOperation("新增mock-manage")
    @PreAuthorize("@el.check('mockEndpoint:add')")
    public ResponseEntity<Object> createMockEndpoint(@Validated @RequestBody MockEndpoint resources){
        mockEndpointService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改mock-manage")
    @ApiOperation("修改mock-manage")
    @PreAuthorize("@el.check('mockEndpoint:edit')")
    public ResponseEntity<Object> updateMockEndpoint(@Validated @RequestBody MockEndpoint resources){
        mockEndpointService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除mock-manage")
    @ApiOperation("删除mock-manage")
    @PreAuthorize("@el.check('mockEndpoint:del')")
    public ResponseEntity<Object> deleteMockEndpoint(@RequestBody List<Long> ids) {
        mockEndpointService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}