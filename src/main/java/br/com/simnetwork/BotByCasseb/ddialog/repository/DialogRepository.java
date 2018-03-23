package br.com.simnetwork.BotByCasseb.ddialog.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.simnetwork.BotByCasseb.ddialog.model.Dialog;
import br.com.simnetwork.BotByCasseb.telegramBot.model.BotUser;

public interface DialogRepository extends MongoRepository<Dialog, Integer>{

	public Dialog findById(Integer id);
	
}
