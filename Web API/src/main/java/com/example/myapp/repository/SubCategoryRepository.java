package com.example.myapp.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.myapp.domain.SubCategory;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Integer> {

	Optional<SubCategory> findOneByCode(String code);

	Page<SubCategory> findAllByCategoryNameLikeOrCategoryCodeLike(Pageable pageable, String categoryName,
			String categoryCode);

//	Page<Category> findAllBySubCategoryCodeLike(Pageable pageable, String subCategoryCode);
}
