package com.hanuor.onyx.helper;

import com.hanuor.onyx.authorization.CredentialCache;
import com.hanuor.onyx.authorization.InMemoryCredentialCache;
import com.hanuor.onyx.exceptionhandler.ClarifaiException;

import java.util.Arrays;
import java.util.List;

import static com.hanuor.onyx.helper.ClarifaiRequester.Method.GET;
import static com.hanuor.onyx.helper.ClarifaiRequester.Method.POST;

/**
 * A simple client for the Clarifai image and video recognition API.
 * <ul>
 *   <li>An introduction can be found at: <a href="https://github.com/clarifai/clarifai-java">
 *       github.com/clarifai/clarifai-java</a>.
 *   <li>Full API documentation can be found at: <a href="https://developer.clarifai.com">
 *       developer.clarifai.com</a>.
 * </ul>
 * <p>
 * The Clarifai API takes images and videos as input, performs recognition on them, returning
 * the results to the caller. The inputs are provided by constructing a {@link RecognitionRequest},
 * which is composed of publicly-accessible URLs, local files, or byte arrays. The request can be
 * passed to the {@link #recognize(RecognitionRequest)} method, which will return a list of
 * {@link RecognitionResult}s, one for each image or video in the request. These results will each
 * include {@link Tag}s and optionally an embedding vector.
 */
public class ClarifaiClient {
  private static final String CLARIFAI_API_ROOT = "https://api.clarifai.com/v1";

  private ConnectionFactory connectionFactory;
  private CredentialManager credentialManager;
  private int maxAttempts = 3;

  /**
   * Constructs a new ClarifaiClient, using the CLARIFAI_APP_ID and CLARIFAI_APP_SECRET
   * environment variables to identify the application. These can be obtained from the
   * <a href="https://developer.clarifai.com/applications">Applications Dashboard</a>.
   *
   * @throws ClarifaiException if the CLARIFAI_APP_ID and CLARIFAI_APP_SECRET environment variables
   *    are not defined
   */
  public ClarifaiClient() throws ClarifaiException {
    this(getEnvironmentVariable("CLARIFAI_APP_ID"), getEnvironmentVariable("CLARIFAI_APP_SECRET"));
  }

  /**
   * Constructs a new ClarifaiClient. The appId and appSecret are specific to an application
   * and can be obtained from the <a href="https://developer.clarifai.com/applications">
   * Applications Dashboard</a>.
   *
   * @param appId unique identifier for the app (also referred to as "Client ID")
   * @param appSecret secret key for the app (also referred to as "Client Secret")
   */
  public ClarifaiClient(String appId, String appSecret) {
    this(CLARIFAI_API_ROOT, appId, appSecret, InMemoryCredentialCache.getInstance());
  }

  ClarifaiClient(String apiRoot, String appId, String appSecret, CredentialCache cache) {
    this.connectionFactory = new ConnectionFactory(apiRoot);
    this.credentialManager = new CredentialManager(connectionFactory, appId, appSecret, cache);
  }

  /**
   * Makes an API info request.
   * @see <a href="https://developer.clarifai.com/docs/info">Info Documentation</a>
   * @return the API info
   * @throws ClarifaiException on errors; the class of the exception indicates the kind of error
   */
  public InfoResult getInfo() throws ClarifaiException {
    return new ClarifaiRequester<InfoResult>(
        connectionFactory, credentialManager, GET, "/info", InfoResult.class, maxAttempts)
        .execute(null);
  }

  /**
   * Makes a recognition request for tags and/or embeddings.
   * @param request the recognition request containing images or videos to recognize and options
   * @return a list of results, one for each image or video
   * @throws ClarifaiException on errors; the class of the exception indicates the kind of error
   */
  public List<RecognitionResult> recognize(RecognitionRequest request) throws ClarifaiException {
    return Arrays.asList(new ClarifaiRequester<RecognitionResult[]>(
        connectionFactory, credentialManager, POST, "/multiop", RecognitionResult[].class,
        maxAttempts)
        .execute(request));
  }

  /**
   * Makes a recognition request for tags only.
   * This supports the select_classes feature to request the probabilities for specific tag for the given image(s) with {@link RecognitionRequest#addTagForSelectClasses(String)}
   * @param request the recognition request containing images or videos to recognize and options
   * @return a list of results, one for each image or video
   * @throws ClarifaiException on errors; the class of the exception indicates the kind of error
   */
  public List<RecognitionResult> recognizeTags(RecognitionRequest request) throws ClarifaiException {
    return Arrays.asList(new ClarifaiRequester<RecognitionResult[]>(
        connectionFactory, credentialManager, POST, "/tag", RecognitionResult[].class,
        maxAttempts)
        .execute(request));
  }

  /**
   * Makes a feedback request.
   * @param request the feedback request
   * @throws ClarifaiException on errors; the class of the exception indicates the kind of error
   */
  public void sendFeedback(FeedbackRequest request) throws ClarifaiException {
    new ClarifaiRequester<Void>(
        connectionFactory, credentialManager, POST, "/feedback", Void.class, maxAttempts)
        .execute(request);
  }

  /**
   * Sets the cache used to store {@link}s for the app. The default credential cache
   * stores credentials in memory and is shared across all ClarifaiClient instances running in the
   * same JVM. This is sufficient for most use cases; however if you want to cache credentials
   * across processes or across JVM invocations, you can implement your own.
   */
  public void setCredentialCache(CredentialCache credentialCache) {
    credentialManager.setCredentialCache(credentialCache);
  }

  /**
   * Sets the maximum number of times to try to successfully make a request. Failures due to
   * connection, server, throttling, and authentication errors will be tried this number of times
   * before failing with the appropriate @{link ClarifaiException}.
   */
  public void setMaxAttempts(int maxAttempts) {
    this.maxAttempts = maxAttempts;
  }

  /** Returns the maximum number of attempts for a request. */
  public int getMaxAttempts() {
    return this.maxAttempts;
  }

  /**
   * Sets the timeout, in milliseconds, for establishing a connection. If the timeout expires
   * before the connection is established, the request will fail with a {@link ClarifaiException}.
   */
  public void setConnectTimeout(int connectTimeoutMillis) {
    connectionFactory.setConnectTimeout(connectTimeoutMillis);
  }

  /** Returns the connection timeout, in milliseconds. */
  public int getConnectTimeout() {
    return connectionFactory.getConnectTimeout();
  }

  /**
   * Sets the read timeout, in milliseconds. If the timeout expires before there is data available
   * for read, the request will fail with a {@link ClarifaiException}.
   */
  public void setReadTimeout(int readTimeoutMillis) {
    connectionFactory.setReadTimeout(readTimeoutMillis);
  }

  /** Returns the read timeout, in milliseconds. */
  public int getReadTimeout() {
    return connectionFactory.getReadTimeout();
  }

  private static String getEnvironmentVariable(String name) throws ClarifaiException {
    String value = System.getenv(name);
    if (value == null || value.length() == 0) {
      throw new ClarifaiException("Environment variable '" + name + "' must be defined");
    }
    return value;
  }
}
