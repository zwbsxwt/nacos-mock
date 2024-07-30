package com.mh.mock.rest;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.googlecode.aviator.runtime.type.AviatorType;
import com.mh.mock.entity.MockEndpoint;
import com.mh.mock.entity.MockServiceConfig;
import com.mh.mock.mapper.MockEndpointMapper;
import com.mh.mock.mapper.MockServiceConfigMapper;
import com.mh.mock.utils.RequestParamsUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

@Slf4j
@RestController
public class ApiController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MockEndpointMapper mockEndpointMapper;

    @Autowired
    private MockServiceConfigMapper mockServiceConfigMapper;

    @Autowired
    private RequestParamsUtil requestParamsUtil;

    @Autowired
    private Configuration freemarkerConfig;

    @RequestMapping("/**")
    public ResponseEntity<?> handleRequest(HttpServletRequest request,
                                           @RequestBody(required = false) String body) throws IOException, TemplateException {
        System.out.println("body = " + body);

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            String header = request.getHeader(name);
            System.out.print("name = " + name);
            System.out.println(" header = " + header);
        }

        String url = request.getRequestURI(). toString();
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        Map<String, Object> params = requestParamsUtil.getRequestParams(request, body, method);

        System.out.println("params = " + JSONObject.toJSONString(params));
        // 从URL中提取服务名和实际路径
        String[] urlParts = url.split("/");
        if (urlParts.length < 2) {
            return new ResponseEntity<>("Invalid service name", HttpStatus.BAD_REQUEST);
        }
        String serviceName = urlParts[1];
        String actualPath = "/" + String.join("/", Arrays.copyOfRange(urlParts, 2, urlParts.length));

        // 根据服务名查询服务配置
        MockServiceConfig mockServiceConfig = mockServiceConfigMapper.selectOne(new QueryWrapper<MockServiceConfig>().eq("service_name", serviceName));
        if (mockServiceConfig == null) {
            return new ResponseEntity<>("Service not found", HttpStatus.NOT_FOUND);
        }

        List<MockEndpoint> mockEndpoints = mockEndpointMapper.findByServiceNameAndMethod(mockServiceConfig.getServiceName(), method.name());
        MockEndpoint mockEndpoint = matchEndpoint(mockEndpoints, actualPath, params);
        if (mockEndpoint != null) {
            Map<String, String> pathVariables = extractPathVariables(mockEndpoint.getUrlPattern(), actualPath);
            String response = generateDynamicResponse(mockEndpoint.getResponseTemplate(), pathVariables, params);

            HttpHeaders headers = new HttpHeaders();
            if (mockEndpoint.getResponseHeader() != null) {
                Map<String, String> responseHeadersMap = new ObjectMapper().readValue(mockEndpoint.getResponseHeader(), HashMap.class);
                responseHeadersMap.forEach(headers::add);
            } else {
                headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            }

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }


        // 如果没有注册的Mock接口，则进行转发
        if (shouldForward(mockServiceConfig) && !StringUtils.isEmpty(mockServiceConfig.getForwardUrl())) {
            String forwardUrl = mockServiceConfig.getForwardUrl() + actualPath;
            HttpHeaders headers = new HttpHeaders();
            Collections.list(request.getHeaderNames()).forEach(headerName -> headers.add(headerName, request.getHeader(headerName)));
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            log.info("###########################################################");
            log.info("forwardUrl - {}" ,forwardUrl);
            ResponseEntity<String> forwardResponse = restTemplate.exchange(forwardUrl, method, entity, String.class);
            String res = forwardResponse.getBody();
            log.info("forwardResponse - {}" ,res);
//            HttpHeaders responseHeaders = new HttpHeaders();
//            forwardResponse.getHeaders().forEach((key, value) -> responseHeaders.put(key, value));
            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
            return new ResponseEntity<>(res, respHeaders, HttpStatus.OK);
        }


        // 默认响应
        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        response.put("method", method.name());
        response.put("msg", "No match! No forward!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private MockEndpoint matchEndpoint(List<MockEndpoint> endpoints, String actualPath, Map<String, Object> postParams) {
        for (MockEndpoint endpoint : endpoints) {
            String pattern = endpoint.getUrlPattern().replaceAll("\\{[^/]+\\}", "[^/]+");
            if (actualPath.matches(pattern) && evaluateExpression(endpoint.getExpression(), postParams)) {
                return endpoint;
            }
        }
        return null;
    }

    private boolean evaluateExpression(String expression, Map<String, Object> env) {
        if (expression == null || expression.isEmpty()) {
            return true;
        }

        try {
            Expression compiledExp = AviatorEvaluator.compile(expression);
            return (Boolean) compiledExp.execute(env);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Map<String, String> extractPathVariables(String pattern, String path) {
        Map<String, String> pathVariables = new HashMap<>();
        String[] patternParts = pattern.split("/");
        String[] pathParts = path.split("/");

        for (int i = 0; i < patternParts.length; i++) {
            if (patternParts[i].startsWith("{") && patternParts[i].endsWith("}")) {
                String key = patternParts[i].substring(1, patternParts[i].length() - 1);
                pathVariables.put(key, pathParts[i]);
            }
        }
        return pathVariables;
    }

    private String generateDynamicResponse(String responseTemplate, Map<String, String> pathVariables, Map<String, Object> params) throws IOException, TemplateException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put("path", pathVariables);
        model.put("get", params.get("get"));
        model.put("post", params.get("post"));

        Template template = new Template("responseTemplate", responseTemplate, freemarkerConfig);
        StringWriter out = new StringWriter();
        template.process(model, out);
        return out.toString();
    }

    private boolean shouldForward(MockServiceConfig MockServiceConfig) {
        // 自定义逻辑判断是否需要转发
        return true;
    }


    static {
        AviatorEvaluator.addFunction(new XmlToJsonFunction());
    }

    static class XmlToJsonFunction extends AbstractFunction {

        @Override
        public AviatorObject  call(Map<String, Object> env, AviatorObject arg1) {
            String xml = arg1.stringValue(env);
            Map<String, Object> resultMap = JSONUtil.xmlToJson(xml).getRaw();
            System.out.println("resultMap = " + JSONObject.toJSONString(resultMap));
            return new AviatorRuntimeJavaType(resultMap);
        }

        @Override
        public String getName() {
            return "xmlToJson";
        }
    }

    public static void main(String[] args) throws JsonProcessingException {

//        AviatorEvaluatorInstance instance = AviatorEvaluator.newInstance();
//
//
//        String json ="{\"post\":{},\"get\":{\"data\":\"<ap>\\n\\t<head>\\n\\t\\t<tr_code>310303</tr_code>\\n\\t\\t<corp_no>0020239537</corp_no>\\n\\t\\t<user_no>00002</user_no>\\n\\t\\t<req_no>20240705304000010196</req_no>\\n\\t\\t<tr_acdt>20240705</tr_acdt>\\n\\t\\t<tr_time>000009</tr_time>\\n\\t\\t<atom_tr_count></atom_tr_count>\\n\\t\\t<channel>0</channel>\\n\\t\\t<reserved></reserved>\\n\\t\\t<sign>1</sign></head>\\n\\t<body>\\n\\t\\t<acno>310066771013008315930</acno>\\n\\t\\t<offset>0</offset>\\n\\t</body>\\n</ap>\"}}";
//        Map<String, Object> env = com.alibaba.fastjson.JSONObject.parseObject(json);
//
//        // 修改表达式以返回 tr_code 的值
//        String expression = "let xmlData = get.data;" +
//                "let jsonMap = xmlToJson(xmlData);" +
//                "jsonMap.ap.head.tr_code";  // 直接返回 tr_code 的值
//
//        Expression compiledExp = instance.compile(expression);
//        Object execute = compiledExp.execute(env);
//        System.out.println("tr_code = " + execute);
//
//        // 再次执行判断表达式
//        expression =
//                "let jsonMap = xmlToJson(get.data);" +
//                "jsonMap.ap.head.tr_code == 310303";
//        System.out.println(expression);
//        compiledExp = instance.compile(expression);
//        execute = compiledExp.execute(env);
//        System.out.println("execute = " + execute);


    }


}
