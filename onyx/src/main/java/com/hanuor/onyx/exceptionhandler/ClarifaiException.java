package com.hanuor.onyx.exceptionhandler;

/** Base class for all exceptions thrown by Clarifai API. */
public class ClarifaiException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ClarifaiException(String message) {
  	super(message, null);
  }

  public ClarifaiException(String message, Throwable e) {
  	super(message, e);
  }
}
