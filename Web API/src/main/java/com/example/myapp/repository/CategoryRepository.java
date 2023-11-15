package com.example.myapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.myapp.domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
	Optional<Category> findOneByCode(String code);

//	Page<Category> \(Pageable pageable, Integer[] ids);
	
}
