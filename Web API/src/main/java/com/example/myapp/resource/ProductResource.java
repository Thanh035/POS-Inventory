//package com.example.myapp.resource;
//
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import javax.servlet.http.HttpServletResponse;
//import javax.validation.Valid;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.Resource;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestPart;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//import org.webjars.NotFoundException;
//
//import com.example.myapp.constant.RolesConstants;
//import com.example.myapp.dto.ProductDTO;
//import com.example.myapp.exception.StorageException;
//import com.example.myapp.resource.errors.BadRequestAlertException;
//import com.example.myapp.service.ProductService;
//import com.example.myapp.util.FileUtil;
//import com.example.myapp.util.HeaderUtil;
//import com.example.myapp.util.PaginationUtil;
//import com.example.myapp.util.ResponseUtil;
//import com.example.myapp.vm.PrintCodeVM;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/api/v1.0/admin")
//@RequiredArgsConstructor
//public class ProductResource {
//
//	private static final List<String> ALLOWED_ORDERED_PROPERTIES = Collections.unmodifiableList(
//			Arrays.asList("id", "parentCategory", "categoryName", "description", "categoryCode", "createdBy"));
//
//	private final Logger log = LoggerFactory.getLogger(ProductResource.class);
//
//	private final ProductService productService;
//
//	@Value("${application.name}")
//	private String applicationName;
//
//	@GetMapping("/products")
//	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
//	public ResponseEntity<List<ProductDTO>> getAllProducts(
//			@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
//		log.debug("REST request to get all Products for an admin");
//		if (!onlyContainsAllowProperties(pageable)) {
//			return ResponseEntity.badRequest().build();
//		}
//		final Page<ProductDTO> page = productService.getAllProducts(pageable);
//		HttpHeaders headers = PaginationUtil
//				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//	}
//
//	@GetMapping("/products/{id}")
//	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
//	public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
//		log.debug("REST request to get Product by ID: {}", id);
//		return ResponseUtil.wrapOrNotFound(productService.getProductById(id));
//	}
//
//	@GetMapping("/products/search")
//	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
//	public ResponseEntity<List<ProductDTO>> searchProduct(
//			@org.springdoc.api.annotations.ParameterObject Pageable pageable, @RequestBody ProductDTO productDTO) {
//		log.debug("REST request to search Products for an admin");
//		if (!onlyContainsAllowProperties(pageable)) {
//			return ResponseEntity.badRequest().build();
//		}
//		final Page<ProductDTO> page = productService.searchProduct(pageable, productDTO.getCategoryCode(),
//				productDTO.getSubCategoryCode());
//		HttpHeaders headers = PaginationUtil
//				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//	}
//
//	@PostMapping(path = "/products", consumes = { "multipart/form-data" })
//	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
//	public ResponseEntity<ProductDTO> createProduct(@Valid @RequestPart("product") ProductDTO productDTO,
//			@RequestPart("images") MultipartFile multipartFile) throws URISyntaxException, IOException {
//		log.debug("REST request to save Product : {}", productDTO);
//		if (productDTO.getId() != null) {
//			throw new BadRequestAlertException("A new product cannot already have an ID", "productManagement",
//					"idexists");
//		} else if (multipartFile.isEmpty()) {
//			throw new StorageException("Failed to store empty file");
//		} else {
//			ProductDTO newProduct = productService.createProduct(productDTO, multipartFile);
//			return ResponseEntity
//					.created(new URI("/api/v1.0/admin/products/" + newProduct.getId())).headers(HeaderUtil
//							.createAlert(applicationName, "productManagement.created", productDTO.getProductName()))
//					.body(newProduct);
//		}
//	}
//
//	@GetMapping(value = "/products/barcodes", produces = MediaType.IMAGE_PNG_VALUE)
//	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
//	public ResponseEntity<BufferedImage> barbecueEAN13Barcode(@RequestBody PrintCodeVM printCodeVM) throws Exception {
//		BufferedImage bufferedImage = productService.generateBarcode(printCodeVM.getCode(), printCodeVM.getWidth(),
//				printCodeVM.getHeight());
//		return new ResponseEntity<>(bufferedImage, HttpStatus.OK);
//	}
//
//	@GetMapping("products/qrcode")
//	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
//	public ResponseEntity<Void> qrcode(@RequestBody PrintCodeVM printCodeVM, HttpServletResponse response)
//			throws Exception {
//		log.debug("REST request to get data export by File PDF");
//		productService.generateQRCode(response, printCodeVM.getCode(), printCodeVM.getWidth(), printCodeVM.getHeight());
//		response.setContentType("image/png");
//		return ResponseEntity.noContent()
//				.headers(HeaderUtil.createAlert(applicationName, "productManagement.qrcode", printCodeVM.getCode()))
//				.build();
//	}
//
//	/*
//	 * @GetMapping("/products/export/pdf")
//	 * 
//	 * @PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")") public
//	 * ResponseEntity<Void> exportToPDF(@RequestBody Long[] ids, HttpServletResponse
//	 * response) throws DocumentException, IOException {
//	 * log.debug("REST request to export pdf file for Products");
//	 * 
//	 * productService.exportPDF(response, ids);
//	 * 
//	 * response.setContentType("application/octet-stream"); DateFormat dateFormatter
//	 * = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss"); String currentDateTime =
//	 * dateFormatter.format(new Date()); return ResponseEntity.noContent()
//	 * .header("Content-Disposition", "attachment; filename=products_" +
//	 * currentDateTime + ".pdf").build(); }
//	 */
//	/*
//	 * @GetMapping("/products/export/excel")
//	 * 
//	 * @PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")") public
//	 * ResponseEntity<Void> exportToExcel(@RequestBody FileVM fileVM,
//	 * HttpServletResponse response) throws IOException {
//	 * log.debug("REST request to export excel file for Products");
//	 * 
//	 * productService.exportExcel(response, fileVM.getFilePath(), fileVM.getIds());
//	 * 
//	 * response.setContentType("application/octet-stream"); DateFormat dateFormatter
//	 * = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss"); String currentDateTime =
//	 * dateFormatter.format(new Date()); return ResponseEntity.noContent()
//	 * .header("Content-Disposition", "attachment; filename=products_" +
//	 * currentDateTime + ".xlsx").build(); }
//	 */
//	@GetMapping("/products/dowload-example-csv")
//	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
//	public ResponseEntity<?> downloadFile() throws IOException {
//		Resource resource = null;
//		try {
//			resource = FileUtil.getFileAsResource("test.csv");
//		} catch (IOException e) {
//			return ResponseEntity.internalServerError().build();
//		}
//
//		if (resource == null) {
//			return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
//		}
//
//		String contentType = "application/octet-stream";
//		String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
//
//		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
//				.header(HttpHeaders.CONTENT_DISPOSITION, headerValue).body(resource);
//
//	}
//
//	@PostMapping("/products/upload-csv-file")
//	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
//	public ResponseEntity<Void> importCsvFile(@RequestParam("file") MultipartFile file) {
//		log.debug("REST request to import products by CSV Files: {}", file);
//		if (file.isEmpty()) {
//			throw new NotFoundException("Please select a CSV file to upload");
//		}
//		productService.importCsv(file);
//		return ResponseEntity.noContent().header("Content-Disposition", "attachment; filename=" + file).build();
//	}
//
//	@GetMapping("/products/upload-csv-file")
//	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
//	public ResponseEntity<List<ProductDTO>> uploadCsvFile(@RequestParam("file") MultipartFile file) {
//		log.debug("REST request to import products by CSV Files: {}", file);
//		if (file.isEmpty()) {
//			throw new NotFoundException("Please select a CSV file to upload");
//		}
//
//		List<ProductDTO> products = productService.uploadCsv(file);
//
//		return new ResponseEntity<>((products), HttpStatus.OK);
//	}
//
//	@PutMapping("/products")
//	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
//	public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestPart("product") ProductDTO productDTO,
//			@RequestPart("images") MultipartFile multipartFile) throws URISyntaxException, IOException {
//		log.debug("REST request to update Product: {}", productDTO);
//		if (multipartFile.isEmpty()) {
//			throw new StorageException("Failed to store empty file");
//		}
//		Optional<ProductDTO> updateProduct = productService.updateProduct(productDTO, multipartFile);
//		return ResponseUtil.wrapOrNotFound(updateProduct,
//				HeaderUtil.createAlert(applicationName, "productManagement.updated", productDTO.getProductName()));
//	}
//
//	@DeleteMapping("/products")
//	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
//	public ResponseEntity<Void> deleteProduct(@RequestBody Long[] ids) {
//		log.debug("REST request to delete Products");
//		productService.deleteProduct(ids);
//		return ResponseEntity.noContent()
//				.headers(HeaderUtil.createAlert(applicationName, "productManageent.deleted", ids.toString())).build();
//	}
//
//	private boolean onlyContainsAllowProperties(Pageable pageable) {
//		return pageable.getSort().stream().map(Sort.Order::getProperty).allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
//	}
//
//}
