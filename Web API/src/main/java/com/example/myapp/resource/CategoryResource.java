package com.example.myapp.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.myapp.constant.RolesConstants;
import com.example.myapp.dto.CategoryDTO;
import com.example.myapp.resource.errors.BadRequestAlertException;
import com.example.myapp.service.CategoryService;
import com.example.myapp.util.HeaderUtil;
import com.example.myapp.util.PaginationUtil;
import com.example.myapp.util.ResponseUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1.0/admin")
@RequiredArgsConstructor
@Slf4j
public class CategoryResource {
	private static final List<String> ALLOWED_ORDERED_PROPERTIES = Collections.unmodifiableList(Arrays.asList("id",
			"name", "code", "description", "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate"));

	private final CategoryService categoryService;

	@Value("${application.name}")
	private String applicationName;

	@RequestMapping(value = "/my-resource", method = RequestMethod.OPTIONS)
	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
	public ResponseEntity<?> options() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Allow", "GET, POST, PUT, DELETE");
		return new ResponseEntity<>(headers, HttpStatus.OK);
	}

	@PostMapping("/categories")
	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
	public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO)
			throws URISyntaxException {
		log.debug("REST request to save Category : {}", categoryDTO);

		if (categoryDTO.getId() != null) {
			throw new BadRequestAlertException("A new category cannot already have an ID");
		} else {
			CategoryDTO newCategory = categoryService.createCategory(categoryDTO);
			return ResponseEntity
					.created(new URI("/api/admin/categories/" + newCategory.getId())).headers(HeaderUtil
							.createAlert(applicationName, "categoryManagement.created", newCategory.getCategoryName()))
					.body(newCategory);
		}
	}

	@PutMapping("/categories")
	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
	public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
		log.debug("REST request to update Category : {}", categoryDTO);
		Optional<CategoryDTO> updatedCategory = categoryService.updateCategory(categoryDTO);
		return ResponseUtil.wrapOrNotFound(updatedCategory,
				HeaderUtil.createAlert(applicationName, "categoryManagement.updated", categoryDTO.getCategoryName()));
	}

	@GetMapping("/categories")
	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
	public ResponseEntity<List<CategoryDTO>> getAllCategories(
			@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
		log.debug("REST request to get all Category for an admin");
		if (!onlyContainsAllowedProperties(pageable)) {
			return ResponseEntity.badRequest().build();
		}

		final Page<CategoryDTO> page = categoryService.getAllCategories(pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	@GetMapping("/categories/{id}")
	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
	public ResponseEntity<CategoryDTO> getCategory(@PathVariable Integer id) {
		log.debug("REST request to get Category : {}", id);
		return ResponseUtil.wrapOrNotFound(categoryService.getCategory(id));
	}

	/*
	 * @GetMapping("/subCategories/search")
	 * 
	 * @PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")") public
	 * ResponseEntity<List<CategoryDTO>> searchCategory(
	 * 
	 * @org.springdoc.api.annotations.ParameterObject Pageable
	 * pageable, @RequestBody String subCategoryCode) {
	 * log.debug("REST request to search Categories for an admin"); if
	 * (!onlyContainsAllowedProperties(pageable)) { return
	 * ResponseEntity.badRequest().build(); } final Page<CategoryDTO> page =
	 * categoryService.searchCategory(pageable, subCategoryCode); HttpHeaders
	 * headers = PaginationUtil
	 * .generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest
	 * (), page); return new ResponseEntity<>(page.getContent(), headers,
	 * HttpStatus.OK); }
	 */

	@DeleteMapping("/categories")
	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
	public ResponseEntity<Void> deleteCategory(@RequestBody Integer[] ids) {
		log.debug("REST request to delete Categories");
		categoryService.deleteCategories(ids);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createAlert(applicationName, "categoryManagement.deleted", ids.toString())).build();
	}

	@DeleteMapping("/categories/{id}")
	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
	public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
		log.debug("REST request to delete Categories");
		categoryService.deleteCategory(id);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createAlert(applicationName, "categoryManagement.deleted", id.toString())).build();
	}

	private boolean onlyContainsAllowedProperties(Pageable pageable) {
		return pageable.getSort().stream().map(Sort.Order::getProperty).allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
	}
}
