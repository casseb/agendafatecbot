package br.com.simnetwork.BotByCasseb.telegramBot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;

import br.com.simnetwork.BotByCasseb.ddialog.service.DialogSchemaService;
import br.com.simnetwork.BotByCasseb.ddialog.service.DialogService;
import br.com.simnetwork.BotByCasseb.telegramBot.model.Bot;
@RestController
public class TelegramBotController {

	@Autowired
	private DialogService dialogService;
	
	@RequestMapping("/readMessages")
	public void readMessages(@RequestBody String stringRequest) {
		if(stringRequest != null) {
			Update update = BotUtils.parseUpdate(stringRequest);
			dialogService.decideDialog(update);
		}
		
	}
	
}
