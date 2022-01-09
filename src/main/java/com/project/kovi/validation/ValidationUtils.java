package com.project.kovi.validation;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class ValidationUtils {

    public static void validateSessionId(String sessionId, Firestore firestore) {
        if(sessionId == null || sessionId.isBlank()){
            throw new RuntimeException("Invalid Session Id");
        }
        DocumentReference sessionDocRef = firestore.collection("sessions").document(sessionId);
        ApiFuture<DocumentSnapshot> docSnapRes = sessionDocRef.get();
        try {
            DocumentSnapshot docSnap = docSnapRes.get();
            if(!docSnap.exists())
                throw new RuntimeException("Invalid Session Id");
        }catch(InterruptedException | ExecutionException exc){
            throw new RuntimeException("Could not validate Session");
        }
    }

}
