package br.com.simnetwork.BotByCasseb;

import org.json.JSONException;
import org.json.JSONObject;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;

public class TestSupport {

	public static Message createMessage(){
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject("{\r\n" + 
					"  \"update_id\":151652641,\"message\":\r\n" + 
					"  {\r\n" + 
					"  \"date\":1493252008,\"chat\":\r\n" + 
					"    {\r\n" + 
					"      \"last_name\":\"Casseb\",\"id\":1,\"type\":\"private\",\"first_name\":\"Felipe\"\r\n" + 
					"      \r\n" + 
					"    },\r\n" + 
					"  \"message_id\":111,\"from\":\r\n" + 
					"    {\r\n" + 
					"      \"last_name\":\"Casseb\",\"id\":1,\"first_name\":\"Felipe\"\r\n" + 
					"      \r\n" + 
					"    },\r\n" + 
					"    \"text\":\"Casseb\"\r\n" + 
					"  }\r\n" + 
					"}");
			Update update = BotUtils.parseUpdate(jsonObj.toString());
			Message message = update.message();
			return message;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
