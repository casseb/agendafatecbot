package br.com.simnetwork.BotByCasseb.telegramBot.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BotUser implements Serializable{
	
	private static final long serialVersionUID = 8322528200592316207L;
	
	@Id
	private Integer id;
	private String firstName;
	private Boolean isBot;
	private String languageCode;
	private String lastName;
	private String username;
	private String contact;
	
	public BotUser(User user) {
		super();
		this.id = user.id();
		this.firstName = user.firstName();
		this.isBot = user.isBot();
		this.languageCode = user.languageCode();
		this.lastName = user.lastName();
		this.username = user.firstName();
	}
	
}
