package com.example.fileupload.exceptions;

import lombok.Getter;

@Getter
public class FileException extends RuntimeException {
    private String message;
    private FileExceptionType type;

    public FileException(String message, FileExceptionType type) {
        super(message);
        this.message = message;
        this.type = type;
    }
}
