package br.com.simnetwork.BotByCasseb.ddbs.service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.simnetwork.BotByCasseb.ddbs.model.Record;

@Service("keyService")
public class KeyServiceImpl implements KeyService {

	public List<String> parseStringKey(String key) {
		List<String> result = new LinkedList<String>();

		if (key.contains("-")) {
			String[] resultArray = key.split("-");
			for (String string : resultArray) {
				result.add(string);
			}
		} else {
			result.add(key);
		}

		return result;
	}
	
	public List<String> parseStringKey(List<Record> records){
		List<String> result = new LinkedList<>();
		for(Record record : records) {
			result.add(record.getKey());
		}
		result = removeRepeatedValue(result);
		return result;
	}
	
	public List<String> removeRepeatedValue(List<String> strings){
		return new LinkedList<String>(new HashSet<String>(strings));
	}

}
