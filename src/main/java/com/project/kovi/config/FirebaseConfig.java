package com.project.kovi.config;

import com.google.api.client.util.Value;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.project.kovi.KoviApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public Firestore firestore(){
        InputStream is = null;
        GoogleCredentials credentials = null;
        try {
            is = KoviApplication.class.getResourceAsStream("/kovi-key.json");
            credentials = GoogleCredentials.fromStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load Firebase");
        }
        FirebaseOptions options = FirebaseOptions.builder().setCredentials(credentials).build();
        FirebaseApp.initializeApp(options);
        Firestore db =  FirestoreClient.getFirestore();
        System.out.println("db = " + db);
        return db;
    }

}
