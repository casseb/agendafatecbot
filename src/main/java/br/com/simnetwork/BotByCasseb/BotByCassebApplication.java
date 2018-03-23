package br.com.simnetwork.BotByCasseb;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BotByCassebApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(BotByCassebApplication.class, args);
	}
	
}
