package com.newdawn.tagvote.vote.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum VoteStatus {
    PROGRESS("progress"),
    END("end");

    private final String dbValue;

    VoteStatus(final String dbValue) {
        this.dbValue = dbValue;
    }

    public static VoteStatus from(final String dbValue) {
        return Arrays.stream(values())
                .filter(status -> status.dbValue.equalsIgnoreCase(dbValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown vote status: " + dbValue));
    }
}
