package com.project.kovi.entity.enums;

public enum IntentType {
    BASIC("Basic"),
    NEWS("News"),
    LIVE_COUNT("Live Count");

    private final String intentType;

    IntentType(String intentType){
        this.intentType = intentType;
    }

    @Override
    public String toString() {
        return intentType;
    }
}
