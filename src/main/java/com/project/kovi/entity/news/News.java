package com.project.kovi.entity.news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class News implements Serializable {

    @JsonProperty("_id")
    private String id;
    private String title;
    private String summary;
    private String country;
    private String media;
    private String link;
    private String language;
    private String rights;
    @JsonProperty("published_date")
    private String publishedDate;

}
