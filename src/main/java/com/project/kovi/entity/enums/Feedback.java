package com.project.kovi.entity.enums;

public enum Feedback {
    NO_FEEDBACK("NO_FEEDBACK"),
    VERY_GOOD("VERY_GOOD"),
    GOOD("GOOD"),
    AVERAGE("AVERAGE"),
    POOR("POOR"),
    VERY_POOR("VERY_POOR");

    private final String feedback;

    Feedback(final String feedback){
        this.feedback = feedback;
    }
    @Override
    public String toString(){
        return this.feedback;
    }
}
