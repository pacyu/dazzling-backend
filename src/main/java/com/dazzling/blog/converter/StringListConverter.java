package com.dazzling.blog.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;

@Converter
public class StringListConverter implements AttributeConverter<ArrayList<String>, String> {
    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(ArrayList<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return String.join(SPLIT_CHAR, list);
    }

    @Override
    public ArrayList<String> convertToEntityAttribute(String joined) {
        if (joined == null || joined.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String[] parts = joined.split(SPLIT_CHAR);
        return new ArrayList<>(Arrays.asList(parts));
    }
}