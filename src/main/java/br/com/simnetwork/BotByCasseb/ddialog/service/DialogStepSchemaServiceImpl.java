package br.com.simnetwork.BotByCasseb.ddialog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;

import br.com.simnetwork.BotByCasseb.ddialog.model.DialogStepSchema;
import br.com.simnetwork.BotByCasseb.telegramBot.service.KeyboardService;

@Service("dialogStepSchemaService")
public class DialogStepSchemaServiceImpl implements DialogStepSchemaService{
	
	@Autowired
	KeyboardService keyboardService;
	
	@Override
	public Keyboard getKeyboard(DialogStepSchema dialogStepSchema) {
		if(!dialogStepSchema.getKeyboardOptions().isEmpty()) {
			return keyboardService.getSimpleKeyboard(dialogStepSchema.getKeyboardOptions());
		}else {
			return keyboardService.getDefaultKeyboard();
		}
	}

	@Override
	public InlineKeyboardMarkup getInlineKeyboard(DialogStepSchema dialogStepSchema) {
		if(!dialogStepSchema.getInlineKeyboard().isEmpty()) {
			return keyboardService.getSimpleInlineKeyboard(dialogStepSchema.getInlineKeyboard());
		}else {
			return null;
		}
	}
	
}
