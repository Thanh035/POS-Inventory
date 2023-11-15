package com.example.myapp.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myapp.domain.Category;
import com.example.myapp.dto.CategoryDTO;
import com.example.myapp.repository.CategoryRepository;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class CategoryService {
	private final Logger log = LoggerFactory.getLogger(CategoryService.class);

	private final CategoryRepository categoryRepository;

//	private final SubCategoryRepository subCategoryRepository;

	@Transactional(readOnly = true)
	public Page<CategoryDTO> getAllCategories(Pageable pageable) {
		return categoryRepository.findAll(pageable).map(CategoryDTO::new);
	}

	@Transactional(readOnly = true)
	public Optional<CategoryDTO> getCategory(Integer id) {
		return categoryRepository.findById(id).map(CategoryDTO::new);
	}

	public CategoryDTO createCategory(CategoryDTO categoryDTO) {
		Category category = new Category();

		category.setName(categoryDTO.getCategoryName());
		category.setDescription(categoryDTO.getDescription());
		category.setCode(categoryDTO.getCategoryCode());

		categoryRepository.save(category);
		log.debug("Created Information for Category: {}", category);
		return new CategoryDTO(category);
	}

	public Optional<CategoryDTO> updateCategory(CategoryDTO categoryDTO) {
		return Optional.of(categoryRepository.findById(categoryDTO.getId())).filter(Optional::isPresent)
				.map(Optional::get).map(category -> {
					category.setName(categoryDTO.getCategoryName());
					category.setDescription(categoryDTO.getDescription());
					category.setCode(categoryDTO.getCategoryCode());
					categoryRepository.save(category);
					log.debug("Changed Information for Category: {}", category);
					return category;
				}).map(CategoryDTO::new);
	}

	public void deleteCategories(Integer[] ids) {
		for (Integer id : ids) {
			categoryRepository.findById(id).ifPresent(category -> {
				categoryRepository.delete(category);
				log.debug("Deleted Category: {}", category);
			});
		}
	}

	public void deleteCategory(Integer id) {
		categoryRepository.findById(id).ifPresent(category -> {
			categoryRepository.delete(category);
			log.debug("Deleted Category: {}", category);
		});
	}

}
