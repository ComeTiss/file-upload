package com.example.fileupload.dto;

public record FileUploadResponse(
        String filename,
        String downloadUri,
        String contentType,
        long size) {
}
