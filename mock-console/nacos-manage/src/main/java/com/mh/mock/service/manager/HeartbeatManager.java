package com.mh.mock.service.manager;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
@Component
public class HeartbeatManager {
    private final Set<HeartbeatTask> tasks = Collections.synchronizedSet(new HashSet<>());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public HeartbeatManager() {
        System.out.println("HeartbeatManager init ....");
        scheduler.scheduleAtFixedRate(this::performHeartbeat, 0, 5, TimeUnit.SECONDS);
    }

    public void addTask(HeartbeatTask task) {
        tasks.add(task);
    }

    public void removeTask(HeartbeatTask task) {
        tasks.remove(task);
    }

    public Set<HeartbeatTask> getTasks() {
        return new HashSet<>(tasks);
    }

    private void performHeartbeat() {
        for (HeartbeatTask task : tasks) {
            if (task.isActive()) {
                try {
                    Map<String, Object> params = new HashMap<>();
                    params.put("serviceName", task.getServiceName());
                    params.put("namespaceId", task.getNamespaceId());
                    params.put("ip", task.getIp());
                    params.put("port", task.getPort());
                    String heartbeatStr = NacosInstanceManager.sendNacosInstanceHeartbeat(task.getNacosUrl(), params);
                    Heartbeat heartbeat = JSONObject.parseObject(heartbeatStr, Heartbeat.class);

                    if(!heartbeat.lightBeatEnabled){
                        System.out.println("Service not found, re-registering: " + task.getUniqueServiceIdentifier());
                        // 上线服务
                        params.put("healthy", true);
                        params.put("enabled", true);
                        params.put("metadata", "{\"preserved.register.source\":\"MOCK-SERVER\"}");
                        NacosInstanceManager.sendNacosInstanceRequest(task.getNacosUrl(),params);
                    }else {
                        System.out.println("Heartbeat for task: " + task.getUniqueServiceIdentifier());
                    }

                } catch (Exception e) {
                    System.err.println("Error during heartbeat for task: " + task.getUniqueServiceIdentifier());
                    e.printStackTrace();
                }
            }
        }
    }

    @Data
    public static class Heartbeat {
        private long clientBeatInterval;
        private long code;
        private Boolean lightBeatEnabled = Boolean.FALSE;
    }


    @Data
    public static class HeartbeatTask {
        private final String serviceName;
        private final String namespaceId;
        private final String nacosUrl;
        private final String ip;
        private final String port;
        private boolean active;

        public HeartbeatTask(String nacosUrl, String serviceName, String namespaceId, String ip, String port) {
            this.serviceName = serviceName;
            this.namespaceId = namespaceId;
            this.nacosUrl = nacosUrl;
            this.ip = ip;
            this.port = port;
            this.active = true;
        }


        public String getUniqueServiceIdentifier() {
            return serviceName + "-" + namespaceId;
        }
    }


    public static void main(String[] args) {
        HeartbeatManager manager = new HeartbeatManager();

        HeartbeatTask task1 = new HeartbeatTask("http://47.97.40.19:8848","nacos-mock", "bae98fd5-aa76-427b-a5fc-c32f81befa38", "127.0.0.1", "1080");
        manager.addTask(task1);

        // Simulate some changes
        try {
            Thread.sleep(3000000); // Wait for a few heartbeats
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Shutdown the scheduler gracefully
        manager.scheduler.shutdown();
    }

}
