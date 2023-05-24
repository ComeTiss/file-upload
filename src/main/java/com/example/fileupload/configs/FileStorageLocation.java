package com.example.fileupload.configs;

import com.example.fileupload.exceptions.FileStorageException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileStorageLocation {

    @Getter
    private final Path path;

    @Autowired
    public FileStorageLocation(FileConfiguration fileConfiguration) {
        String fileStorageDirectory = fileConfiguration.getFileStorageDirectory();
        this.path = Paths.get(fileStorageDirectory).toAbsolutePath().normalize();

        try {
            Files.createDirectories(path);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.");
        }
    }
}
