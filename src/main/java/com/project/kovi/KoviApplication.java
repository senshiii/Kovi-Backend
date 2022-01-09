package com.project.kovi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class KoviApplication {

	public static void main(String[] args) {
		SpringApplication.run(KoviApplication.class, args);
	}

	@Bean
	public Executor taskExecutor(){
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(10);
		taskExecutor.setMaxPoolSize(10);
		taskExecutor.setQueueCapacity(500);
		taskExecutor.setThreadNamePrefix("Intent-");
		taskExecutor.initialize();
		return taskExecutor;
	}

}
