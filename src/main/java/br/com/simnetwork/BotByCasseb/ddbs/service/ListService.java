package br.com.simnetwork.BotByCasseb.ddbs.service;

import java.util.List;
import java.util.Map;

import br.com.simnetwork.BotByCasseb.ddbs.model.AdvancedList;
import br.com.simnetwork.BotByCasseb.ddbs.model.Record;
import br.com.simnetwork.BotByCasseb.ddbs.model.SimpleList;

public interface ListService {
	
	public List<String> getSimpleList(SimpleList simpleList, Map<String, String> dialogDecisions);
	
	public List<String> getAdvancedList(AdvancedList advancedList, Map<String, String> dialogDecisions);
	List<Record> getSimpleList(SimpleList simpleList, ExternalQueryOption option);

}
