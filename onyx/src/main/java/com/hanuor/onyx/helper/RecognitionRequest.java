package com.hanuor.onyx.helper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * A request for recognition to be performed on one or more media files (images or videos).
 * See {@link ClarifaiClient} for more information.
 */
public class RecognitionRequest extends ClarifaiRequest {
  private static class Item {
    File file;
    byte[] imageBytes;
    String url;

    Item(File file) { this.file = file; }
    Item(byte[] imageBytes) { this.imageBytes = imageBytes; }
    Item(String url) { this.url = url; }

    InputStream getStream() throws IOException {
      if (file != null) {
        return new FileInputStream(file);
      } else if (imageBytes != null) {
        return new ByteArrayInputStream(imageBytes);
      }
      return null;
    }
  }

  private static Set<String> defaultOperations() {
    HashSet<String> operations = new HashSet<String>();
    operations.add("tag");
    return operations;
  }

  private final List<Item> items = new ArrayList<Item>();
  private final List<String> selectClasses = new ArrayList<String>();
  private String model = "default";
  private Locale locale = null;
  private final Set<String> operations = defaultOperations();
  private final Multipart multipart = new Multipart();

  /**
   * Constructs a new request for recognition on one or more image or video files on the local
   * filesystem.
   * @param files Files containing the image data to be recognized
   */
  public RecognitionRequest(File ... files) {
    for (File file : files) {
      items.add(new Item(file));
    }
  }

  /**
   * Constructs a new request for recognition on one or more images or videos represented as byte
   * arrays of the image or video data.
   * @param imageByteArrays byte arrays containing image data to be recognized
   */
  public RecognitionRequest(byte[] ... imageByteArrays) {
    for (byte[] imageBytes : imageByteArrays) {
      items.add(new Item(imageBytes));
    }
  }

  /**
   * Constructs a new request for recognition on one or more images or videos represented as
   * publicly-accessible URLs on the web.
   * @param urls publicly-accessible URLs for images to be recognized
   */
  public RecognitionRequest(String ... urls) {
    for (String url : urls) {
      items.add(new Item(url));
    }
  }

  /** Returns the name of the model to use for recognition. */
  public String getModel() {
    return model;
  }

  /** Sets the model to use for recognition. */
  public RecognitionRequest setModel(String model) {
    this.model = model;
    return this;
  }

  /**
   * Returns the {@link Locale} to use for tags returned from the server or null if the default
   * should be used.
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * Sets the {@link Locale} to use for tags returned from the server. The default is null, which
   * will result in tags being returned in the default language for the application, which can be
   * set at the <a href="https://developer.clarifai.com/applications/">developer console</a>.
   * <p>
   * Note that sending a Locale with a language that is not supported by the server will result in
   * an error. It is the caller's responsibility to ensure that only supported Locales are set.
   *
   * @see <a href="https://developer.clarifai.com/docs/tag">Tagging Documentation</a>
   */
  public RecognitionRequest setLocale(Locale locale) {
    this.locale = locale;
    return this;
  }

  /** Returns true (default) if tags should be included in the result, or false if not. */
  public boolean getIncludeTags() {
    return operations.contains("tag");
  }

  /** Sets whether to include tags in the result. */
  public RecognitionRequest setIncludeTags(boolean includeTags) {
    if (includeTags) {
      operations.add("tag");
    } else {
      operations.remove("tag");
    }
    return this;
  }

  /**
   * Returns true if embeddings should be included in the result, or false (default) if not.
   * Embeddings are currently not supported by default. Please contact us at support@clarifai.com
   * to enable embeddings for your application.
   */
  public boolean getIncludeEmbedding() {
    return operations.contains("embed");
  }

  /** Sets whether to include embeddings in the result. */
  public RecognitionRequest setIncludeEmbedding(boolean includeEmbedding) {
    if (includeEmbedding) {
      operations.add("embed");
    } else {
      operations.remove("embed");
    }
    return this;
  }

  /** Adds a custom operation to the list of operations the server should perform on the image. */
  public RecognitionRequest addCustomOperation(String customOperation) {
    operations.add(customOperation);
    return this;
  }

  /** Add a tag for which you'd like to get the probability of for the image.
   *  @see <a href="https://developer.clarifai.com/guide/tag#select-classes">Select Classes Documentation</a>
   */
  public RecognitionRequest addTagForSelectClasses(String tag) {
    selectClasses.add(tag);
    return this;
  }

  @Override String getContentType() {
    return "multipart/form-data; boundary=" + multipart.getBoundary();
  }

  @Override void writeContent(OutputStream out) throws IOException {
    StringBuilder opParam = new StringBuilder();
    for (String op : operations) {
      if (opParam.length() > 0) {
        opParam.append(',');
      }
      opParam.append(op);
    }

    multipart.start(out);
    multipart.writeParameter("op", opParam.toString());
    multipart.writeParameter("model", model);

    if (locale != null) {
      // Use just the language code, unless it's zh-TW, in which case we need the country too.
      String language;
      if (locale.equals(Locale.TRADITIONAL_CHINESE)) {
        language = "zh-TW";
      } else {
        language = locale.getLanguage();
      }
      multipart.writeParameter("language", language);
    }

    for (Item item : items) {
      if (item.url != null) {
        multipart.writeParameter("url", item.url);
      } else {
        InputStream in = item.getStream();
        try {
          multipart.writeMedia("encoded_data", "media", in);
        } finally {
          in.close();
        }
      }
    }

    if (selectClasses.size() > 0) {
      StringBuilder joined = new StringBuilder(selectClasses.get(0));
      for (int i = 1; i < selectClasses.size(); i++) {
        joined.append(',').append(selectClasses.get(i));
      }
      multipart.writeParameter("select_classes", joined.toString());
    }

    multipart.finish();
  }
}
