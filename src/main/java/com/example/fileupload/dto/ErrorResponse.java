package com.example.fileupload.dto;

import com.example.fileupload.exceptions.FileExceptionType;

public record ErrorResponse(String message, FileExceptionType type) {
}
