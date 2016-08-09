package com.hanuor.onyx.helper;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A request for providing feedback on tagging results for one or more images. See the
 * <a href="https://developer.clarifai.com/docs/feedback">feedback documentation</a> for more
 * information on this request and its use.
 */
public class FeedbackRequest extends ClarifaiRequest {
  private String[] docIds;
  private String[] addTags;
  private String[] removeTags;
  private String[] similarDocIds;
  private String[] dissimilarDocIds;

  /** Returns the docids for the images that the feedback applies to. */
  public String[] getDocIds() {
    return docIds;
  }

  /** Sets the docids for the images that the feedback applies to. */
  public FeedbackRequest setDocIds(String ... docIds) {
    this.docIds = docIds;
    return this;
  }

  /** Returns the list of tags that should be added to the images. */
  public String[] getAddTags() {
    return addTags;
  }

  /** Sets the list of tags that should be added to the images. */
  public FeedbackRequest setAddTags(String ... addTags) {
    this.addTags = addTags;
    return this;
  }

  /** Returns the list of tags that should be removed from the images. */
  public String[] getRemoveTags() {
    return removeTags;
  }

  /** Sets the list of tags that should be removed from the images. */
  public FeedbackRequest setRemoveTags(String ... removeTags) {
    this.removeTags = removeTags;
    return this;
  }

  /** Returns the docids to be marked as similar to the docids set with {@link #setDocIds}. */
  public String[] getSimilarDocIds() {
    return similarDocIds;
  }

  /** Sets the docids to be marked as similar to the docids set with {@link #setDocIds}. */
  public FeedbackRequest setSimilarDocIds(String ... similarDocIds) {
    this.similarDocIds = similarDocIds;
    return this;
  }

  /** Returns the docids to be marked as dissimilar to the docids set with {@link #setDocIds}. */
  public String[] getDissimilarDocIds() {
    return dissimilarDocIds;
  }

  /** Sets the docids to be marked as dissimilar to the docids set with {@link #setDocIds}. */
  public FeedbackRequest setDissimilarDocIds(String ... dissimilarDocIds) {
    this.dissimilarDocIds = dissimilarDocIds;
    return this;
  }

  @Override String getContentType() {
    return "application/x-www-form-urlencoded";
  }

  @Override void writeContent(OutputStream out) throws IOException {
    FormEncoded form = new FormEncoded();
    addArrayParameter(form, "docids", docIds);
    addArrayParameter(form, "add_tags", addTags);
    addArrayParameter(form, "remove_tags", removeTags);
    addArrayParameter(form, "similar_docids", similarDocIds);
    addArrayParameter(form, "dissimilar_docids", dissimilarDocIds);
    out.write(form.toByteArray());
  }

  private static void addArrayParameter(FormEncoded form, String name, String[] values) {
    if (values != null && values.length > 0) {
      StringBuilder joined = new StringBuilder(values[0]);
      for (int i = 1; i < values.length; i++) {
        joined.append(',').append(values[i]);
      }
      form.addParameter(name, joined.toString());
    }
  }
}
