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
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CovidStats implements Serializable {

    private String id;

    @JsonProperty("Country")
    private String country;

    @JsonProperty("Continent")
    private String continent;

    @JsonProperty("TwoLetterSymbol")
    private String twoLetterSymbol;

    @JsonProperty("ThreeLetterSymbol")
    private String threeLetterSymbol;

    @JsonProperty("NewCases")
    private long newCases;

    @JsonProperty("TotalCases")
    private long totalCases;

    @JsonProperty("ActiveCases")
    private long activeCases;

    @JsonProperty("TotalDeaths")
    private long totalDeaths;

    @JsonProperty("NewDeaths")
    private long newDeaths;

    @JsonProperty("TotalRecovered")
    private long totalRecovered;

    @JsonProperty("NewRecovered")
    private long newRecovered;

    @JsonProperty("TotalTests")
    private long totalTests;

    @JsonProperty("Infection_Risk")
    private double infectionRisk;

    @JsonProperty("Case_Fatality_Rate")
    private double caseFatalityRate;

}
