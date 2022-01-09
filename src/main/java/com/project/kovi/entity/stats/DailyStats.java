package com.project.kovi.entity.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyStats implements Serializable {

    @JsonProperty("Country")
    private String country;

    @JsonProperty("Continent")
    private String continent;

    // TODO: Add Deserialization Class
    private String date;

    @JsonProperty("total_cases")
    private long totalCases;

    @JsonProperty("total_deaths")
    private long totalDeaths;

}
