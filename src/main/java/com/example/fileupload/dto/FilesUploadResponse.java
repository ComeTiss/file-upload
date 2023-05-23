package com.example.fileupload.dto;

import com.example.fileupload.service.FileMetadata;

import java.util.List;

public record FilesUploadResponse(List<FileMetadata> files) {
}
