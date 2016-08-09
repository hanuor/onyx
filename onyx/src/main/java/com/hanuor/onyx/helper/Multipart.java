package com.hanuor.onyx.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Random;

class Multipart {
  private static final Charset UTF8 = Charset.forName("UTF-8");
  private static final int BUFFER_SIZE = 4096;

  private OutputStream out;
  private final String boundary;

  public Multipart() {
    boundary = Long.toHexString(new Random().nextLong());
  }

  public String getBoundary() {
    return boundary;
  }

  public void start(OutputStream out) {
    this.out = out;
  }

  public void finish() throws IOException {
    out.write(("--" + boundary + "--\r\n").getBytes(UTF8));
    out.flush();
  }

  public void writeParameter(String name, String value) throws IOException {
    writeBoundary();
    String payload = "Content-Disposition: form-data; " +
                     "name=\"" + name + "\"\r\n\r\n" + value + "\r\n";
    out.write(payload.getBytes(UTF8));
  }

  /** Writes media from the input stream. Does not close the input stream. */
  public void writeMedia(String name, String filename, InputStream in) throws IOException {
    writeBoundary();
    String header = "Content-Disposition: form-data;" +
                    " name=\"" + name + "\";" +
                    " filename=\"" + filename + "\"\r\n" +
                    "Content-Type: application/octet-stream\r\n\r\n";
    out.write(header.getBytes(UTF8));

    byte[] buf = new byte[BUFFER_SIZE];
    while (true) {
      int numRead = in.read(buf);
      if (numRead < 0) {
        break;
      }
      out.write(buf, 0, numRead);
    }
    out.write("\r\n".getBytes(UTF8));
  }

  private void writeBoundary() throws IOException {
    out.write(("--" + boundary + "\r\n").getBytes(UTF8));
  }
}
