package com.example.myapp.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.myapp.dto.CategoryDTO;
import com.example.myapp.service.CategoryService;
import com.example.myapp.util.PaginationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
@Slf4j
public class PublicCategoryResource {

	@Value("${application.name}")
	private String applicationName;

	private final CategoryService categoryService;

	@GetMapping("/categories")
	public ResponseEntity<List<CategoryDTO>> getAllPublicCategories(
			@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
		log.debug("REST request to get all Products for Public");

		final Page<CategoryDTO> page = categoryService.getAllCategories(pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

}
