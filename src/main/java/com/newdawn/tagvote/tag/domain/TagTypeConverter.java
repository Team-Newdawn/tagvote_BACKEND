package com.newdawn.tagvote.tag.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TagTypeConverter implements AttributeConverter<TagType, String> {

    @Override
    public String convertToDatabaseColumn(final TagType attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public TagType convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : TagType.from(dbData);
    }
}
