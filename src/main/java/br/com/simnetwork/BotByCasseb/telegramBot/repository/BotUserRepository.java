package br.com.simnetwork.BotByCasseb.telegramBot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.simnetwork.BotByCasseb.telegramBot.model.BotUser;

public interface BotUserRepository extends MongoRepository<BotUser, Integer>{
	
}
