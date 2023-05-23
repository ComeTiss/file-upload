package com.example.fileupload.exceptions;

public class InvalidFileException extends FileException {
    public InvalidFileException(String message) {
        super(message, FileExceptionType.INVALID_INPUT_ERROR);
    }
}
