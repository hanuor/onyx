package com.hanuor.onyx.helper;

/** A tag returned by the recognition API. */
public class Tag {
  private final String name;
  private final double probability;

  Tag(String name, double probability) {
    this.name = name;
    this.probability = probability;
  }

  /**
   * Returns the name of the tag. The name will be in the language specified by the Locale passed
   * to {@link RecognitionRequest#setLocale(java.util.Locale)}, or the application's default
   * language if none was specified. Note that the name may consist of more than one word.
   */
  public String getName() {
    return name;
  }

  /** Returns a probability that this tag is associated with the input image. */
  public double getProbability() {
    return probability;
  }

  @Override public String toString() {
    return "[Tag " + name + ": " + probability + "]";
  }
}
