package com.hanuor.onyx.helper;

import java.io.IOException;
import java.io.OutputStream;

/** Base class for requests to the Clarifai API. */
public abstract class ClarifaiRequest {
  /** Returns the content type of the payload */
  abstract String getContentType();

  /** Writes the payload to the given stream. */
  abstract void writeContent(OutputStream out) throws IOException;
}
