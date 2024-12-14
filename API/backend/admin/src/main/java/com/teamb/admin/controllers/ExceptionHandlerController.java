package com.teamb.admin.controllers;

import com.teamb.admin.exception.EntityNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerController {

    @ExceptionHandler(EntityNotFound.class)
    public ResponseEntity<ProblemDetail> handleNoEntityFound(EntityNotFound entityNotFound) {
        var problemDetails = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetails.setTitle(HttpStatus.NOT_FOUND.name());
        problemDetails.setDetail(entityNotFound.getMessage());
        problemDetails.setProperties(entityNotFound.getDetails());
        return new ResponseEntity<>(problemDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpectedError(Exception e) {
        var problemDetails = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetails.setTitle("Something went wrong");
        problemDetails.setDetail(e.getMessage());
        return ResponseEntity.internalServerError().body(problemDetails);
    }
}
