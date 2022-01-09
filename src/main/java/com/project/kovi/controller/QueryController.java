package com.project.kovi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.project.kovi.entity.chatbotresponse.ChatbotResponse;
import com.project.kovi.entity.enums.Feedback;
import com.project.kovi.entity.Query;
import com.project.kovi.models.MessageBody;
import com.project.kovi.service.IntentService;
import com.project.kovi.validation.Utils;
import com.project.kovi.validation.ValidationUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/query")
@CrossOrigin(origins = {"http://localhost:3000", "https://kovi-2b0b0.web.app", "https://kovi-2b0b0.firebaseapp.com"})
public class QueryController {

    @Autowired
    private Firestore firestore;

    @Autowired
    private IntentService intentService;

    @PostMapping
    public @ResponseBody
    ResponseEntity<Query> createQuery(@RequestBody MessageBody msgBody,
                                        @RequestParam("sessionId") String sessionId){
        // Check If Session Exists
        ValidationUtils.validateSessionId(sessionId, firestore);

        String message = msgBody.getMessage(), urlEncodedMessage = null;

        Query query = new Query();
        query.setId(Utils.newId());
        query.setSessionId(sessionId);
        query.setText(message);
        query.setCreatedAt(new Date());

        // Call chatbot

        try{
            urlEncodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
        }catch(UnsupportedEncodingException e){
            throw new RuntimeException("Server Error");
        }

        OkHttpClient client = new OkHttpClient();
        Request chatbotReq = new Request.Builder().url("https://kovi-chatbot-backend.herokuapp.com/" +
                "prediction?query="+urlEncodedMessage).build();
        Response clientRes = null;
        ChatbotResponse chatbotResponse = null;
        try {
            clientRes = client.newCall(chatbotReq).execute();
            ObjectMapper objectMapper = new ObjectMapper();
            chatbotResponse = objectMapper.readValue(clientRes.body().byteStream(), ChatbotResponse.class);
            System.out.println("Message = " + message + " Chatbot Response = " + chatbotResponse);
            query.getResponse().setText(chatbotResponse.getMessage());
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to understand query");
        }

        // Async Resource Loading
        // Responds with Pusher Message
        if(chatbotResponse.getFetchRes().getNews().isPresent()){
            query.getResponse().setFetchingResources(true);
            String keyword = chatbotResponse.getFetchRes().getNews().getKeyword();
            String location = chatbotResponse.getFetchRes().getNews().getLocation();
            intentService.getNews(query.getId(), keyword, location, sessionId);
        }
        if(chatbotResponse.getFetchRes().getStats().isPresent()){
            query.getResponse().setFetchingResources(true);
            String loc = chatbotResponse.getFetchRes().getStats().getLocation();
            intentService.getStats(query.getId(), loc, sessionId);
        }

        query.getResponse().setSuggestions(chatbotResponse.getQueryRecommendation());

        // Write to Firestore
        DocumentReference msgDocRef = firestore.collection("messages").document(query.getId());
        ApiFuture<WriteResult> writeResult = msgDocRef.set(query);
        try {
            System.out.println("Document Created At = " + writeResult.get().getUpdateTime());
        }catch(InterruptedException | ExecutionException exc){
            throw new RuntimeException("Could not create Message");
        }

        return ResponseEntity.status(HttpStatus.CREATED.value()).body(query);
    }

    @PutMapping("/:messageId/feedback")
    public @ResponseBody
    Query updateFeedback(@RequestParam("sessionId") String sessionId,
                         @PathVariable String messageId,
                         @RequestParam("feedback") Feedback feedback){
        ValidationUtils.validateSessionId(sessionId, firestore);

        if(messageId == null || messageId.isBlank())
            throw new RuntimeException("Invalid Message Id");

        Query msg = null;

        ApiFuture<DocumentSnapshot> docSnap = firestore.collection("messages")
                .document("messagedId").get();

        try{
            DocumentSnapshot doc = docSnap.get();
            if(!doc.exists())
                throw new RuntimeException("Message Not Found");
            msg = doc.toObject(Query.class);
            msg.setId(doc.getId());
        }catch(InterruptedException | ExecutionException e){
            throw new RuntimeException("Failed to Load Message");
        }

        msg.setFeedback(feedback);
        ApiFuture<WriteResult> writeRes = firestore.collection("messages")
                .document(msg.getId()).update("feedback", feedback);

        return msg;
    }

}
