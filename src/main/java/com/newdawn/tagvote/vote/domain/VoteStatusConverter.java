package com.newdawn.tagvote.vote.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VoteStatusConverter implements AttributeConverter<VoteStatus, String> {

    @Override
    public String convertToDatabaseColumn(final VoteStatus attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public VoteStatus convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : VoteStatus.from(dbData);
    }
}
