package com.bitesaitzz.CloudService.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CloudFile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cloud_file_seq")
    @SequenceGenerator(name="cloud_file_seq", sequenceName = "cloud_file_seq", allocationSize = 1)
    private Long id;

    private String name;

    private String contentType;

    private long size;

    @ManyToOne
    @JoinColumn(name = "cloud_id")
    private Cloud cloud;
}
