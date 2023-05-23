package com.example.fileupload.service;

import com.example.fileupload.FileConfiguration;
import com.example.fileupload.exceptions.FileStorageException;
import com.example.fileupload.exceptions.InvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class FileService {

    private final Path fileStorageLocation;

    @Autowired
    public FileService(FileConfiguration fileConfigurations) {
        this.fileStorageLocation = Paths
                .get(fileConfigurations.getFileStorageDirectory())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.");
        }
    }

    public String storeFile(MultipartFile file) throws FileStorageException {
        if (isInvalid(file)) {
            throw new InvalidFileException("Invalid file uploaded");
        }
        try {
            String filenameClean = StringUtils.cleanPath(file.getOriginalFilename());
            Path fileTargetLocation = fileStorageLocation.resolve(filenameClean);
            Files.copy(file.getInputStream(), fileTargetLocation);

            return buildFileDownloadUri(filenameClean);
        } catch(FileAlreadyExistsException exception) {
            throw new InvalidFileException("An exception occurred: the file already exists");
        } catch (Exception exception) {
            log.error("An exception occurred while uploading file: {}", exception.getMessage());
            throw new FileStorageException(exception.getMessage());
        }
    }

    private boolean isInvalid(MultipartFile file) {
        return file == null
                || file.getOriginalFilename() == null
                || file.getOriginalFilename().isEmpty();
    }

    private String buildFileDownloadUri(String filename) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/downloadFile/")
                .path(filename)
                .toUriString();
    }
}
