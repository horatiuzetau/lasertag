package com.hashtag.lasertag.shared;

import com.hashtag.lasertag.setting.exceptions.ServiceUnavailableException;
import com.hashtag.lasertag.shared.enums.ResponseVoiceType;
import com.hashtag.lasertag.shared.exceptions.BadRequestException;
import com.hashtag.lasertag.shared.exceptions.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * When resource was not found
   */
  @ExceptionHandler(ServiceUnavailableException.class)
  public ResponseEntity<?> handleServiceUnavailableException(ServiceUnavailableException ex,
      WebRequest request) {

    log.error("[GLOBAL EXCEPTION HANDLER] Error occurred: ", ex);
    ErrorDetails errorDetails = new ErrorDetails(
        HttpStatus.SERVICE_UNAVAILABLE.value(), ex.getMessage(),
        request.getDescription(false), ResponseVoiceType.ERROR
    );
    return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE);
  }

  /**
   * When input validation fails
   */
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<?> handleBadRequestException(BadRequestException ex,
      WebRequest request) {

    log.error("[GLOBAL EXCEPTION HANDLER] Error occurred: ", ex);
    ErrorDetails errorDetails = new ErrorDetails(
        HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
        request.getDescription(false), ResponseVoiceType.ERROR
    );
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  /**
   * All other cases
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
    log.error("[GLOBAL EXCEPTION HANDLER] Error occurred: ", ex);
    ErrorDetails errorDetails = new ErrorDetails(
        HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(),
        request.getDescription(false), ResponseVoiceType.ERROR
    );
    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}