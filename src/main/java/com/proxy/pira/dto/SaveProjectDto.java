package com.proxy.pira.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request DTO for creating a new project. Both fields are mandatory. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaveProjectDto extends RequestDto {

    /** The project title; must not be blank. */
    @NotBlank
    private String title;

    /** A short description of the project; must not be blank. */
    @NotBlank
    private String description;

}
