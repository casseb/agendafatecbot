package br.com.simnetwork.BotByCasseb.ddialog.service;

import java.util.List;
import java.util.Map;

public interface DecisionService {

	public Map<String,String> getDecisionsFilter(Map<String,String> decisions, String filter);
	public Map<String,String> cleanDecisions(Map<String,String> decisions, String filter);
	public List<String> fieldsInside(Map<String,String> decisions);
	
}
