package com.project.kovi.models;

import com.project.kovi.entity.Query;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MessageListResponse {

    private String sessionId;
    private List<Query> messages;

}
