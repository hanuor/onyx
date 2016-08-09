package com.hanuor.onyx.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

class FormEncoded {
  private final StringBuilder b = new StringBuilder();

  public FormEncoded addParameter(String key, String value) {
    b.append(b.length() > 0 ? "&" : "");
    try {
      b.append(URLEncoder.encode(key, "UTF-8")).append('=')
          .append(URLEncoder.encode(value, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);  // Should never happen.
    }
    return this;
  }

  public byte[] toByteArray() {
    return toString().getBytes(Charset.forName("UTF-8"));
  }

  @Override public String toString() {
    return b.toString();
  }
}
