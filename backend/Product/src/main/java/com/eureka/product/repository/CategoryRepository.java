package com.eureka.product.repository;

import com.eureka.product.dto.Category;
import com.eureka.product.dto.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,String> {

    Category findByName(String name);

    List<Category> findByDepthBetweenOrderByDepthAsc(int start, int end);

}