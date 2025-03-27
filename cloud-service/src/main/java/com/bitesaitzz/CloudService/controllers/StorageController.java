package com.bitesaitzz.CloudService.controllers;


import com.bitesaitzz.CloudService.services.CloudFileService;
import com.bitesaitzz.CloudService.services.CloudService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/storage")
@AllArgsConstructor
public class StorageController {

    private final CloudService cloudService;
    private final CloudFileService cloudFileService;

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) throws IOException {
        return cloudService.downloadFile(fileId);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        cloudFileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{cloudId}/add")
    public ResponseEntity<Void> addFile(@PathVariable Long cloudId, @RequestParam("files") MultipartFile[] files) throws IOException {
        cloudService.addFiles(cloudId, files);
        return ResponseEntity.noContent().build();
    }
}
