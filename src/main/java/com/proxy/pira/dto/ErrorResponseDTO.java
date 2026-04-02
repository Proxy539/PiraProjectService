package com.proxy.pira.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {

    private String service;
    private int status;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> validationErrors;

}
