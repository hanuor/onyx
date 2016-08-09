package com.hanuor.onyx.authorization;

/** An OAuth2 credential for that may be stored in a {@link CredentialCache}. */
public class Credential {
  private final String accessToken;
  private final String refreshToken;
  private final long expirationTimeMillis;

  /**
   * Constructs a new Credential.
   * @param accessToken the OAuth2 access token
   * @param refreshToken the OAuth2 refresh token, or null if there is no refresh token
   * @param expirationTimeMillis the time at which the access token expires
   */
  public Credential(String accessToken, String refreshToken, long expirationTimeMillis) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.expirationTimeMillis = expirationTimeMillis;
  }

  /** Returns the access token. */
  public String getAccessToken() {
    return accessToken;
  }

  /** Returns the refresh token, or null if there is none. */
  public String getRefreshToken() {
    return refreshToken;
  }

  /** Returns the time at which the current access token expires. */
  public long getExpirationTimeMillis() {
    return expirationTimeMillis;
  }
}
