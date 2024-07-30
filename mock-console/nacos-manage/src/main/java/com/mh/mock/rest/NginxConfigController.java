package com.mh.mock.rest;

import com.mh.mock.service.DockerService;
import com.mh.mock.service.NginxConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/nginx")
public class NginxConfigController {

    @Autowired
    private NginxConfigService nginxConfigService;

    @Autowired
    private DockerService dockerService;

    @Value("${docker.ng.name}")
    private String nginxName;

    @GetMapping
    public ResponseEntity<String> getConfig() {
        try {
            String config = nginxConfigService.readConfig();
            return ResponseEntity.ok(config);
        } catch ( IOException e) {
            return ResponseEntity.status(500).body("Error reading config: " + e.getMessage());
        }
    }
    @PostMapping
    public ResponseEntity<String> saveConfig(@RequestBody String config) {
        try {
            nginxConfigService.writeConfig(config);
//            dockerService.restartContainer("mock-nginx");
            return ResponseEntity.ok("Config saved");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error saving config: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Error restarting Nginx: " + e.getMessage());
        }
    }

    @PostMapping("/restart")
    public ResponseEntity<String> restartNginx() {
        try {
            dockerService.restartContainer(nginxName);
            return ResponseEntity.ok("Nginx restarted");
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Error restarting Nginx: " + e.getMessage());
        }
    }
}
