package com.mh.mock.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class NginxConfigService {

    @Value("${docker.ng.config}")
    private String nginxConfigPath;

    public String readConfig() throws IOException {
        Path path = Paths.get(nginxConfigPath);
        return new String(Files.readAllBytes(path));
    }

    public void writeConfig(String content) throws IOException {
        Path path = Paths.get(nginxConfigPath);
        Files.write(path, content.getBytes());
    }
}
