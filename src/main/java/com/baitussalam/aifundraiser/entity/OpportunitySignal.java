package com.baitussalam.aifundraiser.entity;

import com.baitussalam.aifundraiser.config.JsonNodeConverter;
import jakarta.persistence.*;
import lombok.*;
import tools.jackson.databind.JsonNode;
import java.time.LocalDateTime;

@Entity
@Table(name = "opportunity_signal", uniqueConstraints = @UniqueConstraint(columnNames = {"country", "category", "signal_type"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpportunitySignal {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(name = "signal_type", nullable = false, length = 50)
    private String signalType;

    @Column(nullable = false, length = 50)
    private String source;

    @Column(name = "raw_value")
    private Double rawValue;

    @Column(name = "normalized_score", nullable = false)
    private Double normalizedScore;

    @Column(columnDefinition = "JSON") @Convert(converter = JsonNodeConverter.class)
    private JsonNode evidence;

    @Column(name = "calculated_at", nullable = false) @Builder.Default
    private LocalDateTime calculatedAt = LocalDateTime.now();
}