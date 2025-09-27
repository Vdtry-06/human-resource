package com.spring.human.resource.server.exceptions;

import com.spring.human.lib.api.ApiResponse;
import com.spring.human.lib.exceptions.BadRequestException;
import com.spring.human.resource.server.configs.language.MessageSourceHelper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class RestExceptionsHandler extends ResponseEntityExceptionHandler {
    private final MessageSourceHelper messageSourceHelper;

    /* Default exception */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception exception) {
        String message = messageSourceHelper.getMessage("error.unexpected");
        String detailMessage = exception.getLocalizedMessage();
        String moreInformation = "http:/localhost:8080/api/v1/exception/500";

        log.error(detailMessage, exception);
        ApiResponse<String> apiResponse = new ApiResponse<String>(false, message, detailMessage, HttpStatus.INTERNAL_SERVER_ERROR, moreInformation);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException exception) {
        String message = messageSourceHelper.getMessage("error.badRequestException");
        String detailMessage = exception.getLocalizedMessage();
        String moreInformation = "http:/localhost:8080/api/v1/exception/400";

        log.error(detailMessage, exception);
        ApiResponse<String> apiResponse = new ApiResponse<String>(false, message, detailMessage, HttpStatus.BAD_REQUEST, moreInformation);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    /* Url handler not found exception */
    public ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ndfe,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        StringBuilder message = new StringBuilder(messageSourceHelper.getMessage("error.notFound"));
        message.append(", ")
                .append(ndfe.getHttpMethod())
                .append(", ")
                .append(ndfe.getRequestURL());

        String detailMessage = ndfe.getLocalizedMessage();
        String moreInformation = "http:/localhost:8080/api/v1/exception/404";

        log.error(detailMessage, ndfe);

        ApiResponse<String> apiResponse = new ApiResponse<>(false, message.toString(), detailMessage, HttpStatus.NOT_FOUND, moreInformation);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    /* Method not allow exception */
    @Override
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException hrmnse,
                                                                      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String message = getMessageFromHttpRequestMethodNotSupportedException(hrmnse);
        String detailMessage = hrmnse.getLocalizedMessage();
        String moreInformation = "http://localhost:8080/api/v1/exception/405";

        log.error(detailMessage, hrmnse);
        ApiResponse<String> rsp = new ApiResponse<String>(false, message, detailMessage, HttpStatus.METHOD_NOT_ALLOWED, moreInformation);
        return new ResponseEntity<>(rsp, HttpStatus.METHOD_NOT_ALLOWED);
    }

    private String getMessageFromHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        StringBuilder message = new StringBuilder(exception.getMethod());
        message
                .append(", ")
                .append(messageSourceHelper.getMessage("error.notSupportedMethod"));
        for (HttpMethod method : exception.getSupportedHttpMethods()) {
            message.append(method).append(" ");
        }
        return message.toString();
    }

    /* Not supported media type */
    @Override
    public ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException hmtnse,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String message = getMessageFromHttpMediaTypeNotSupportedException(hmtnse);
        String detailMessage = hmtnse.getLocalizedMessage();
        String moreInformation = "http://localhost:8080/api/v1/exception/415";

        log.error(detailMessage, hmtnse);
        ApiResponse<String> rsp = new ApiResponse<String>(false, message, detailMessage, HttpStatus.UNSUPPORTED_MEDIA_TYPE, moreInformation);
        return new ResponseEntity<>(rsp, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    private String getMessageFromHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        StringBuilder message = new StringBuilder(exception.getContentType().toString());
        message
                .append(", ")
                .append(messageSourceHelper.getMessage("error.notSupportedMedia"));

        for (Object method : exception.getSupportedMediaTypes().toArray()) {
            message
                    .append(method)
                    .append(" ");
        }
        return message.substring(0, message.length() - 2);
    }

    /* Argument not valid */
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException manve,
                                                               HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String message = messageSourceHelper.getMessage("error.argumentNotValid");
        String detailMessage = manve.getLocalizedMessage();
        // error
        Map<String, String> errors = new HashMap<String, String>();
        for (ObjectError error : manve.getBindingResult().getAllErrors()) {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        }

        String moreInformation = "http://localhost:8080/api/v1/exception/400";

        log.error("{}\n{} {}", detailMessage, errors.toString(), manve);
        ApiResponse<String> rsp = new ApiResponse<String>(false, message, detailMessage, HttpStatus.BAD_REQUEST, moreInformation);
        return new ResponseEntity<>(rsp, HttpStatus.BAD_REQUEST);
    }

    /* Bean validation error */
    @SuppressWarnings("rawtypes")
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(ConstraintViolationException exception) {

        StringBuilder detailMessage = new StringBuilder(messageSourceHelper.getMessage("error.methodArgumentNotValid"));
        detailMessage.append(" \n");
        detailMessage.append(exception.getLocalizedMessage());

        // error
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation violation : exception.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }
        String moreInformation = "http://localhost:8080/api/v1/exception/400";

        ApiResponse<Map<String, String>> response =
                new ApiResponse<Map<String, String>>(false, detailMessage.toString(), errors, HttpStatus.BAD_REQUEST, moreInformation);
        log.error("{}\n{} {}", detailMessage, errors.toString(), exception);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /*
        MissingServletRequestPartException: This exception is thrown when the part of a multipart request not found
        MissingServletRequestParameterException: This exception is thrown when request missing parameter
    */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException exception, HttpHeaders headers, HttpStatusCode status,
            WebRequest request) {

        String message = exception.getParameterName() + " " + messageSourceHelper.getMessage("error.missingServletRequestParameterException");
        String detailMessage = exception.getLocalizedMessage();

        String moreInformation = "http://localhost:8080/api/v1/exception/400";

        ApiResponse<String> response = new ApiResponse<String>(false, message, detailMessage, HttpStatus.BAD_REQUEST, moreInformation);
        log.error(detailMessage, exception);
        return new ResponseEntity<>(response, headers, status);
    }

    /*
        TypeMismatchException: This exception is thrown when try to set bean property with wrong type.
        MethodArgumentTypeMismatchException: This exception is thrown when method argument is not the expected type
    */
    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<ApiResponse<String>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {

        String message = exception.getName() + " " + messageSourceHelper.getMessage("error.MethodArgumentTypeMismatchException")
                + exception.getRequiredType().getName();
        String detailMessage = exception.getLocalizedMessage();
        String moreInformation = "http://localhost:8080/api/v1/exception/400";

        ApiResponse<String> response = new ApiResponse<String>(false, message, detailMessage, HttpStatus.BAD_REQUEST, moreInformation);
        log.error(detailMessage, exception);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


}
