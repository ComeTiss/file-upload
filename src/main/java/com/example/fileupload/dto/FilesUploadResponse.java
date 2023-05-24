package com.example.fileupload.dto;

import java.util.List;

public record FilesUploadResponse(List<FileMetadata> files) {
}
