package br.com.simnetwork.BotByCasseb.telegramBot.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;

import br.com.simnetwork.BotByCasseb.ddbs.model.Record;

@Service("keyboardService")
public class KeyboardServiceImpl implements KeyboardService {

	@Override
	public Keyboard getSimpleKeyboard(List<String> keyboardOptions) {
		Map<Integer, String[]> map = prepareKeyboard(keyboardOptions);
		Keyboard replyKeyboardMarkup = new ReplyKeyboardMarkup(map.values().toArray(new String[map.size()][20])).resizeKeyboard(true).oneTimeKeyboard(true);
		return replyKeyboardMarkup;
	}
	
	@Override
	public Keyboard getDefaultKeyboard() {
		List<String> options = new LinkedList<String>();
		options.add("Menu");
		Map<Integer, String[]> map = prepareKeyboard(options);
		Keyboard replyKeyboardMarkup = new ReplyKeyboardMarkup(map.values().toArray(new String[map.size()][20])).resizeKeyboard(true);
		return replyKeyboardMarkup;
	}


	@Override
	public InlineKeyboardMarkup getSimpleInlineKeyboard(List<String> inlineOptions) {
		Map<Integer, InlineKeyboardButton[]> map = prepareInlineKeyboard(inlineOptions);
		InlineKeyboardMarkup inlineKeyboardMarkup = 
				new InlineKeyboardMarkup(map.values().toArray(new InlineKeyboardButton[map.size()][20]));
		return inlineKeyboardMarkup;
	}
	
	@Override
	public InlineKeyboardMarkup getRecordInlineKeyboard(List<Record> records) {
		List<String> inlineOptions = new LinkedList<>();
		for(Record record : records) {
			inlineOptions.add(record.getKey());
		}
		return getSimpleInlineKeyboard(inlineOptions);
	}

	private Map<Integer, String[]> prepareKeyboard(List<String> strings) {
		Map<Integer, String[]> map = new HashMap<Integer, String[]>();
		if (strings.size() != 0) {
			int linha = 0;
			int item = 0;

			int biggerString = biggerString(strings);

			int size1 = 32;
			int size2 = 15;
			int size3 = 9;
			int size4 = 5;

			int n;

			if (biggerString >= size1) {
				n = 1;
			} else {
				if (biggerString >= size2) {
					n = 1;
				} else {
					if (biggerString >= size3) {
						n = 2;
					} else {
						if (biggerString >= size4) {
							n = 3;
						} else {
							n = 4;
						}
					}
				}
			}

			// Caso a lista seja menor que a quantidade de colunas
			if (n > strings.size()) {
				String[] conteudoUnico = new String[strings.size()];
				for (int i = 0; i < strings.size(); i++) {
					conteudoUnico[i] = strings.get(item++);
				}
				map.put(linha++, conteudoUnico);
			} else {
				// Caso haja sobra na distribuição dos botões
				if (strings.size() % n != 0) {
					int sobra = strings.size() % n;
					if (sobra > 0) {
						String[] conteudoSobra = new String[sobra];
						for (int i = 0; i < sobra; i++) {
							conteudoSobra[i] = strings.get(item++);
						}
						map.put(linha++, conteudoSobra);
					}
				}
				// Distribuição final
				while (item < strings.size()) {
					String[] conteudo = new String[n];
					for (int i = 0; i < n; i++) {
						conteudo[i] = strings.get(item++);
					}
					map.put(linha++, conteudo);
				}
			}
		}
		return map;

	}
	
	private Map<Integer, InlineKeyboardButton[]> prepareInlineKeyboard(List<String> strings) {
		Map<Integer, InlineKeyboardButton[]> map = new HashMap<Integer, InlineKeyboardButton[]>();
		List<InlineKeyboardButton> buttons = new LinkedList<InlineKeyboardButton>();
		
		for(String string : strings) {
			buttons.add(new InlineKeyboardButton(string).callbackData(string));
		}
		
		if (strings.size() != 0) {
			int linha = 0;
			int item = 0;

			int biggerString = biggerString(strings);

			int size1 = 32;
			int size2 = 15;
			int size3 = 9;
			int size4 = 5;

			int n;

			if (biggerString >= size1) {
				n = 1;
			} else {
				if (biggerString >= size2) {
					n = 1;
				} else {
					if (biggerString >= size3) {
						n = 2;
					} else {
						if (biggerString >= size4) {
							n = 3;
						} else {
							n = 4;
						}
					}
				}
			}

			// Caso a lista seja menor que a quantidade de colunas
			if (n > strings.size()) {
				InlineKeyboardButton[] conteudoUnico = new InlineKeyboardButton[strings.size()];
				for (int i = 0; i < strings.size(); i++) {
					conteudoUnico[i] = buttons.get(item++);
				}
				map.put(linha++, conteudoUnico);
			} else {
				// Caso haja sobra na distribuição dos botões
				if (strings.size() % n != 0) {
					int sobra = strings.size() % n;
					if (sobra > 0) {
						InlineKeyboardButton[] conteudoSobra = new InlineKeyboardButton[sobra];
						for (int i = 0; i < sobra; i++) {
							conteudoSobra[i] = buttons.get(item++);
						}
						map.put(linha++, conteudoSobra);
					}
				}
				// Distribuição final
				while (item < strings.size()) {
					InlineKeyboardButton[] conteudo = new InlineKeyboardButton[n];
					for (int i = 0; i < n; i++) {
						conteudo[i] = buttons.get(item++);
					}
					map.put(linha++, conteudo);
				}
			}
		}
		return map;

	}

	private int biggerString(List<String> strings) {
		int result = 0;
		for (String string : strings) {
			if (string.length() > result) {
				result = string.length();
			}
		}
		return result;
	}
	

}
