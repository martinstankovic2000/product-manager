package com.ingemark.productmanager.repository;

import com.ingemark.productmanager.model.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByCode(String code);

    @Query(value = "SELECT nextval('product_code_2025_seq')", nativeQuery = true)
    Long getNextCodeSequence();

    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR :name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:minPriceEur IS NULL OR p.priceEur >= :minPriceEur) AND " +
            "(:maxPriceEur IS NULL OR p.priceEur <= :maxPriceEur) AND " +
            "(:minPriceUsd IS NULL OR p.priceUsd >= :minPriceUsd) AND " +
            "(:maxPriceUsd IS NULL OR p.priceUsd <= :maxPriceUsd)")
    Page<Product> findProductsByFilters(
            @Param("name") String name,
            @Param("minPriceEur") BigDecimal minPriceEur,
            @Param("maxPriceEur") BigDecimal maxPriceEur,
            @Param("minPriceUsd") BigDecimal minPriceUsd,
            @Param("maxPriceUsd") BigDecimal maxPriceUsd,
            Pageable pageable
    );
}
