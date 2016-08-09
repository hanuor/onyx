package com.hanuor.onyx.helper;

import com.hanuor.onyx.toolbox.gson.JsonParseException;
import com.hanuor.onyx.exceptionhandler.ClarifaiBadRequestException;
import com.hanuor.onyx.exceptionhandler.ClarifaiException;
import com.hanuor.onyx.exceptionhandler.ClarifaiNotAuthorizedException;
import com.hanuor.onyx.exceptionhandler.ClarifaiThrottledException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;


/** Manages a single request to the Clarifai API. */
class ClarifaiRequester<T> {
  static enum Method {
    GET, POST
  }

  private final ConnectionFactory connectionFactory;
  private final CredentialManager credentialManager;
  private final Method method;
  private final String path;
  private final Class<T> resultClass;
  private final int maxAttempts;

  ClarifaiRequester(ConnectionFactory connectionFactory, CredentialManager credentialManager,
      Method method, String path, Class<T> resultClass, int maxAttempts) {
    this.connectionFactory = connectionFactory;
    this.credentialManager = credentialManager;
    this.method = method;
    this.path = path;
    this.resultClass = resultClass;
    this.maxAttempts = maxAttempts;
  }

  T execute(ClarifaiRequest request) throws ClarifaiException {
    for (int i = maxAttempts - 1; i >= 0; i--) {
      try {
        return executeOnce(request);
      } catch (ClarifaiNotAuthorizedException e) {
        credentialManager.invalidateCredential();
        if (i == 0) throw e;
      } catch (ClarifaiThrottledException e) {
        if (i == 0) throw e;
        waitForSeconds(e.getWaitSeconds());
      } catch (ClarifaiBadRequestException e) {
        throw e;  // Retrying will not help.
      } catch (ClarifaiException e) {
        if (i == 0) throw e;
      }
    }
    throw new IllegalStateException();
  }

  T executeOnce(ClarifaiRequest request) throws ClarifaiException {
    try {
      // Send request:
      HttpURLConnection conn;
      if (method == Method.POST) {
        conn = connectionFactory.newPost(path, credentialManager.getCredential());
      } else {
        conn = connectionFactory.newGet(path, credentialManager.getCredential());
      }
      if (request != null) {
        conn.setRequestProperty("Content-Type", request.getContentType());
        BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
        try {
          request.writeContent(out);
        } finally {
          out.close();
        }
      }

      // Parse result:
      boolean isSuccess = (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300);
      BaseResponse response;
      if (isSuccess) {
        response = ResponseUtil.parseJsonAndClose(conn.getInputStream(), BaseResponse.class);
      } else {
        response = ResponseUtil.parseJsonAndClose(conn.getErrorStream(), BaseResponse.class);

        // The API returns a 400 when all images in a recognition request are bad. However, we
        // want to communicate this back in the RecognitionResults rather than throwing an
        // exception for consistency with the partial error and non-error cases.
        if (conn.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST &&
            resultClass == RecognitionResult[].class &&
            response.results != null && response.results.isJsonArray()) {
          isSuccess = true;
        } else {
          ResponseUtil.throwExceptionForErrorResponse(conn, response);
        }
      }

      if (resultClass == Void.class) {
        return null;
      } else {
        return ResponseUtil.GSON.fromJson(response.results, resultClass);
      }
    } catch (IOException e) {
      throw new ClarifaiException("IOException", e);
    } catch (JsonParseException e) {
      throw new ClarifaiException("Server returned an unparsable response", e);
    }
  }

  private static void waitForSeconds(int seconds) {
    try {
      Thread.sleep(seconds * 1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ClarifaiException("Interrupted", e);
    }
  }
}
