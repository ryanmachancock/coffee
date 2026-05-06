package com.projects.coffee.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BeanDTO {
    @NotBlank(message = "Flavor is required")
    private String flavor;

    @NotBlank(message = "Origin is required")
    private String origin;

    @NotBlank(message = "Roast level is required")
    private String roast;

    private Boolean isPublic = true;

}
