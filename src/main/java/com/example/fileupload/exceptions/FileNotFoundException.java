package com.example.fileupload.exceptions;

public class FileNotFoundException extends FileException {
    public FileNotFoundException(String message) {
        super(message, FileExceptionType.FILE_NOT_FOUND);
    }
}
