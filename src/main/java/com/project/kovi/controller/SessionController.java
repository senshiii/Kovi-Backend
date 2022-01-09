package com.project.kovi.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.project.kovi.entity.Query;
import com.project.kovi.models.MessageListResponse;
import com.project.kovi.validation.Utils;
import com.project.kovi.validation.ValidationUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/session")
@CrossOrigin(origins = {"http://localhost:3000", "https://kovi-2b0b0.web.app", "https://kovi-2b0b0.firebaseapp.com"})
public class SessionController {

    @Autowired
    private Firestore firestore;

    @GetMapping("/new")
    public @ResponseBody
    ResponseEntity<Map<String, Object>> newSession(){
        System.out.println("SessionController::newSession");
        String sessionId = Utils.newId();
        DocumentReference docRef = firestore.collection("sessions").document(sessionId);
        Map<String, Object> data = new HashMap<>();
        data.put("createdAt", new Date());
        data.put("sessionId", sessionId);
        data.put("userDetails", new HashMap<>());
        ApiFuture<WriteResult> result = docRef.set(data);
        try {
            System.out.println("Created new session at " + result.get().getUpdateTime());
        }catch (InterruptedException | ExecutionException e){
            System.out.println("Error Creating Session " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error Creating Session");
        }
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(data);
    }

    @GetMapping
    public ResponseEntity<?> sessionInfo(@RequestParam String sessionId){
        ValidationUtils.validateSessionId(sessionId, firestore);
        ApiFuture<DocumentSnapshot> docSnap = firestore.collection("sessions").document(sessionId).get();
        Map<String, Object> data = new HashMap<>();
        try{
            DocumentSnapshot sessionDoc = docSnap.get();
            data.put("sessionId", sessionId);
            data.putAll(sessionDoc.getData());
        }catch(InterruptedException | ExecutionException exc){
            throw new RuntimeException("Failed to load Session Information. Try again.");
        }
        return ResponseEntity.ok(data);
    }

    @GetMapping("/thread")
    public @ResponseBody ResponseEntity<MessageListResponse> retrieveMessages(@RequestParam("sessionId") String sessionId){
        ValidationUtils.validateSessionId(sessionId, firestore);

        MessageListResponse res = new MessageListResponse();
        ApiFuture<QuerySnapshot> readRes = firestore.collection("messages")
                .whereEqualTo("sessionId", sessionId)
//                .orderBy("createdAt", com.google.cloud.firestore.Query.Direction.DESCENDING)
                .get();
        try {
            List<Query> msgList = new ArrayList<>();
            res.setSessionId(sessionId);
            List<QueryDocumentSnapshot> messages = readRes.get().getDocuments();
            for(QueryDocumentSnapshot doc : messages){
                Query msgQuery = doc.toObject(Query.class);
                msgQuery.setId(doc.getId());
                msgList.add(msgQuery);
            }
            res.setMessages(msgList);
        }catch(InterruptedException | ExecutionException exc){
            exc.printStackTrace();
        }

        return ResponseEntity.ok(res);
    }

}
