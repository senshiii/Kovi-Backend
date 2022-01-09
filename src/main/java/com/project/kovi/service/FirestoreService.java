package com.project.kovi.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.project.kovi.entity.Query;
import com.project.kovi.models.MessageListResponse;
import com.project.kovi.validation.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirestoreService {

    @Autowired
    private Firestore firestore;

    public Query getQueryById(final String queryId){
        DocumentReference docRef = firestore.collection("messages").document(queryId);
        DocumentSnapshot docSnap = null;
        try {
            docSnap = docRef.get().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if(docSnap == null || !docSnap.exists())
            throw new RuntimeException("404! Resource Not found");

        return docSnap.toObject(Query.class);
    }

    public MessageListResponse listQueriesBySessionId(final String sessionId){
        ValidationUtils.validateSessionId(sessionId, firestore);
        ApiFuture<QuerySnapshot> future = firestore.collection("messages").whereEqualTo("sessionId", sessionId).get();
        MessageListResponse response = new MessageListResponse();
        List<Query> queries = new ArrayList<>();
        try{
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for(QueryDocumentSnapshot doc : docs){
                queries.add(doc.toObject(Query.class));
            }
        }catch(InterruptedException | ExecutionException exc){
            exc.printStackTrace();
        }
        response.setSessionId(sessionId);
        response.setMessages(queries);
        return response;
    }

    public Query createQuery(Query query){
        DocumentReference docRef = firestore.collection("messages").document(query.getId());
        ApiFuture<WriteResult> future = docRef.set(query);
        DocumentSnapshot docSnap = null;
        try {
            WriteResult result = future.get();
            docSnap = docRef.get().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if(docSnap == null || !docSnap.exists())
            throw new RuntimeException("Failed to fetch created object");
        return docSnap.toObject(Query.class);
    }

    public ApiFuture<WriteResult> updateQueryAsync(String queryId, Map<String, Object> data){
        DocumentReference docRef = firestore.collection("messages").document(queryId);
        ApiFuture<WriteResult> future = null;
        try{
            if(!docRef.get().get().exists())
                throw new RuntimeException("404! Resource Not Found");

            future = docRef.update(data);

        }catch(InterruptedException | ExecutionException exc){
            exc.printStackTrace();
        }
        return future;
    }

    public Query updateQuery(String queryId, Map<String, Object> data){
        DocumentReference docRef = firestore.collection("messages").document(queryId);
        DocumentSnapshot docSnap = null;
        try{
            if(!docRef.get().get().exists())
                throw new RuntimeException("404! Resource Not Found");

            docRef.update(data);

            docSnap = docRef.get().get();

        }catch(InterruptedException | ExecutionException exc){
            exc.printStackTrace();
        }
        if(docSnap == null || !docSnap.exists())
            throw new RuntimeException("404!");
        return docSnap.toObject(Query.class);
    }

}
