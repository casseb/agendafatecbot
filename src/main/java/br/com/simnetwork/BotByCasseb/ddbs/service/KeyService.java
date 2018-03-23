package br.com.simnetwork.BotByCasseb.ddbs.service;

import java.util.List;

import br.com.simnetwork.BotByCasseb.ddbs.model.Record;
import br.com.simnetwork.BotByCasseb.ddbs.model.SimpleList;

public interface KeyService {

	public List<String> parseStringKey(String key);
	public List<String> parseStringKey(List<Record> records);
	public List<String> removeRepeatedValue(List<String> strings);
	
}
