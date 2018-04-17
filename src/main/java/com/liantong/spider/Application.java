package com.liantong.spider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableScheduling
@SpringBootApplication
public class Application{



	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
