//package com.hashtag.lasertag.shared;
//
//import com.hashtag.lasertag.shared.enums.ResponseVoiceType;
//import com.hashtag.lasertag.shared.exceptions.ErrorDetails;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.WebRequest;
//
//@Slf4j
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
////  /**
////   * When resource was not found
////   */
////  @ExceptionHandler(ServiceUnavailableException.class)
////  public ResponseEntity<?> handleServiceUnavailableException(ServiceUnavailableException ex,
////      WebRequest request) {
////
////    ErrorDetails errorDetails = new ErrorDetails(
////        HttpStatus.NOT_FOUND.value(), ex.getMessage(),
////        request.getDescription(false), ResponseVoiceType.ERROR
////    );
////    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
////  }
////
////  /**
////   * When resource was not found
////   */
////  @ExceptionHandler(ResourceNotFoundException.class)
////  public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex,
////      WebRequest request) {
////
////    ErrorDetails errorDetails = new ErrorDetails(
////        HttpStatus.NOT_FOUND.value(), ex.getMessage(),
////        request.getDescription(false), ResponseVoiceType.ERROR
////    );
////    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
////  }
////
////  /**
////   * When input validation fails
////   */
////  @ExceptionHandler(BadRequestException.class)
////  public ResponseEntity<?> handleBadRequestException(BadRequestException ex,
////      WebRequest request) {
////
//////    log.error("Error occurred: ", ex);
////    ErrorDetails errorDetails = new ErrorDetails(
////        HttpStatus.NOT_FOUND.value(), ex.getMessage(),
////        request.getDescription(false), ResponseVoiceType.ERROR
////    );
////    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
////  }
////
////  /**
////   * When payload fields are not valid
////   */
////  @ExceptionHandler(MethodArgumentNotValidException.class)
////  public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
////    Map<String, String> errors = new HashMap<>();
////    ex.getBindingResult().getFieldErrors().forEach(error -> {
////      errors.put(error.getField(), error.getDefaultMessage());
////    });
////    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
////  }
////
////  /**
////   * When there is a database error at commit time
////   */
////  @ExceptionHandler(DataIntegrityViolationException.class)
////  public ResponseEntity<?> handleDatabaseException(DataIntegrityViolationException ex) {
////    ErrorDetails errorDetails = new ErrorDetails(
////        HttpStatus.CONFLICT.value(), "Database error: " + ex.getMessage(),
////        "", ResponseVoiceType.ERROR
////    );
////    return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
////  }
//
//  /**
//   * All other cases
//   */
//  @ExceptionHandler(Exception.class)
//  public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
//    log.error(ex.getMessage());
//    ErrorDetails errorDetails = new ErrorDetails(
//        HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(),
//        request.getDescription(false), ResponseVoiceType.ERROR
//    );
//    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
//  }
//}