package com.example.fileupload.service;

import com.example.fileupload.configs.FileStorageLocation;
import com.example.fileupload.dto.FileMetadata;
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

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class FileService {

    @Autowired
    private FileStorageLocation fileStorageLocation;

    public FileMetadata storeFile(MultipartFile file) throws FileStorageException {
        if (isInvalid(file)) {
            throw new InvalidFileException("Invalid file uploaded");
        }
        try {
            String filenameClean = StringUtils.cleanPath(file.getOriginalFilename());
            Path fileTargetPath = buildFileTargetPath(filenameClean);
            Files.copy(file.getInputStream(), fileTargetPath);
            return new FileMetadata(file);
        } catch(FileAlreadyExistsException exception) {
            throw new InvalidFileException("An exception occurred while uploading file: the file already exists");
        } catch (Exception exception) {
            log.error("An exception occurred while uploading file: {}", exception.getMessage());
            throw new FileStorageException(exception.getMessage());
        }
    }

    public List<FileMetadata> storeFiles(MultipartFile[] files) {
        List<CompletableFuture<FileMetadata>> storeFileFutures = new ArrayList<>();
        for (MultipartFile file : files) {
            storeFileFutures.add(CompletableFuture.supplyAsync(() -> storeFile(file)));
        }
        try {
           return CompletableFuture.allOf(storeFileFutures.toArray(new CompletableFuture[storeFileFutures.size()]))
                     .thenApply(future -> {
                         return storeFileFutures
                             .stream()
                             .map(CompletableFuture::join)
                             .toList();
                     })
                    .get();
        } catch (InterruptedException | ExecutionException exception) {
            log.error("An exception occurred while uploading files: {}", exception.getMessage());
            throw new FileStorageException("An exception occurred while uploading files");
        }
    }

    public Optional<Resource> loadFile(String filename) throws FileStorageException {
        Path filePath = buildFileTargetPath(filename);
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


    public List<FileMetadata> getFiles() {
        File fileStorage = new File(fileStoragePath().toString());

        return Arrays.stream(fileStorage.listFiles())
                .map(FileMetadata::new)
                .toList();
    }

    private boolean isInvalid(MultipartFile file) {
        return file == null
                || file.getOriginalFilename() == null
                || file.getOriginalFilename().isEmpty();
    }

    private Path fileStoragePath() {
        return fileStorageLocation.getPath();
    }

    private Path buildFileTargetPath(String filename) {
        return fileStoragePath().resolve(filename).normalize();
    }
}
