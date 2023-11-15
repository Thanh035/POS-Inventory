package com.example.myapp.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myapp.domain.Category;
import com.example.myapp.domain.SubCategory;
import com.example.myapp.dto.SubCategoryDTO;
import com.example.myapp.repository.CategoryRepository;
import com.example.myapp.repository.SubCategoryRepository;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class SubCategoryService {

	private final Logger log = LoggerFactory.getLogger(SubCategoryService.class);

	private final SubCategoryRepository subCategoryRepository;

	private final CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	public Page<SubCategoryDTO> getAllSubCategories(Pageable pageable) {
		return subCategoryRepository.findAll(pageable).map(SubCategoryDTO::new);
	}

	@Transactional(readOnly = true)
	public Optional<SubCategoryDTO> getSubCategory(Integer id) {
		return subCategoryRepository.findById(id).map(SubCategoryDTO::new);
	}

	@Transactional(readOnly = true)
	public Page<SubCategoryDTO> searchSubCategory(Pageable pageable, String categoryCode, String categoryName) {
		return subCategoryRepository.findAllByCategoryNameLikeOrCategoryCodeLike(pageable, categoryName, categoryCode)
				.map(SubCategoryDTO::new);
	}

	public SubCategoryDTO createSubCategory(SubCategoryDTO subCategoryDTO, String parentCategoryCode) {
		SubCategory subCategory = new SubCategory();

		subCategory.setName(subCategoryDTO.getSubCategory());
		subCategory.setCode(subCategoryDTO.getCategoryCode());
		subCategory.setDescription(subCategoryDTO.getDescription());

		categoryRepository.findOneByCode(parentCategoryCode).ifPresent(category -> {
			subCategory.setCategory(category);
		});

		Optional<Category> category = categoryRepository.findOneByCode(parentCategoryCode);
		if (category.isPresent()) {
			subCategory.setCategory(category.get());
		}

		subCategoryRepository.save(subCategory);
		log.debug("Created Information For SubCategory: {}", subCategory);
		return new SubCategoryDTO(subCategory);
	}

	public Optional<SubCategoryDTO> updateSubCategory(SubCategoryDTO subCategoryDTO) {
		return Optional.of(subCategoryRepository.findById(subCategoryDTO.getId())).filter(Optional::isPresent)
				.map(Optional::get).map(subCategory -> {
					subCategory.setName(subCategoryDTO.getSubCategory());
					subCategory.setDescription(subCategoryDTO.getDescription());
					subCategory.setCode(subCategoryDTO.getSubCategoryCode());
					subCategoryRepository.save(subCategory);
					log.debug("Changed Information for Category: {}", subCategory);
					return subCategory;
				}).map(SubCategoryDTO::new);
	}

	public void deleteSubCategory(Integer[] ids) {
		for (Integer id : ids) {
			subCategoryRepository.findById(id).ifPresent(subCategory -> {
				subCategoryRepository.delete(subCategory);
				log.debug("Deleted Sub Category: {}", subCategory);
			});
		}
	}

}
