package com.example.fileupload.controllers;

import com.example.fileupload.dto.ErrorResponse;
import com.example.fileupload.exceptions.FileNotFoundException;
import com.example.fileupload.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/download")
public class FileDownloadController {

    @Autowired
    private FileService fileService;

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> uploadFile(@PathVariable String filename) {
        log.info("Request to download file: {}", filename);
        Optional<Resource> resource = fileService.loadFileAsResource(filename);
        if (resource.isEmpty()) {
            throw new FileNotFoundException("File not found");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.get().getFilename() + "\"")
                .body(resource.get());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FileNotFoundException.class)
    public ErrorResponse handleFileNotFoundException(FileNotFoundException fileException) {
        return new ErrorResponse(fileException.getMessage(), fileException.getType());
    }
}
