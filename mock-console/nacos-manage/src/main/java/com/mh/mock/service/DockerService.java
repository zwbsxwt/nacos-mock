package com.mh.mock.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class DockerService {

    private final DockerClient dockerClient;

    public DockerService() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost("unix:///var/run/docker.sock")
            .build();

        this.dockerClient = DockerClientBuilder.getInstance(config).build();
    }

    public void restartContainer(String containerName) {
        String containerId = dockerClient.listContainersCmd()
            .withNameFilter(Collections.singleton(containerName))
            .exec()
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Container not found"))
            .getId();

        dockerClient.restartContainerCmd(containerId).exec();
    }

    public InspectContainerResponse inspectContainer(String containerName) {
        String containerId = dockerClient.listContainersCmd()
            .withNameFilter(Collections.singleton(containerName))
            .exec()
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Container not found"))
            .getId();

        return dockerClient.inspectContainerCmd(containerId).exec();
    }
}
