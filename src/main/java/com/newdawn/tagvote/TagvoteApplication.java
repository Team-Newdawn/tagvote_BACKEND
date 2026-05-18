package com.newdawn.tagvote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TagvoteApplication {

	public static void main(String[] args) {
		SpringApplication.run(TagvoteApplication.class, args);
	}

}
