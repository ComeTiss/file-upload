package com.example.fileupload;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileConfiguration {

    @Getter
    @Value("${file.upload.dir}")
    private String fileStorageDirectory;
}
