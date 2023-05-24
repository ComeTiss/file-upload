package com.example.fileupload.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLConnection;

@Getter
public class FileMetadata {
        private final String filename;
        private final String downloadUri;
        private final String contentType;
        private final long size;

    public FileMetadata(MultipartFile file)  {
        this.filename = file.getName();
        this.downloadUri = buildFileDownloadUri(file.getName());
        this.contentType = file.getContentType();
        this.size = file.getSize();
    }

    public FileMetadata(File file)  {
        this.filename = file.getName();
        this.downloadUri = buildFileDownloadUri(file.getName());
        this.contentType = URLConnection.guessContentTypeFromName(file.getName());
        this.size = file.length();
    }

    private String buildFileDownloadUri(String filename) {
        return "/api/download/" + filename;
    }
}
