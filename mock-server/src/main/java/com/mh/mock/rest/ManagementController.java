//package com.mh.mock.rest;
//
//import com.mh.mock.entity.MockEndpoint;
//import com.mh.mock.entity.MockServiceConfig;
//import com.mh.mock.mapper.MockEndpointMapper;
//import com.mh.mock.mapper.MockServiceConfigMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.io.IOException;
//import java.util.Map;
//import java.util.TreeMap;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/management")
//public class ManagementController {
//
//    @Autowired
//    private MockEndpointMapper mockEndpointMapper;
//
//    @Autowired
//    private MockServiceConfigMapper mockServiceMapper;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @PutMapping("/register")
//    public String registerMockEndpoint(@RequestBody MockEndpoint mockEndpoint) throws IOException {
//        // 标准化参数
//        mockEndpointMapper.insert(mockEndpoint);
//        return "Mock endpoint registered successfully!";
//    }
//
//    @PutMapping("/registerService")
//    public String registerService(@RequestBody MockServiceConfig mockService) {
//        mockServiceMapper.insert(mockService);
//        return "Service registered successfully!";
//    }
//
//    private String standardizeParams(String params) throws IOException {
//        if (params == null || params.isEmpty()) {
//            return "";
//        }
//        Map<String, String> paramMap = objectMapper.readValue(params, TreeMap.class); // TreeMap自动按键排序
//        return paramMap.entrySet().stream()
//                .map(entry -> entry.getKey() + "=" + entry.getValue())
//                .collect(Collectors.joining("&"));
//    }
//}
