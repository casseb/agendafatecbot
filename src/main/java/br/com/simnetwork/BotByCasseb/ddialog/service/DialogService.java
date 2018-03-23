package br.com.simnetwork.BotByCasseb.ddialog.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;

import br.com.simnetwork.BotByCasseb.ddialog.model.DialogSchema;

public interface DialogService {

	public void decideDialog(Update update);
	
	public void createDialog(User user, DialogSchema dialogSchema);
	
	public void executeDialog(User user, Message message, String callBackData);
	
	public void resetAllDialogs();
	
}
