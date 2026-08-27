package com.baitussalam.aifundraiser.repository;

import com.baitussalam.aifundraiser.entity.OpportunityScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OpportunityScoreRepository extends JpaRepository<OpportunityScore, Long> {
    Optional<OpportunityScore> findByCountryAndCategory(String country, String category);
    List<OpportunityScore> findAllByOrderByTotalScoreDesc();
    List<OpportunityScore> findAllByCountryIgnoreCaseOrderByCalculatedAtDesc(String country);

    // PAGINATED
    Page<OpportunityScore> findByCountryIgnoreCase(String country, Pageable pageable);
    Page<OpportunityScore> findByCategoryIgnoreCase(String category, Pageable pageable);
    Page<OpportunityScore> findByCountryIgnoreCaseAndCategoryIgnoreCase(String country, String category, Pageable pageable);
}