package hu.paulintamas.foodorderingsystem.application;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {Exception.class})
    public ErrorDTO handleException(Exception exception) {
        log.error("Message='" + exception.getMessage() + "'", exception);
        return ErrorDTO.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                // [KNOWLEDGE]
                // Dio not show exact error message due to security reasons!
                .message("Unexpected error!")
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {ValidationException.class})
    public ErrorDTO handleException(ValidationException validationException) {
        log.error("Message='" + validationException.getMessage() + "'", validationException);
        if (validationException instanceof ConstraintViolationException) {// From @Max, etc annotations...
            var violations = extractViolations((ConstraintViolationException) validationException);
            return ErrorDTO.builder()
                    .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .message(violations)
                    .build();
        } else {
            return ErrorDTO.builder()
                    .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .message(validationException.getMessage())
                    .build();
        }
    }

    private String extractViolations(ConstraintViolationException constraintViolationException) {
        return constraintViolationException.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("--"));
    }

}
