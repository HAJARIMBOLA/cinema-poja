package com.example.demo.service.exception;

public class ConflictException extends RuntimeException {

  public ConflictException(String message) {
    super(message);
  }
}
