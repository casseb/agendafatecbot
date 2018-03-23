package br.com.simnetwork.BotByCasseb.ddialog.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;

import br.com.simnetwork.BotByCasseb.botDialog.StepType;
import br.com.simnetwork.BotByCasseb.ddbs.model.AdvancedList;
import br.com.simnetwork.BotByCasseb.ddbs.model.SimpleList;
import br.com.simnetwork.BotByCasseb.telegramBot.service.KeyboardService;
import br.com.simnetwork.BotByCasseb.telegramBot.service.KeyboardServiceImpl;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class DialogStepSchema {

	@Id @NonNull
	private ObjectId id;
	private String botMessage;
	private StepType stepType;
	private List<String> keyboardOptions = new LinkedList<>();
	private List<String> inlineKeyboard = new LinkedList<>();
	private String key;
	private String entity;
	private Map<String,String> parameters = new HashMap<String,String>();
	private SimpleList simpleList;
	private AdvancedList advancedList;
	private String entityToChange;
	
	public void addParameter(String key, String value) {
		this.parameters.put(key, value);
	}
	
}
