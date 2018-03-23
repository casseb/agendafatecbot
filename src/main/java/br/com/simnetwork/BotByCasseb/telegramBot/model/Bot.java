package br.com.simnetwork.BotByCasseb.telegramBot.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import br.com.simnetwork.BotByCasseb.telegramBot.service.KeyboardService;
import br.com.simnetwork.BotByCasseb.telegramBot.service.KeyboardServiceImpl;

public class Bot {
	
	//Testes
	//private static String token = "458418970:AAGVmBBBzb8C_JN06fpHR0XCZiDbkVPxoro";
	
	//Oficial
	private static String token = "522292896:AAGftYq_vLZFvw25_2givAkqg28qkDlG6dE";
	private static TelegramBot bot = TelegramBotAdapter.build(token);
	
	public static void sendMessage(String chatId, String text, Keyboard keyboard) {
		KeyboardServiceImpl keyboardService = new KeyboardServiceImpl();
		if(keyboard == null) {
			bot.execute(new SendMessage(chatId, text).parseMode(ParseMode.HTML).replyMarkup(keyboardService.getDefaultKeyboard()));
		}else {
			bot.execute(new SendMessage(chatId, text).parseMode(ParseMode.HTML).replyMarkup(keyboard));
		}
		
	}
	
	public static void requestContact(String chatId, String text) {
		KeyboardButton[] keyboardButton = new KeyboardButton[]{
                new KeyboardButton("Disponibilizar Contato").requestContact(true)
        };
		Keyboard keyboard = new ReplyKeyboardMarkup(keyboardButton);
		bot.execute(new SendMessage(chatId, text).replyMarkup(keyboard));
	}
	
	public static void requestInlineOption(String chatId, String text, InlineKeyboardMarkup keyboard) {
		bot.execute(new SendMessage(chatId, text).replyMarkup(keyboard));
	}

}
