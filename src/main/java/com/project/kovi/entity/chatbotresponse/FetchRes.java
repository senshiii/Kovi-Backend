package com.project.kovi.entity.chatbotresponse;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class FetchRes implements Serializable {

    private FetchResNews news;
    private FetchResStats stats;
    private boolean hasResources;

    public FetchRes(){
        this.news = new FetchResNews();
        this.stats = new FetchResStats();
    }

}
