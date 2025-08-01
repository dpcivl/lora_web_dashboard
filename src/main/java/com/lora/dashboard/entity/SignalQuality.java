package com.lora.dashboard.entity;

public enum SignalQuality {
    EXCELLENT("매우 좋음"),
    GOOD("좋음"),
    FAIR("보통"),
    POOR("나쁨");

    private final String description;

    SignalQuality(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}