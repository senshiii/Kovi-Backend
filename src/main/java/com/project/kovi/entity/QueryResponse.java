package com.project.kovi.entity;

import com.project.kovi.entity.enums.IntentType;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class QueryResponse implements Serializable {

    private String text;
    private Date createdAt;
    private IntentType intentType;
    private boolean hasResources;
    private ResponseResources resources;
    private List<String> suggestions;
    private boolean fetchingResources;

    public QueryResponse(){
        this.resources = new ResponseResources();
        this.createdAt = new Date();
    }

}
