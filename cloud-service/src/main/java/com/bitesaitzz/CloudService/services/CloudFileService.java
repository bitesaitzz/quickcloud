package com.bitesaitzz.CloudService.services;


import com.bitesaitzz.CloudService.models.Cloud;
import com.bitesaitzz.CloudService.models.CloudFile;
import com.bitesaitzz.CloudService.repositories.CloudFileRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CloudFileService {
    private final CloudFileRepository cloudFileRepository;

    public void deleteFile(Long id) {
        cloudFileRepository.deleteById(id);
    }

    public boolean existsByNameAndCloud(String name, Cloud cloud) {
        return cloudFileRepository.existsByNameAndCloud(name, cloud);
    }

    public void addFiles(Cloud savedCloud, MultipartFile[] files) throws IOException {

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
    }

    @Cacheable(value = "cloudFiles", key = "#cloud.id", unless = "#result.size() == 0")
    public List<CloudFile> getCloudFilesWithoutUUID(Cloud cloud) {
        List<CloudFile> cloudFiles = cloudFileRepository.findByCloud(cloud);
        for(CloudFile cloudFile : cloudFiles){
            String originalName = cloudFile.getName().substring(cloudFile.getName().indexOf('_') + 1);
            cloudFile.setName(originalName);
        }
        return cloudFiles;
    }


    @Transactional
    public void deleteFilesInCloud(Cloud cloud){
        List<CloudFile> cloudFiles = cloudFileRepository.findByCloud(cloud);
        for(CloudFile cloudFile : cloudFiles){
            Path path = Paths.get("uploads/" + cloudFile.getName());
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cloudFileRepository.deleteByCloud(cloud);
    }


}
