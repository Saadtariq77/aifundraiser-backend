package com.baitussalam.aifundraiser.entity;


import com.baitussalam.aifundraiser.config.JsonNodeConverter;
import jakarta.persistence.*;
import lombok.*;
import tools.jackson.databind.JsonNode;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "external_api_raw_data",
        indexes = {
                @Index(name = "idx_source", columnList = "source"),
                @Index(name = "idx_country", columnList = "country"),
                @Index(name = "idx_category", columnList = "category"),
                @Index(name = "idx_processed", columnList = "processed"),
                @Index(name = "idx_fetched_at", columnList = "fetched_at")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalApiRawData {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "source", nullable = false, length = 50)
    private String source;


    @Column(name = "country", length = 100)
    private String country;


    @Column(name = "category", length = 100)
    private String category;


    @Column(name = "request_url", columnDefinition = "TEXT")
    private String requestUrl;


    @Column(name = "request_parameters", columnDefinition = "JSON")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode requestParameters;


    @Column(name = "response_data", nullable = false, columnDefinition = "JSON")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode responseData;


    @Column(name = "processed")
    @Builder.Default
    private Boolean processed = false;


    @Column(name = "fetched_at", nullable = false)
    @Builder.Default
    private LocalDateTime fetchedAt = LocalDateTime.now();

}
