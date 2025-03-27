package com.bitesaitzz.CloudService.repositories;

import com.bitesaitzz.CloudService.models.Cloud;
import com.bitesaitzz.CloudService.models.CloudFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CloudFileRepository extends JpaRepository<CloudFile, Long> {
    List<CloudFile> findByCloud(Cloud cloud);
    boolean existsByNameAndCloud(String name, Cloud cloud);
    void deleteByCloud(Cloud cloud);
}
