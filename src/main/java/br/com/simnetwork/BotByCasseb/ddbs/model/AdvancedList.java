package br.com.simnetwork.BotByCasseb.ddbs.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdvancedList {

	@Id
	private String listName;
	
	private Map<String,AdvancedListOption> itens = new HashMap<String,AdvancedListOption>();
	
	public Map<String,AdvancedListOption> getItensByAdvancedListOption(AdvancedListOption option){
		Map<String,AdvancedListOption> result = new HashMap<String,AdvancedListOption>();
		
		for(String item : itens.keySet()) {
			if(itens.get(item).equals(option)) {
				result.put(item, itens.get(item));
			}
		}
		
		return result;
	}
	
	public void addItem(String simpleListName, AdvancedListOption option) {
		this.itens.put(simpleListName, option);
	}
	
}
