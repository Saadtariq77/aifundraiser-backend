package com.baitussalam.aifundraiser.entity;

import com.baitussalam.aifundraiser.config.JsonNodeConverter;
import jakarta.persistence.*;
import lombok.*;
import tools.jackson.databind.JsonNode;
import java.time.LocalDateTime;

@Entity
@Table(name = "opportunity_score", uniqueConstraints = @UniqueConstraint(columnNames = {"country", "category"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpportunityScore {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String category;

    @Column(name = "total_score", nullable = false)
    private Double totalScore;

    @Column(nullable = false, length = 20)
    private String confidence;

    @Column(columnDefinition = "JSON") @Convert(converter = JsonNodeConverter.class)
    private JsonNode breakdown;

    @Column(name = "evidence_bundle", columnDefinition = "JSON") @Convert(converter = JsonNodeConverter.class)
    private JsonNode evidenceBundle;

    @Column(name = "calculated_at", nullable = false) @Builder.Default
    private LocalDateTime calculatedAt = LocalDateTime.now();

}
