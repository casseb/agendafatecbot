package br.com.simnetwork.BotByCasseb.ddbs.service;

import java.util.List;

import br.com.simnetwork.BotByCasseb.ddbs.model.Record;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordSchema;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordStatus;
import br.com.simnetwork.BotByCasseb.ddbs.model.SimpleList;

public interface ExternalService {

	public List<Record> queryBySimpleList(SimpleList simpleList);
	public List<SimpleList> queryAllSimpleLists();
	public RecordStatus insertRecordSchema(List<RecordSchema> recordSchema);
	public List<RecordSchema> getAllRecordSchema();
	public RecordStatus insertRecord(List<Record> records);
	
}
