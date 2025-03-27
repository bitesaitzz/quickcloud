package com.bitesaitzz.CloudService.repositories;


import com.bitesaitzz.CloudService.models.Cloud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CloudRepository extends JpaRepository<Cloud, Long> {
    Cloud findByCode(String code);
}
