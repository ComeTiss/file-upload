package com.example.fileupload.service;

public record FileMetadata(
        String filename,
        String downloadUri,
        String contentType,
        long size) {
}
