package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItApp {

	public static void main(String[] args) {
		System.getProperties().put("server.port", 9090);
		SpringApplication.run(ShareItApp.class, args);
	}

}
