package com.hanuor.onyx.helper;

import com.hanuor.onyx.toolbox.gson.FieldNamingPolicy;
import com.hanuor.onyx.toolbox.gson.Gson;
import com.hanuor.onyx.toolbox.gson.GsonBuilder;
import com.hanuor.onyx.toolbox.gson.JsonParseException;
import com.hanuor.onyx.exceptionhandler.ClarifaiBadRequestException;
import com.hanuor.onyx.exceptionhandler.ClarifaiException;
import com.hanuor.onyx.exceptionhandler.ClarifaiNotAuthorizedException;
import com.hanuor.onyx.exceptionhandler.ClarifaiThrottledException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;

/** Utilities for working with responses from the server. */
class ResponseUtil {
  static final Gson GSON = new GsonBuilder()
      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT)
      .registerTypeAdapter(RecognitionResult.class, new RecognitionResult.Deserializer())
      .create();

  /**
   * Throws the appropriate ClarifaiException for the response code from an HttpURLConnection,
   * populating the message from the response payload.
   */
  static void throwExceptionForErrorResponse(HttpURLConnection conn, BaseResponse response)
      throws IOException, ClarifaiException {
    String errorMessage = "";
    if (response.statusCode != null) {
      errorMessage += response.statusCode;
    }
    if (response.statusMsg != null) {
      errorMessage += " " + response.statusMsg;
    }
    if (response.results != null && response.results.isJsonPrimitive()) {
      errorMessage += " " + response.results.getAsString();
    }
    if (errorMessage.length() == 0) {
      errorMessage = conn.getResponseMessage();
    }

    int code = conn.getResponseCode();
    if (code == HttpURLConnection.HTTP_UNAUTHORIZED) {
      throw new ClarifaiNotAuthorizedException(errorMessage);
    } else if (code == 429) {  // Too Many Requests
      int waitSeconds = conn.getHeaderFieldInt("X-Throttle-Wait-Seconds", 10);
      throw new ClarifaiThrottledException(errorMessage, waitSeconds);
    } else if (code >= 400 && code < 500) {
      throw new ClarifaiBadRequestException(errorMessage);
    } else if (code >= 500 && code < 600) {
      throw new ClarifaiException(errorMessage);
    } else {
      throw new ClarifaiException("Unexpected HTTP status code (" + code + "): " + errorMessage);
    }
  }

  /** Parses an input as JSON of the given class, then closes the stream. */
  static <T> T parseJsonAndClose(InputStream in, Class<T> cls) throws JsonParseException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    try {
      return GSON.fromJson(reader, cls);
    } finally {
      try {
        reader.close();
      } catch (IOException e) { /* ignored */ }
    }
  }
}
