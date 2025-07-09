package org.study.app.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseError {
    public static final int UNPROCESSABLE_ENTITY = 422;

    private String message;
    private List<FieldError> errors;

    public static <T> ResponseError createFromValidation(Set<ConstraintViolation<T>> violations) {
        var errors = violations.stream().map(constViol ->
                new FieldError(constViol.getPropertyPath().toString(), constViol.getMessage()))
                .toList();
        return new ResponseError("Validation Errors", errors);
    }

    public Response withStatusCode(int code) {
        return Response.status(code).entity(this).build();
    }
}
