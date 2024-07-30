package com.mh.mock.service.manager;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class NacosInstanceManager {

    public static void main(String[] args) {
        String baseUrl = "http://47.97.40.19:8848";

        // 构建请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("ip", "127.0.0.1");
        params.put("port", 1080);
        params.put("serviceName", "nacos-mock");
        params.put("namespaceId", "bae98fd5-aa76-427b-a5fc-c32f81befa38");


        params.put("healthy", true);
        params.put("enabled", true);
        params.put("metadata", "{\"preserved.register.source\":\"MOCK-SERVER\"}");

        // 调用封装的方法
        String response = sendNacosInstanceRequest(baseUrl, params);

        // 打印响应
        System.out.println(response);

//
//        String baseUrl = "http://47.97.40.19:8848";
//
//        // 构建请求参数
//        Map<String, Object> params = new HashMap<>();
//        params.put("serviceName", "nacos-mock");
//        params.put("namespaceId", "bae98fd5-aa76-427b-a5fc-c32f81befa38");
//        params.put("ip", "127.0.0.1");
//        params.put("port", 1080);
//
//        // 调用封装的方法
//        String response = sendNacosInstanceHeartbeat(baseUrl, params);
//
//        // 打印响应
//        System.out.println(response);
    }

    public static String sendNacosInstanceRequest(String baseUrl, Map<String, Object> params) {
        String url = baseUrl + "/nacos/v1/ns/instance";

        // 发送 POST 请求
        HttpResponse response = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .form(params)
                .execute();

        // 返回响应体
        return response.body();
    }


    public static String sendNacosInstanceHeartbeat(String baseUrl, Map<String, Object> params) {
        String url = baseUrl + "/nacos/v1/ns/instance/beat";

        // 发送 PUT 请求
        HttpResponse response = HttpRequest.put(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .form(params)
                .execute();

        // 返回响应体
        return response.body();
    }

}
