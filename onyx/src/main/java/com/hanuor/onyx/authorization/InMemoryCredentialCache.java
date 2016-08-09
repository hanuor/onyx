package com.hanuor.onyx.authorization;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Default in-memory {@link CredentialCache} implementation. */
public class InMemoryCredentialCache implements CredentialCache {
  private static final InMemoryCredentialCache INSTANCE = new InMemoryCredentialCache();

  /** Returns the singleton instance. */
  public static InMemoryCredentialCache getInstance() {
    return INSTANCE;
  }

  private final Map<String, Credential> cache = new ConcurrentHashMap<String, Credential>();

  public void putCredential(String appId, Credential credential) {
    cache.put(appId, credential);
  }

  public Credential getCredential(String appId) {
    return cache.get(appId);
  }

  public void removeCredential(String appId) {
    cache.remove(appId);
  }

  private InMemoryCredentialCache() {}
}
