package com.hashtag.lasertag.shared.exceptions;

public class ResourceNotFoundException extends RuntimeException {

  Class<?> resourceClass;
  Object resourceId;

  public ResourceNotFoundException(Class<?> resourceClass, Object resourceId, String message) {
    super(message);
    this.resourceClass = resourceClass;
    this.resourceId = resourceId;
  }

  public ResourceNotFoundException(Class<?> resource, String message) {
    super(message);
  }
}