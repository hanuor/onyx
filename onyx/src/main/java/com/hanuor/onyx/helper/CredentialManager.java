package com.hanuor.onyx.helper;

import com.hanuor.onyx.toolbox.gson.JsonParseException;
import com.hanuor.onyx.authorization.Credential;
import com.hanuor.onyx.authorization.CredentialCache;
import com.hanuor.onyx.exceptionhandler.ClarifaiException;
import com.hanuor.onyx.exceptionhandler.ClarifaiNotAuthorizedException;

import java.io.IOException;
import java.net.HttpURLConnection;


class CredentialManager {
  private static final long MIN_TTL_MS = 60000;

  private static class TokenResponse {
    String accessToken;
    Long expiresIn;
  }

  private final ConnectionFactory connectionFactory;
  private final String appId;
  private final String appSecret;
  private CredentialCache credentialCache;

  CredentialManager(ConnectionFactory connectionFactory, String appId, String appSecret,
      CredentialCache credentialCache) {
    this.connectionFactory = connectionFactory;
    this.appId = appId;
    this.appSecret = appSecret;
    this.credentialCache = credentialCache;
  }

  void setCredentialCache(CredentialCache credentialCache) {
    this.credentialCache = credentialCache;
  }

  Credential getCredential() throws ClarifaiException {
    Credential credential = credentialCache.getCredential(appId);
    if (credential != null &&
        System.currentTimeMillis() < credential.getExpirationTimeMillis() - MIN_TTL_MS) {
      return credential;
    }
    try {
      // Prepare the connection.
      HttpURLConnection conn = connectionFactory.newPost("/token", null);
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      conn.getOutputStream().write(new FormEncoded()
          .addParameter("grant_type", "client_credentials")
          .addParameter("client_id", appId)
          .addParameter("client_secret", appSecret)
          .toByteArray());

      if (conn.getResponseCode() < 200 || conn.getResponseCode() >= 300) {
        BaseResponse response = ResponseUtil.parseJsonAndClose(conn.getErrorStream(),
            BaseResponse.class);
        ResponseUtil.throwExceptionForErrorResponse(conn, response);
      }
      TokenResponse res = ResponseUtil.parseJsonAndClose(conn.getInputStream(),
          TokenResponse.class);
      if (res.accessToken != null && res.expiresIn != null) {
        long expiration = System.currentTimeMillis() + res.expiresIn;
        credential = new Credential(res.accessToken, null, expiration);
        credentialCache.putCredential(appId, credential);
        return credential;
      }
      throw new ClarifaiNotAuthorizedException(
          "Response did not contain access token or expiration.");
    } catch (IOException e) {
      throw new ClarifaiException("IOException", e);
    } catch (JsonParseException e) {
      throw new ClarifaiException("JSON parser error", e);
    }
  }

  void invalidateCredential() {
    credentialCache.removeCredential(appId);
  }
}
