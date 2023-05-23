package com.example.fileupload.controllers;

import com.example.fileupload.dto.ErrorResponse;
import com.example.fileupload.dto.FileUploadResponse;
import com.example.fileupload.exceptions.FileStorageException;
import com.example.fileupload.exceptions.InvalidFileException;
import com.example.fileupload.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private FileService fileService;

    @PostMapping
    public FileUploadResponse uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("Uploading file: {}", file.getOriginalFilename());
        String fileDownloadUri = fileService.storeFile(file);

        return new FileUploadResponse(
                file.getOriginalFilename(),
                fileDownloadUri,
                file.getContentType(),
                file.getSize()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(FileStorageException.class)
    public ErrorResponse handleFileStorageException(FileStorageException fileException) {
        return new ErrorResponse(fileException.getMessage(), fileException.getType());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidFileException.class)
    public ErrorResponse handleInvalidException(InvalidFileException fileException) {
        return new ErrorResponse(fileException.getMessage(), fileException.getType());
    }
}
