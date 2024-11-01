package com.hashtag.lasertag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LasertagApplication {

	public static void main(String[] args) {
//		SpringApplication.run(LasertagApplication.class, args);
		SpringApplication app = new SpringApplication(LasertagApplication.class);
		app.run(args);
		System.out.println("Classpath: " + System.getProperty("java.class.path"));

	}

}
