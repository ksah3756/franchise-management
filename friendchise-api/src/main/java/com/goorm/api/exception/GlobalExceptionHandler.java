package com.goorm.api.exception;

import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.warn("CustomException 예외 발생, msg:{}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        int errorCodeValue = errorCode.getStatus().getValue();
        if(errorCode == ErrorCode.WEBCLIENT_ERROR)
            return ResponseEntity.status(HttpStatus.valueOf(errorCodeValue)).body(createErrorResponse(errorCode, e.getMessage()));
        return ResponseEntity.status(HttpStatus.valueOf(errorCodeValue)).body(createErrorResponse(errorCode));
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        log.warn("handleMethodArgumentNotValid 예외 발생, msg:{}", e.getMessage());
        ErrorCode errorCode = ErrorCode.INVALID_PARAMETER;
        return ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus().getValue())).body(createErrorResponse(e, errorCode));
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("handleHttpRequestMethodNotSupported 예외 발생, msg:{}", ex.getMessage());
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        return ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus().getValue())).body(createErrorResponse(errorCode));
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("handleNoHandlerFoundException 예외 발생, msg:{}", ex.getMessage());
        ErrorCode errorCode = ErrorCode.URL_NOT_FOUND;
        return ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus().getValue())).body(createErrorResponse(errorCode));
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("handleTypeMismatch 예외 발생, msg:{}", ex.getMessage());
        ErrorCode errorCode = ErrorCode.TYPE_MISMATCH;
        return ResponseEntity.status(HttpStatus.valueOf(errorCode.getStatus().getValue())).body(createErrorResponse(errorCode));
    }

    private ErrorResponse createErrorResponse(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
    }

    private ErrorResponse createErrorResponse(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(message)
                .build();
    }

    private ErrorResponse createErrorResponse(BindException e, ErrorCode errorCode) {
        List<ErrorResponse.ValidationError> validationErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ErrorResponse.ValidationError::of)
                .toList();
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .errors(validationErrors)
                .build();
    }
}
