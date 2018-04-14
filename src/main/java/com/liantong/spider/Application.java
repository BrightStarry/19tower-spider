package com.liantong.spider;

import com.liantong.spider.task.MainTask;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner{

	private final MainTask mainTask;

	public Application(MainTask mainTask) {
		this.mainTask = mainTask;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		mainTask.run();
	}
}
