package com.example.myapp.dto;

import java.io.Serializable;

import com.example.myapp.domain.SubCategory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class SubCategoryDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String subCategory;
	private String subCategoryCode;
	private String description;
	private String createdBy;

	private String category;
	private String categoryCode;

	public SubCategoryDTO(SubCategory subCategory) {
		this.id = subCategory.getId();
		if (subCategory.getCategory() != null) {
			this.category = subCategory.getCategory().getName();
			this.categoryCode = subCategory.getCategory().getCode();
		}
		this.subCategoryCode = subCategory.getCode();
		this.subCategory = subCategory.getName();
		this.description = subCategory.getDescription();
		this.createdBy = subCategory.getCreatedBy();
	}
}
