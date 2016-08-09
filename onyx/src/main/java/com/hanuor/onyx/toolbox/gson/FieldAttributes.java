/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hanuor.onyx.toolbox.gson;

import com.hanuor.onyx.toolbox.gson.internal.$Gson$Preconditions;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

public final class FieldAttributes {
  private final Field field;

  public FieldAttributes(Field f) {
    $Gson$Preconditions.checkNotNull(f);
    this.field = f;
  }

  /**
   * @return the declaring class that contains this field
   */
  public Class<?> getDeclaringClass() {
    return field.getDeclaringClass();
  }

  /**
   * @return the name of the field
   */
  public String getName() {
    return field.getName();
  }
  public Type getDeclaredType() {
    return field.getGenericType();
  }

  public Class<?> getDeclaredClass() {
    return field.getType();
  }

  public <T extends Annotation> T getAnnotation(Class<T> annotation) {
    return field.getAnnotation(annotation);
  }
  public Collection<Annotation> getAnnotations() {
    return Arrays.asList(field.getAnnotations());
  }
  public boolean hasModifier(int modifier) {
    return (field.getModifiers() & modifier) != 0;
  }

  Object get(Object instance) throws IllegalAccessException {
    return field.get(instance);
  }
  boolean isSynthetic() {
    return field.isSynthetic();
  }
}
