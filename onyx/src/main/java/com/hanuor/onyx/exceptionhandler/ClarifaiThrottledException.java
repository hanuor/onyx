package com.hanuor.onyx.exceptionhandler;

/**
 * Thrown when the client is making too many requests. The client should wait for the time returned
 * by {@link #getWaitSeconds()} before continuing to send requests.
 */
public class ClarifaiThrottledException extends ClarifaiException {
  private static final long serialVersionUID = 1L;
  private final int waitSeconds;

  public ClarifaiThrottledException(String message, int waitSeconds) {
    super(message == null ? "Throttled" : message);
    this.waitSeconds = waitSeconds;
  }

  /** @return the number of seconds to wait before sending more requests. */
  public int getWaitSeconds() {
    return waitSeconds;
  }
}
