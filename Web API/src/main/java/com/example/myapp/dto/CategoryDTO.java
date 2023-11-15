package com.example.myapp.dto;

import java.io.Serializable;

import com.example.myapp.domain.Category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class CategoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String categoryName;
    private String categoryCode;
    private String description;
    private String createdBy;

    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.categoryCode = category.getCode();
        this.categoryName = category.getName();
        this.description = category.getDescription();
        this.createdBy = category.getCreatedBy();
    }

}
