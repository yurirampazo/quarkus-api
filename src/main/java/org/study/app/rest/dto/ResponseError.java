package org.study.app.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Set;

public class ResponseError {
    public static final int UNPROCESSABLE_ENTITY = 422;

    private String message;
    private List<FieldError> errors;

    public ResponseError(String message, List<FieldError> errors) {
        this.message = message;
        this.errors = errors;
    }

    public static <T> ResponseError createFromValidation(Set<ConstraintViolation<T>> violations) {
        var errors = violations.stream().map(constViol ->
                new FieldError(constViol.getPropertyPath().toString(), constViol.getMessage()))
                .toList();
        return new ResponseError("Validation Errors", errors);
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public void setErrors(List<FieldError> errors) {
        this.errors = errors;
    }

    public Response withStatusCode(int code) {
        return Response.status(code).entity(this).build();
    }
}
