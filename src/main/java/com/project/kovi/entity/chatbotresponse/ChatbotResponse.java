package com.project.kovi.entity.chatbotresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@ToString
public class ChatbotResponse implements Serializable {

    private String message;
    private FetchRes fetchRes;
    private List<String> queryRecommendation;

    public ChatbotResponse(){
        this.fetchRes = new FetchRes();
    }

}
