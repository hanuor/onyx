package com.hanuor.onyx.helper;


import com.hanuor.onyx.toolbox.gson.JsonArray;
import com.hanuor.onyx.toolbox.gson.JsonDeserializationContext;
import com.hanuor.onyx.toolbox.gson.JsonDeserializer;
import com.hanuor.onyx.toolbox.gson.JsonElement;
import com.hanuor.onyx.toolbox.gson.JsonObject;
import com.hanuor.onyx.toolbox.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.hanuor.onyx.helper.ResponseUtil.GSON;

/**
 * Recognition results for a single image or video. See {@link ClarifaiClient} for more information.
 */
public class RecognitionResult {

  // Use a custom deserializer to handle different types in results:
  static class Deserializer implements JsonDeserializer<RecognitionResult> {
    public RecognitionResult deserialize(JsonElement json, Type type,
                                         JsonDeserializationContext context) throws JsonParseException {
      MediaResultEnvelope envelope = GSON.fromJson(json, MediaResultEnvelope.class);

      // The type of the "result" field can vary depending on the request and status:
      // 1. For a successful video recognition request, it is a VideoResultMessage.
      // 2. For a successful image recognition request, it is a ImageResultMessage.
      // 3. For an error, it is a ErrorResultMessage.
      // 4. For custom operations, it may be something completely different.
      // We need to peek into the JSON to figure out what to parse it as.
      if (isVideoResult(envelope.result)) {
        VideoResultMessage result = GSON.fromJson(envelope.result, VideoResultMessage.class);

        // Convert disjoint lists of classes, probabilities, embeddings, and timestamps into a
        // single list of VideoSegments:
        List<VideoSegment> segments = new ArrayList<VideoSegment>();
        if (result.tag != null) {
          int numTags = Math.min(Math.min(result.tag.timestamps.length,
              result.tag.classes.length), result.tag.probs.length);
          for (int i = 0; i < numTags; i++) {
            double timestamp = result.tag.timestamps[i];
            List<Tag> tags = tagsForClassesAndProbs(result.tag.classes[i], result.tag.probs[i]);
            double[] embed = (result.embed != null && result.embed.length > i) ?
                result.embed[i] : null;
            segments.add(new VideoSegment(timestamp, tags, embed));
          }
        } else if (result.embed != null) {
          for (int i = 0; i < result.embed.length; i++) {
            // If we request embeddings but no tags, we do not get timestamps back from the API.
            // Assume they are 1 per second for now.
            segments.add(new VideoSegment(i, null, result.embed[i]));
          }
        }
        return new RecognitionResult(envelope, null, null, segments, null);
      } else if (envelope.result.get("tag") != null || envelope.result.get("embed") != null) {
        ImageResultMessage result = GSON.fromJson(envelope.result, ImageResultMessage.class);
        List<Tag> tags = null;
        if (result.tag != null) {
          tags = tagsForClassesAndProbs(result.tag.classes, result.tag.probs);
        }
        return new RecognitionResult(envelope, tags, result.embed, null, null);
      } else {
        // This is either an error or no known fields (for example with custom ops).
        ErrorResultMessage result = GSON.fromJson(envelope.result, ErrorResultMessage.class);
        return new RecognitionResult(envelope, null, null, null, result.error);
      }
    }
  }

  /** Returns true if the result JSON is for video. */
  private static boolean isVideoResult(JsonObject result) {
    if (result.get("tag") != null && result.get("tag").isJsonObject()) {
      JsonElement timestamps = result.get("tag").getAsJsonObject().get("timestamps");
      if (timestamps != null && timestamps.isJsonArray()) {
        return true;
      }
    }
    if (result.get("embed") != null && result.get("embed").isJsonArray()) {
      JsonArray embedArray = result.get("embed").getAsJsonArray();
      if (embedArray.size() > 0 && embedArray.get(0).isJsonArray()) {
        return true;
      }
    }
    return false;
  }

  private static List<Tag> tagsForClassesAndProbs(String[] classes, double[] probs) {
    int count = Math.min(classes.length, probs.length);
    List<Tag> tags = new ArrayList<Tag>(count);
    for (int i = 0; i < count; i++) {
      tags.add(new Tag(classes[i], probs[i]));
    }
    return tags;
  }

  /** Envelope for a result for one image or video. */
  static class MediaResultEnvelope {
    private String statusCode;
    private String statusMsg;
    private String docidStr;
    private JsonObject result;
  }

  /** Result message for images. */
  static class ImageResultMessage {
    private ImageTagMessage tag;
    private double[] embed;
  }

  static class ImageTagMessage {
    private String[] classes;
    private double[] probs;
  }

  /** Result message for videos. */
  static class VideoResultMessage {
    private VideoTagMessage tag;
    private double[][] embed;
  }

  static class VideoTagMessage {
    private double[] timestamps;
    private String[][] classes;
    private double[][] probs;
  }

  /** Result message for errors. */
  static class ErrorResultMessage {
    private String error;
  }


  /** Indicates the status of the request. */
  public static enum StatusCode {
    /** The recognition operation completed successfully. */
    OK,
    /** There was a problem with the input provided by the caller. */
    CLIENT_ERROR,
    /** There was an error on the server processing the request.. */
    SERVER_ERROR,
  }

  private final StatusCode statusCode;
  private String statusMessage;
  private final String docId;
  private List<Tag> tags;
  private double[] embedding;
  private List<VideoSegment> videoSegments;
  private final JsonObject jsonResponse;

  private RecognitionResult(MediaResultEnvelope envelope, List<Tag> tags, double[] embedding,
      List<VideoSegment> videoSegments, String errorMessage) {
    if ("OK".equals(envelope.statusCode)) {
      statusCode = StatusCode.OK;
    } else if ("CLIENT_ERROR".equals(envelope.statusCode)) {
      statusCode = StatusCode.CLIENT_ERROR;
    } else {  // Treat unknown status code as server error.
      statusCode = StatusCode.SERVER_ERROR;
    }
    statusMessage = envelope.statusMsg;
    docId = envelope.docidStr;
    jsonResponse = envelope.result;
    this.videoSegments = videoSegments;
    if (videoSegments != null && videoSegments.size() > 0) {
      // Use the first segment for tags and embeddings.
      this.tags = videoSegments.get(0).getTags();
      this.embedding = videoSegments.get(0).getEmbedding();
    } else {
      this.tags = tags;
      this.embedding = embedding;
    }

    if (errorMessage != null) {
      // Additional error details can be stored in result.error.
      statusMessage += " " + errorMessage;
    }
  }

  /** Returns the status of the request. */
  public StatusCode getStatusCode() {
    return statusCode;
  }

  /** Returns additional information about the status of the request. */
  public String getStatusMessage() {
    return statusMessage;
  }

  /** Returns a unique and stable identifier for the content. */
  public String getDocId() {
    return docId;
  }

  /**
   * Returns a list of {@link Tag}s describing the content. If called on video content, this will
   * return tags for the first segment of the video. Use {@link #getVideoSegments} to get tags for
   * specific time intervals of the video. This returns null if tags were not requested or the
   * request failed.
   */
  public List<Tag> getTags() {
    return tags;
  }

  /**
   * Returns an embedding vector describing the content. If called on a video result, this will
   * return the embedding vector for the first segment of the video. Use {@link #getVideoSegments}
   * to get embeddings for specific time intervals of the video. This returns null if embeddings
   * were not requested or the request failed.
   */
  public double[] getEmbedding() {
    return embedding;
  }

  /**
   * For video input, this returns a list of {@link VideoSegment}s, each of which describes a time
   * interval of the video. These are ordered chronologically (in ascending order of timestamp).
   * If the input was not a video or the request failed, this returns null.
   */
  public List<VideoSegment> getVideoSegments() {
    return videoSegments;
  }

  /**
   * Returns the full JSON response for this result. This may contain fields that are not part of
   * the public API and are subject to change in the future.
   */
  public JsonObject getJsonResponse() {
    return jsonResponse;
  }
}
