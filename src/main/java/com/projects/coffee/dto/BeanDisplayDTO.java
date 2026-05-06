package com.projects.coffee.dto;

import lombok.Data;

@Data
public class BeanDisplayDTO {
    private Long id;
    private String flavor;
    private String origin;
    private String roast;
    private String createdBy;
    private Boolean isPublic;

    public BeanDisplayDTO(Long id, String flavor, String origin, String roast, String createdBy, Boolean isPublic) {
        this.id = id;
        this.flavor = flavor;
        this.origin = origin;
        this.roast = roast;
        this.createdBy = createdBy;
        this.isPublic = isPublic;
    }

    public BeanDisplayDTO() {}
}
