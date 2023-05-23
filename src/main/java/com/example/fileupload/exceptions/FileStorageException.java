package com.example.fileupload.exceptions;

public class FileStorageException extends FileException {
    public FileStorageException(String message) {
        super(message, FileExceptionType.INTERNAL_ERROR);
    }
}
