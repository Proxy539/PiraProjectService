package com.proxy.pira.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request DTO for updating an existing project's title and description. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProjectDto extends RequestDto {

    /** The new project title; must not be blank. */
    @NotBlank
    private String title;

    /** The new project description; must not be blank. */
    @NotBlank
    private String description;

}
