//package com.example.myapp.service;
//
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.List;
//import java.util.Optional;
//
//import javax.servlet.http.HttpServletResponse;
//
//import org.modelmapper.ModelMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.StringUtils;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.example.myapp.domain.Category;
//import com.example.myapp.domain.Product;
//import com.example.myapp.domain.SubCategory;
//import com.example.myapp.dto.ProductDTO;
//import com.example.myapp.helper.CSVHelper;
//import com.example.myapp.helper.ZXingHelper;
//import com.example.myapp.repository.CategoryRepository;
//import com.example.myapp.repository.ProductRepository;
//import com.example.myapp.repository.SubCategoryRepository;
//import com.example.myapp.util.FileUtil;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class ProductService {
//
//	private final Logger log = LoggerFactory.getLogger(SubCategoryService.class);
//
//	private final SubCategoryRepository subCategoryRepository;
//
//	private final CategoryRepository categoryRepository;
//
//	private final ProductRepository productRepository;
//
//	private final ModelMapper mapper = new ModelMapper();
//
//	@Transactional(readOnly = true)
//	public Page<ProductDTO> getAllProducts(Pageable pageable) {
//		return productRepository.findAll(pageable).map(ProductDTO::new);
//	}
//
//	@Transactional(readOnly = true)
//	public Optional<ProductDTO> getProductById(Long id) {
//		return productRepository.findById(id).map(ProductDTO::new);
//	}
//
//	@Transactional(readOnly = true)
//	public Page<ProductDTO> searchProduct(Pageable pageable, String categoryCode, String subCategoryCode) {
//		return productRepository
//				.findAllByCategoryCodeLikeOrSubCategoryCodeLike(pageable, categoryCode, subCategoryCode)
//				.map(ProductDTO::new);
//	}
//
//	@Transactional(readOnly = true)
//	public Page<ProductDTO> getAllPublicProducts(Pageable pageable) {
//		return productRepository.findAllByIdNotNullAndStatusIsTrue(pageable).map(ProductDTO::new);
//	}
//
//	public ProductDTO createProduct(ProductDTO productDTO, MultipartFile multipartFile) throws IOException {
//		Product product = mapper.map(productDTO, Product.class);
//
//		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
//		if (fileName != null) {
//			product.setImage(fileName);
//		}
//
//		Optional<Category> category = categoryRepository.findOneByCode(productDTO.getCategoryCode());
//		if (category.isPresent()) {
//			product.setCategory(category.get());
//		}
//
//		Optional<SubCategory> subCategory = subCategoryRepository.findOneByCode(productDTO.getSubCategoryCode());
//		if (subCategory.isPresent()) {
//			product.setSubCategory(subCategory.get());
//		}
//
//		Product saveProduct = productRepository.save(product);
//
//		String uploadDir = "upload/product-photos/" + saveProduct.getId();
//		FileUtil.saveFile(uploadDir, fileName, multipartFile);
//
//		log.debug("Created Information For Product: {}", product);
//		return new ProductDTO(product);
//	}
//
//	public Optional<ProductDTO> updateProduct(ProductDTO productDTO, MultipartFile multipartFile) throws IOException {
//		return Optional.of(productRepository.findById(productDTO.getId())).filter(Optional::isPresent)
//				.map(Optional::get).map(product -> {
//					product = mapper.map(productDTO, Product.class);
//
//					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
//					if (fileName != null) {
//						product.setImage(fileName);
//					}
//
//					Optional<Category> category = categoryRepository.findOneByCode(productDTO.getCategoryCode());
//					if (category.isPresent()) {
//						product.setCategory(category.get());
//					}
//
//					Optional<SubCategory> subCategory = subCategoryRepository
//							.findOneByCode(productDTO.getSubCategoryCode());
//					if (subCategory.isPresent()) {
//						product.setSubCategory(subCategory.get());
//					}
//
//					Product updateProduct = productRepository.save(product);
//
//					String uploadDir = "upload/product-photos/" + updateProduct.getId();
//					try {
//						FileUtil.deleteFolder(uploadDir);
//						FileUtil.saveFile(uploadDir, fileName, multipartFile);
//
//					} catch (IOException e) {
//						log.error(e.getMessage());
//					}
//
//					log.debug("Changed Information for Product: {}", product);
//					return product;
//				}).map(ProductDTO::new);
//	}
//
//	public void deleteProduct(Long[] ids) {
//		for (Long id : ids) {
//			productRepository.findById(id).ifPresent(product -> {
//				productRepository.delete(product);
//				String deleteDir = "upload/product-photos/" + id;
//				try {
//					FileUtil.deleteFolder(deleteDir);
//				} catch (IOException e) {
//					log.error(e.getMessage());
//				}
//
//				log.debug("Deleted Product: {}", product);
//			});
//		}
//	}
//
//	/*public void exportExcel(HttpServletResponse response, String filename, Long[] ids) {
//		List<Product> products = new ArrayList<>();
//		if (ids != null) {
//			for (Long id : ids) {
//				productRepository.findById(id).ifPresent(product -> {
//					products.add(product);
//				});
//			}
//		} else {
//			productRepository.findAll().stream().forEach(product -> {
//				products.add(product);
//			});
//		}
//
//		String excelFilePath = filename;
//		try {
//			ProductExcelHelper productExcelHelper = new ProductExcelHelper(products);
//			productExcelHelper.writeExcel(excelFilePath, response);
//			log.debug("Export Products: {}", products);
//		} catch (IOException e) {
//			log.error(e.getMessage());
//		}
//	}*/
//
//	public List<ProductDTO> uploadCsv(MultipartFile file) {
//		try {
//			return CSVHelper.csvToProducts(file.getInputStream());
//		} catch (IOException e) {
//			throw new RuntimeException("fail to store csv data: " + e.getMessage());
//		}
//	}
//
//	public void importCsv(MultipartFile file) {
//		List<ProductDTO> products;
//		try {
//			products = CSVHelper.csvToProducts(file.getInputStream());
//			products.stream().forEach(productDTO -> {
//				Product product = mapper.map(productDTO, Product.class);
//				categoryRepository.findOneByCode(productDTO.getCategoryCode()).ifPresent(category -> {
//					product.setCategory(category);
//				});
//				productRepository.save(product);
//			});
//		} catch (IOException e) {
//			throw new RuntimeException("fail to store csv data: " + e.getMessage());
//		}
//	}
//
//	/*public void exportPDF(HttpServletResponse response, Long[] ids) throws IOException {
//		List<Product> listProducts = new ArrayList<>();
//		if (ids.length > 0) {
//			for (Long id : ids) {
//				productRepository.findById(id).ifPresent(product -> {
//					listProducts.add(product);
//				});
//			}
//		} else {
//			productRepository.findAll().stream().forEach(product -> {
//				listProducts.add(product);
//			});
//		}
//
//		if (!listProducts.isEmpty()) {
//			ProductPDF exporter = new ProductPDF(listProducts);
//			exporter.export(response);
//			log.debug("Export information For Products: {}", listProducts);
//		}
//	}*/
//
//	public void generateQRCode(HttpServletResponse response, String id, Integer width, Integer height)
//			throws IOException {
//		OutputStream outputStream = response.getOutputStream();
//		outputStream.write(ZXingHelper.getQRCodeImage(id, width, height));
//		outputStream.flush();
//		outputStream.close();
//	}
//
//	public BufferedImage generateBarcode(String barcode, Integer width, Integer height) throws Exception {
//		return ZXingHelper.generateEAN13BarcodeImage(barcode, width, height);
//	}
//
//}
