package com.example.myapp.dto;

import java.io.Serializable;

import org.springframework.data.annotation.Transient;

import com.example.myapp.domain.Product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class ProductDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	private String productName;

	private String productImage;

	private String SKU;

	private String subCategory;

	private String subCategoryCode;

	private String category;

	private String categoryCode;

	private String description;

	private Boolean status;

	private Double price;

	private Integer quantity;

	private Integer discountType;

	private String createdBy;
	
	@Transient
	public String getPhotosImagePath(String image) {
		if (image == null || id == null)
			return null;
		return "/product-photos/" + id + "/" + image;
	}

	public ProductDTO(Product product) {
		this.id = product.getId();
		if (product.getCategory() != null) {
			this.category = product.getCategory().getName();
			this.categoryCode = product.getCategory().getCode();
		}
		if (product.getSubCategory() != null) {
			this.subCategory = product.getSubCategory().getName();
			this.subCategoryCode = product.getSubCategory().getCode();
		}
		this.productName = product.getProductName();
		this.productImage = getPhotosImagePath(product.getImage());
		this.SKU = product.getSKU();
		this.description = product.getDescription();
		this.status = product.getStatus();
		this.price = product.getPrice();
		this.quantity = product.getQuantity();
		this.discountType = product.getDiscountType();
		this.createdBy = product.getCreatedBy();
	}

}
