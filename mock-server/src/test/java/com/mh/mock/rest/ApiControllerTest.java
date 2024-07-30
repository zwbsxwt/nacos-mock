//package com.mh.mock.rest;
//
//import com.mh.mock.entity.MockEndpoint;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.io.IOException;
//
//
//@SpringBootTest
//class ApiControllerTest {
//
//    @Autowired
//    private ManagementController managementController;
//
//
//    @Test
//    public void test() throws IOException {
//        MockEndpoint mockEndpoint = new MockEndpoint();
//        mockEndpoint.setServiceName("orderService");
//        mockEndpoint.setMethod("POST");
//        mockEndpoint.setUrlPattern("/api/order/{type}/{orderId}"); // 使用路径参数模式
//        mockEndpoint.setExpression("request.featureCode == 'js_new_v2' && request.serviceProviderCode == 'js_6005'"); // 表达式匹配
//        mockEndpoint.setResponseTemplate("Order type ${type} for ${featureCode} by ${serviceProviderCode}. Order ID: ${orderId}. Random number: ${random}. Date: ${date}");
//
//        managementController.registerMockEndpoint(mockEndpoint);
//    }
//
//}