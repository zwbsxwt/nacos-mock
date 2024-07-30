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
import com.mh.mock.domain.NacosMockServer;
import com.mh.mock.service.NacosMockServerService;
import com.mh.mock.domain.vo.NacosMockServerQueryCriteria;
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
* @date 2024-06-25
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "nacos-mock管理")
@RequestMapping("/api/nacosMockServer")
public class NacosMockServerController {

    private final NacosMockServerService nacosMockServerService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('nacosMockServer:list')")
    public void exportNacosMockServer(HttpServletResponse response, NacosMockServerQueryCriteria criteria) throws IOException {
        nacosMockServerService.download(nacosMockServerService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询nacos-mock")
    @ApiOperation("查询nacos-mock")
    @PreAuthorize("@el.check('nacosMockServer:list')")
    public ResponseEntity<PageResult<NacosMockServer>> queryNacosMockServer(NacosMockServerQueryCriteria criteria, Page<Object> page){
        return new ResponseEntity<>(nacosMockServerService.queryAll(criteria,page),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增nacos-mock")
    @ApiOperation("新增nacos-mock")
    @PreAuthorize("@el.check('nacosMockServer:add')")
    public ResponseEntity<Object> createNacosMockServer(@Validated @RequestBody NacosMockServer resources){
        nacosMockServerService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }



    @PostMapping("/activateMockService")
    @Log("上线mock服务")
    @ApiOperation("上线mock服务")
    public ResponseEntity<Object> activateMockService(@Validated @RequestBody NacosMockServer resources){
        nacosMockServerService.activateMockService(resources);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PutMapping
    @Log("修改nacos-mock")
    @ApiOperation("修改nacos-mock")
    @PreAuthorize("@el.check('nacosMockServer:edit')")
    public ResponseEntity<Object> updateNacosMockServer(@Validated @RequestBody NacosMockServer resources){
        nacosMockServerService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除nacos-mock")
    @ApiOperation("删除nacos-mock")
    @PreAuthorize("@el.check('nacosMockServer:del')")
    public ResponseEntity<Object> deleteNacosMockServer(@RequestBody List<Long> ids) {
        nacosMockServerService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}