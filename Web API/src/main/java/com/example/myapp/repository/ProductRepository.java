package com.example.myapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.myapp.domain.Category;
import com.example.myapp.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	Page<Product> findAllByIdNotNullAndStatusIsTrue(Pageable pageable);

	Page<Product> findAllByCategory(Pageable pageable, Category category);

//	@Query("select e from Product e where lower(e.firstName) like lower(concat('%', :search, '%')) "
//			+ "or lower(e.lastName) like lower(concat('%', :search, '%'))")
	Page<Product> findAllByCategoryCodeLikeOrSubCategoryCodeLike(Pageable pageable,String categoryCode,String subCategoryCode);
}
