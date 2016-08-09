package com.hanuor.onyx.authorization;


/** Interface for a cache of {@link Credential}s. */
public interface CredentialCache {
  /** Stores a {@link Credential} for a given appId. */
  public void putCredential(String appId, Credential credential);

  /** Returns the {@link Credential} for the given appId or null if it is not cached. */
  public Credential getCredential(String appId);

  /** Returns the {@link Credential} for a given appId from the cache. */
  public void removeCredential(String appId);
}
