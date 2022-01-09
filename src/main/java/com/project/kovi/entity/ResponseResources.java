package com.project.kovi.entity;

import com.project.kovi.entity.news.NewsList;
import com.project.kovi.entity.stats.CovidStats;
import com.project.kovi.entity.stats.DailyStats;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ResponseResources implements Serializable {

    private boolean hasNews;
    private NewsList news;

    private boolean hasStats;
    private CovidStats stats;

    private boolean hasSeries;
    private List<DailyStats> dailySeries;

}
