package com.newdawn.tagvote.tag.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TagType {
    TEXT("text"),
    PHOTO("photo"),
    VIDEO("video");

    private final String dbValue;

    TagType(final String dbValue) {
        this.dbValue = dbValue;
    }

    public static TagType from(final String dbValue) {
        return Arrays.stream(values())
                .filter(type -> type.dbValue.equalsIgnoreCase(dbValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown tag type: " + dbValue));
    }
}
