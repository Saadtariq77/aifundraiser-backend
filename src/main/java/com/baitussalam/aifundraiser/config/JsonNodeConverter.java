package com.baitussalam.aifundraiser.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Converter
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {


    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public String convertToDatabaseColumn(JsonNode attribute) {

        try {
            return attribute == null ? null :
                    objectMapper.writeValueAsString(attribute);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to convert JSON",
                    e
            );
        }
    }


    @Override
    public JsonNode convertToEntityAttribute(String dbData) {

        try {
            return dbData == null ? null :
                    objectMapper.readTree(dbData);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to read JSON",
                    e
            );
        }
    }
}
