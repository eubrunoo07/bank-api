package com.bruno.api.brbank.exceptions;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

public class ApiErrors {
    @Getter
    private List<String> errors;

    public ApiErrors(String error){
        this.errors = Collections.singletonList(error);
    }

    public ApiErrors(List<String> errors){
        this.errors = errors;
    }
}
