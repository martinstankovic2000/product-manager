package com.ingemark.productmanager.repository;

import com.ingemark.productmanager.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByCode(String code);

    @Query(value = "SELECT nextval('product_code_2025_seq')", nativeQuery = true)
    Long getNextCodeSequence();
}
