package com.proxy.pira.dto;

import com.sun.istack.NotNull;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaveTicketDto {

    @NotBlank
    private String title;
    @NotBlank
    private String desctipion;
    
}
