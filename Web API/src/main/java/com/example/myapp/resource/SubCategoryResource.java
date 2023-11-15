//package com.example.myapp.resource;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import javax.validation.Valid;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//
//import com.example.myapp.constant.RolesConstants;
//import com.example.myapp.dto.SubCategoryDTO;
//import com.example.myapp.resource.errors.BadRequestAlertException;
//import com.example.myapp.service.SubCategoryService;
//import com.example.myapp.util.HeaderUtil;
//import com.example.myapp.util.PaginationUtil;
//import com.example.myapp.util.ResponseUtil;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/api/v1.0/admin")
//@RequiredArgsConstructor
//public class SubCategoryResource {
//
//	private static final List<String> ALLOWED_ORDERED_PROPERTIES = Collections.unmodifiableList(
//			Arrays.asList("id", "parentCategory", "categoryName", "description", "categoryCode", "createdBy"));
//
//	private final Logger log = LoggerFactory.getLogger(SubCategoryResource.class);
//
//	private final SubCategoryService subCategoryService;
//
////	private final SubCategoryRepository subCategoryRepository;
//
//	@Value("${application.name}")
//	private String applicationName;
//
//	@GetMapping("/subCategories")
//	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
//	public ResponseEntity<List<SubCategoryDTO>> getAllSubCategories(
//			@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
//		log.debug("REST request to get all Sub Categories for an admin");
//		if (!onlyContainsAllowProperties(pageable)) {
//			return ResponseEntity.badRequest().build();
//		}
//		final Page<SubCategoryDTO> page = subCategoryService.getAllSubCategories(pageable);
//		HttpHeaders headers = PaginationUtil
//				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//	}
//
//	@GetMapping("/subCategories/{id}")
//	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
//	public ResponseEntity<SubCategoryDTO> getSubCategory(@PathVariable Integer id) {
//		log.debug("REST request to get Sub Category by ID : {}", id);
//		return ResponseUtil.wrapOrNotFound(subCategoryService.getSubCategory(id));
//	}
//
//	@GetMapping("/subCategories/search")
//	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
//	public ResponseEntity<List<SubCategoryDTO>> searchSubCategory(
//			@org.springdoc.api.annotations.ParameterObject Pageable pageable,
//			@RequestBody SubCategoryDTO subCategoryDTO) {
//		log.debug("REST request to search Products for an admin");
//		if (!onlyContainsAllowProperties(pageable)) {
//			return ResponseEntity.badRequest().build();
//		}
//		final Page<SubCategoryDTO> page = subCategoryService.searchSubCategory(pageable,
//				subCategoryDTO.getCategoryCode(), subCategoryDTO.getCategory());
//		HttpHeaders headers = PaginationUtil
//				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//	}
//
//	@PostMapping("/subCategories")
//	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
//	public ResponseEntity<SubCategoryDTO> createSubCategories(@Valid @RequestBody SubCategoryDTO subCategoryDTO)
//			throws URISyntaxException {
//		log.debug("REST request to save Sub Category : {}", subCategoryDTO);
//		if (subCategoryDTO.getId() != null) {
//			throw new BadRequestAlertException("A new sub category cannot already have an ID", "subCategoryManagement",
//					"idexists");
//		}
////		else if (subCategoryRepository.findOneByCode(subCategoryDTO.getCategoryCode()).isPresent()) {
////			throw new CodeAlradyUsedException();
////		}
//		else {
//			SubCategoryDTO newSubCategory = subCategoryService.createSubCategory(subCategoryDTO,
//					subCategoryDTO.getCategoryCode());
//			return ResponseEntity.created(new URI("/api/admin/subCategories/" + newSubCategory.getId()))
//					.headers(HeaderUtil.createAlert(applicationName, "subCategoriesManagement.created",
//							newSubCategory.getCategory()))
//					.body(newSubCategory);
//		}
//	}
//
//	@PutMapping("/subCategories")
//	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
//	public ResponseEntity<SubCategoryDTO> updateSubCategory(@Valid @RequestBody SubCategoryDTO subCategoryDTO) {
//		log.debug("REST request to update Sub Category : {}", subCategoryDTO);
////		if (subCategoryRepository.findOneByCode(subCategoryDTO.getCategoryCode()).isPresent()) {
////			throw new CodeAlradyUsedException();
////		}
//		Optional<SubCategoryDTO> updatedSubCategory = subCategoryService.updateSubCategory(subCategoryDTO);
//
//		return ResponseUtil.wrapOrNotFound(updatedSubCategory, HeaderUtil.createAlert(applicationName,
//				"subCategoryManagement.updated", subCategoryDTO.getSubCategory()));
//	}
//
//	@DeleteMapping("/subCategories")
//	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
//	public ResponseEntity<Void> deleteSubCategory(@RequestBody Integer[] ids) {
//		log.debug("REST request to delete Sub Categories");
//		subCategoryService.deleteSubCategory(ids);
//		return ResponseEntity.noContent()
//				.headers(HeaderUtil.createAlert(applicationName, "subCategoryManagement.deleted", ids.toString()))
//				.build();
//	}
//
//	private boolean onlyContainsAllowProperties(Pageable pageable) {
//		return pageable.getSort().stream().map(Sort.Order::getProperty).allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
//	}
//
//}
