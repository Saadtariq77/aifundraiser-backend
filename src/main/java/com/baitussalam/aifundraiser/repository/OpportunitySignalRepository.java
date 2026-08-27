package com.baitussalam.aifundraiser.repository;

import com.baitussalam.aifundraiser.entity.OpportunitySignal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OpportunitySignalRepository extends JpaRepository<OpportunitySignal, Long> {

    Optional<OpportunitySignal> findByCountryAndCategoryAndSignalType(String country,
                                                                      String category,
                                                                      String signalType);

    List<OpportunitySignal> findByCountryAndCategory(String country, String category);

    List<OpportunitySignal> findByCountry(String country);
}
