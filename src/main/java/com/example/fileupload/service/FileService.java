package com.example.fileupload.service;

import com.example.fileupload.FileConfiguration;
import com.example.fileupload.exceptions.FileNotFoundException;
import com.example.fileupload.exceptions.FileStorageException;
import com.example.fileupload.exceptions.InvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    public List<FileMetadata> getAll() {
        File fileStorage = new File(fileStorageLocation.toString());
        File[] files = fileStorage.listFiles();

        return Arrays.stream(files)
                .map(file -> new FileMetadata(
                    file.getName(),
                    buildFileDownloadUri(file.getName()),
                    URLConnection.guessContentTypeFromName(file.getName()),
                    file.length()))
                .toList();
    }


    public Optional<Resource> loadFileAsResource(String filename) throws FileStorageException {
        Path filePath = fileStorageLocation.resolve(filename).normalize();
        try {
            Resource resource = new UrlResource(filePath.toUri());
            return resource.exists()
                    ? Optional.of(resource)
                    : Optional.empty();

        } catch (MalformedURLException exception) {
            log.error("An exception occurred while loading file {}: {}", filename, exception.getMessage());
            throw new FileNotFoundException("File not found, invalid filename provided");
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
