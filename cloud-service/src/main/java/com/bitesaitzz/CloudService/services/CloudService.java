package com.bitesaitzz.CloudService.services;


import com.bitesaitzz.CloudService.models.Cloud;
import com.bitesaitzz.CloudService.models.CloudFile;
import com.bitesaitzz.CloudService.repositories.CloudFileRepository;
import com.bitesaitzz.CloudService.repositories.CloudRepository;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

public class CloudService {
    private final CloudRepository cloudRepository;
    private final CloudFileRepository cloudFileRepository;
    private final List<String> words;
    private final CloudFileService cloudFileService;
    private final Logger logger;
    private final RedisTemplate<String, Object> redisTemplate;

    public CloudService(CloudRepository cloudRepository, CloudFileRepository cloudFileRepository, List<String> words, CloudFileService cloudFileService, Logger logger, RedisTemplate<String, Object> redisTemplate, RedisTemplate<String, Object> redisTemplate1) throws IOException {
        this.cloudRepository = cloudRepository;
        this.cloudFileRepository = cloudFileRepository;
        this.cloudFileService = cloudFileService;
        this.logger = logger;
        this.redisTemplate = redisTemplate1;
        this.words = loadWords();
    }

    private List<String> loadWords() throws IOException {
        Resource resource = new ClassPathResource("cities.txt");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    public Cloud createCloud(Cloud cloud, MultipartFile[] files) throws Exception {
        cloud.setCreatedAt(LocalDateTime.now());
        String code = generateRandomCode();
        int i = 100;
        while(cloudRepository.findByCode(code) != null && i > 0) {
            code = generateRandomCode();
            i--;
        }
        cloud.setCode(code);
        Cloud savedCloud = cloudRepository.save(cloud);
        List<CloudFile> cloudFiles = new ArrayList<>();
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        for (MultipartFile multipartFile : files) {
            if (!multipartFile.isEmpty()) {
                String filename = multipartFile.getOriginalFilename();
                String specialFilename = UUID.randomUUID().toString() + "_" + filename;
                String contentType = multipartFile.getContentType();
                long size = multipartFile.getSize();

                Path path = uploadDir.resolve(specialFilename);
                Files.write(path, multipartFile.getBytes());

                CloudFile cloudFile = new CloudFile();
                cloudFile.setName(specialFilename);
                cloudFile.setContentType(contentType);
                cloudFile.setSize(size);
                cloudFile.setCloud(savedCloud);

                cloudFiles.add(cloudFile);
            }
        }
        cloudFileRepository.saveAll(cloudFiles);
        logger.info("Cloud created: " + savedCloud.getId() + " " + savedCloud.getCode());
        cacheCloud(savedCloud);

        return savedCloud;
    }
    public List<Cloud> listClouds() {
        return cloudRepository.findAll();
    }



    @Cacheable(value = "clouds", key = "#code", unless = "#result == null")
    public Cloud getCloudByCode(String code) {
        return cloudRepository.findByCode(code);
    }

    public List<CloudFile> getCloudFiles(Cloud cloud) {
        return cloudFileRepository.findByCloud(cloud);
    }



    public ResponseEntity<Resource> downloadFile(Long fileId) throws IOException {
        CloudFile cloudFile = cloudFileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found with id " + fileId));

        Path filePath = Paths.get("uploads").resolve(cloudFile.getName()).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new FileNotFoundException("File not found " + cloudFile.getName());
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(cloudFile.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + cloudFile.getName() + "\"")
                .body(resource);
    }

    private String generateRandomCode() {
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }


    public Cloud getCloudById(Long id) {
        return cloudRepository.findById(id).orElse(null);
    }

    public void updateCloud(Long id, Cloud updatedCloud, MultipartFile[] files) {
        Cloud cloud = getCloudById(id);
        cloud.setName(updatedCloud.getName());
        cloud.setCode(updatedCloud.getCode());
        cloud.setDescription(updatedCloud.getDescription());

        // Загрузка файлов
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                CloudFile cloudFile = new CloudFile();
                cloudFile.setName(file.getOriginalFilename());
                cloudFile.setContentType(file.getContentType());
                cloudFile.setSize(file.getSize());
                cloudFile.setCloud(cloud);
                cloudFileRepository.save(cloudFile);
            }
        }

        cloudRepository.save(cloud);

    }

    @Transactional
    public void deleteCloud(Long id) {
        cloudFileService.deleteFilesInCloud(getCloudById(id));
        cloudRepository.deleteById(id);
    }

    public void addFiles(Long cloudId, MultipartFile[] files) throws IOException {
        Cloud cloud = getCloudById(cloudId);
        cloudFileService.addFiles(cloud, files);

    }

    @Async
    public void cacheCloud(Cloud cloud){
        redisTemplate.opsForValue().set("clouds::" + cloud.getCode(), cloud);
        List<CloudFile> cloudFiles = cloudFileService.getCloudFilesWithoutUUID(cloud);
        redisTemplate.opsForValue().set("cloudFiles::" + cloud.getId(), cloudFiles);
    }




}
