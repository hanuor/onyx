package com.hanuor.onyx.helper;

/**
 * Provides API details and usage limits. These limits must be obeyed by the user when making API
 * calls or the API call will fail.
 */
public class InfoResult {
  private Boolean embedAllowed;
  private Integer minImageSize;
  private Integer maxImageSize;
  private Long maxImageBytes;
  private Integer maxBatchSize;

  private Integer minVideoSize;
  private Integer maxVideoSize;
  private Long maxVideoBytes;
  private Integer maxVideoDuration;
  private Integer maxVideoBatchSize;

  /**
   * Returns whether the current user is allowed to use embed operations. If this returns false
   * and you would like to use this feature, please contact us at support@clarifai.com.
   */
  public boolean embedAllowed() {
    return (embedAllowed == null) ? false : embedAllowed;
  }

  /**
   * Returns the minimum allowed image size (on the smaller dimension). Recognition requests for
   * images that have a minimum dimension less than this limit will result in an error.
   */
  public int getMinImageSize() {
    return (minImageSize == null) ? 0 : minImageSize;
  }

  /**
   * Returns the maximum allowed image size (on the larger dimension). Recognition requests for
   * images that have a maximum dimension greater than this limit will result in an error.
   */
  public int getMaxImageSize() {
    return (maxImageSize == null) ? Integer.MAX_VALUE : maxImageSize;
  }

  /**
   * Returns the maximum allowed image size in bytes. Recognition requests for images that are
   * larger than this limit will result in an error.
   */
  public long getMaxImageBytes() {
    return (maxImageBytes == null) ? Integer.MAX_VALUE : maxImageBytes;
  }

  /** Returns the maximum number of images allowed in a single batch request. */
  public int getMaxBatchSize() {
    return (maxBatchSize == null) ? Integer.MAX_VALUE : maxBatchSize;
  }

  /**
   * Returns the minimum allowed video size (on the smaller dimension). Recognition requests for
   * videos that have a smaller dimension less than this limit will result in an error.
   */
  public int getMinVideoSize() {
    return (minVideoSize == null) ? 0 : minVideoSize;
  }

  /**
   * Returns the maximum allowed video size (on the larger dimension). Recognition requests for
   * videos that have a larger dimension greater than this limit will result in an error.
   */
  public int getMaxVideoSize() {
    return (maxVideoSize == null) ? Integer.MAX_VALUE : maxVideoSize;
  }

  /**
   * Returns the maximum allowed video size in bytes. Recognition requests for videos that are
   * larger than this limit will result in an error.
   */
  public long getMaxVideoBytes() {
    return (maxVideoBytes == null) ? Integer.MAX_VALUE : maxVideoBytes;
  }

  /**
   * Returns the maximum duration of a video, in seconds. Recognition requests for videos that have
   * a duration greater than this limit will result in an error.
   */
  public int getMaxVideoDuration() {
    return (maxVideoDuration == null) ? Integer.MAX_VALUE : maxVideoDuration;
  }

  /** Returns the maximum number of videos allowed in a single batch request. */
  public int getMaxVideoBatchSize() {
    return (maxVideoBatchSize == null) ? Integer.MAX_VALUE : maxVideoBatchSize;
  }
}
