package com.hanuor.onyx.exceptionhandler;

/** Thrown for requests that are not authorized. Call {@link #getMessage()} for details. */
public class ClarifaiNotAuthorizedException extends ClarifaiException {
  private static final long serialVersionUID = 1L;

  public ClarifaiNotAuthorizedException(String message) {
    super(message == null ? "Not Authorized" : message);
  }
}
