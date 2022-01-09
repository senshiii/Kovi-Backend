package com.project.kovi.config;

import com.pusher.rest.Pusher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PusherConfig {

    @Value("${pusher.app_id}")
    private String pusherId;
    @Value("${pusher.key}")
    private String pusherKey;
    @Value("${pusher.secret}")
    private String pusherSecret;
    @Value("${pusher.cluster}")
    private String pusherCluster;

    @Bean
    public Pusher pusher(){
        System.out.println("Setting Up Pusher");
        Pusher pusher = new Pusher(pusherId, pusherKey, pusherSecret);
        pusher.setCluster(pusherCluster);
        System.out.println("pusher = " + pusher);
        return pusher;
    }

}
