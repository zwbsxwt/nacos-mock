package com.mh.mock.utils;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
public class RequestParamsUtil {

    @Autowired
    private ObjectMapper objectMapper;


    public Map<String, Object> getRequestParams(HttpServletRequest request, String body, HttpMethod method) throws IOException {
//        Map<String, Object> paramsMap1 = new HashMap<>();
//        Map<String, Object> allParams = new TreeMap<>();
//
//        // Get parameters from the query string or form data
//        request.getParameterMap().forEach((key, value) -> allParams.put(key, String.join(",", value)));
//
//        paramsMap1.put("params", allParams);
//
//        System.out.println("paramsMap1 = " + JSONObject.toJSONString(paramsMap1));


        Map<String, Object> paramsMap = new HashMap<>();
        Map<String, Object> getParams = new TreeMap<>();
        Map<String, Object> postParams = new TreeMap<>();

        request.getParameterMap().forEach((key, value) -> getParams.put(key, String.join(",", value)));

        if (method == HttpMethod.POST && body != null&&request.getHeader("content-type").equalsIgnoreCase("application/json")) {
            postParams = objectMapper.readValue(body, TreeMap.class);
        }

        if (method == HttpMethod.POST && body != null&&request.getHeader("content-type").equalsIgnoreCase("application/x-www-form-urlencoded")) {
//            postParams = objectMapper.readValue(body, TreeMap.class);
            try{
                String decode = URLDecoder.decode(body, Charset.defaultCharset());
                if(JSONUtil.isTypeJSON(decode)){
                    postParams = objectMapper.readValue(decode, TreeMap.class);
                }else if(decode.trim().startsWith("<")){ //xml
                    postParams = JSONUtil.xmlToJson(decode);
                }
            }catch (Exception e){
                System.out.println("##############ERR 不是json 也不是xml##################");
                e.printStackTrace();
                // 不是json 也不是xml
            }

        }
//
//        if (method == HttpMethod.GET) {
//            request.getParameterMap().forEach((key, value) -> getParams.put(key, String.join(",", value)));
//        } else if (method == HttpMethod.POST && body != null) {
//            postParams = objectMapper.readValue(body, TreeMap.class);
//        }

        paramsMap.put("get", getParams);
        paramsMap.put("post", postParams);
        return paramsMap;
    }


    public Map<String, String> getParamsMap(HttpServletRequest request, String body) throws IOException {
        Map<String, String> paramsMap = new TreeMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        parameterMap.forEach((key, value) -> paramsMap.put(key, String.join(",", value)));

        if (body != null && !body.isEmpty()) {
            Map<String, Object> requestBody = objectMapper.readValue(body, TreeMap.class);  // TreeMap自动按键排序
            requestBody.forEach((key, value) -> paramsMap.put(key, value.toString()));
        }
        return paramsMap;
    }
}
