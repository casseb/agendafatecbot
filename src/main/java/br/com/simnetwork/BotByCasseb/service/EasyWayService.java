package br.com.simnetwork.BotByCasseb.service;

import java.util.List;
import java.util.Map;

import br.com.simnetwork.BotByCasseb.ddbs.model.SimpleList;
import br.com.simnetwork.BotByCasseb.ddialog.model.DialogStepSchema;
import br.com.simnetwork.BotByCasseb.model.ConfFileStatus;

public interface EasyWayService {

	public ConfFileStatus loadConfFile();
	public SimpleList createSimpleList(String line);
	public List<String> smartInsertOptions(DialogStepSchema dialogStepSchema, Map<String,String> dialogDecisions);
	
}
