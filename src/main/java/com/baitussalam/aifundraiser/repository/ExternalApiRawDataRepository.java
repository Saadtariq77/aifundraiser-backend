package com.baitussalam.aifundraiser.repository;

import com.baitussalam.aifundraiser.entity.ExternalApiRawData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExternalApiRawDataRepository extends JpaRepository<ExternalApiRawData, Long> {

    List<ExternalApiRawData> findByProcessedFalse();

    List<ExternalApiRawData> findBySourceAndCountry(String source, String country);

    List<ExternalApiRawData> findByCountry(String country);

    List<ExternalApiRawData> findByCountryIgnoreCaseOrderByFetchedAtDesc(String country);
}
