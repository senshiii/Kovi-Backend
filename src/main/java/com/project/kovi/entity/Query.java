package com.project.kovi.entity;

import com.project.kovi.entity.enums.Feedback;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class Query {

    private String id;
    private String text;
    private Date createdAt;
    private String sessionId;
    private Feedback feedback;
    private QueryResponse response;

    public Query() {
        this.feedback = Feedback.NO_FEEDBACK;
        this.response = new QueryResponse();
    }

}
