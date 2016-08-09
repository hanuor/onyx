package com.hanuor.onyx.exceptionhandler;

/** Thrown for requests that fail due to bad input. Call {@link #getMessage()} for details. */
public class ClarifaiBadRequestException extends ClarifaiException {
  private static final long serialVersionUID = 1L;

  public ClarifaiBadRequestException(String message) {
    super(message == null ? "Bad Request" : message);
  }
}
