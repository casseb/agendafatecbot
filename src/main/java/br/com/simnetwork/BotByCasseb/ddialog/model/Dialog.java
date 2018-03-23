package br.com.simnetwork.BotByCasseb.ddialog.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
public class Dialog {
	
	@Id @NonNull
	private Integer id;
	@NonNull
	private DialogSchema dialogSchema;
	private DialogStatus dialogStatus = DialogStatus.INICIO;
	private Map<String,String> decisions = new HashMap<>();
	private Integer currentStep = 1;

	
	public void addDecision(String key, String value) {
		if(value!=null) {
			decisions.put(key, value);
		}
	}
}
