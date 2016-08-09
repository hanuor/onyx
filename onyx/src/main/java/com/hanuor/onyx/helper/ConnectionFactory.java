package com.hanuor.onyx.helper;

import com.hanuor.onyx.authorization.Credential;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

class ConnectionFactory {
  private final String baseUrl;
  private int connectTimeout = 30000;
  private int readTimeout = 60000;

  ConnectionFactory(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  int getConnectTimeout() {
    return connectTimeout;
  }

  void setReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
  }

  int getReadTimeout() {
    return readTimeout;
  }

  HttpURLConnection newGet(String path, Credential credential) throws IOException {
    HttpURLConnection conn = newConnection(path, credential);
    conn.setRequestMethod("GET");
    conn.setDoInput(true);
    return conn;
  }

  HttpURLConnection newPost(String path, Credential credential) throws IOException {
    HttpURLConnection conn = newConnection(path, credential);
    conn.setRequestMethod("POST");
    conn.setDoOutput(true);
    conn.setDoInput(true);
    return conn;
  }

  HttpURLConnection newConnection(String path, Credential credential) throws IOException {
    URL url = new URL(this.baseUrl + path);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setConnectTimeout(connectTimeout);
    conn.setReadTimeout(readTimeout);
    conn.setUseCaches(false);
    if (credential != null) {
      conn.setRequestProperty("Authorization", "Bearer " + credential.getAccessToken());
    }
    return conn;
  }
}
