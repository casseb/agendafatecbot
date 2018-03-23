package br.com.simnetwork.BotByCasseb.ddbs.service;

import java.util.List;
import java.util.Map;

import br.com.simnetwork.BotByCasseb.ddbs.model.Record;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordStatus;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordType;
import br.com.simnetwork.BotByCasseb.ddialog.model.DialogSchema;
import br.com.simnetwork.BotByCasseb.telegramBot.model.BotUser;

public interface RecordService {
	
	public void synchronizeStaticEntities();
	public void synchronizeStaticEntitiesTest();
	public Record findByKeys(String entityName, String key, String fieldName);
	public List<Record> findByKeys(String entityName, String key);
	public List<Record> findByKeys(String entityName, List<String> keys);
	public List<String> findByFields(String entityName, Map<String, String> decisions);
	public void deleteByKey(String entityName, String key);
	public RecordType getType(Record record);
	public RecordType getType(String entityName, String fieldName);
	public Boolean setValue(Record record, String newValue);
	public Boolean validateValue(Record record, String newValue);
	public boolean validateInteger(String value);
	public boolean validateBoolean(String value);
	public boolean validateRecord(Record record, String value);
	public boolean validateBotUser(String value);
	public boolean validateDialogSchema(String value);
	public Integer getInteger(Record record);
	public Boolean getBoolean(Record record);
	public List<Record> validateRecord(Record record);
	public BotUser getBotUser(Record record);
	public RecordStatus insertRecord(String entityName, Map<String, String> content);
	public void synchronizeBotUserEntity();
	public void deleteRecord(String entityName,String recordKey);
	public List<Record> findByEntityNameAndFieldName(String entityName, String fieldName);
	public List<Record> findByEntityName(String entityName);
	public void resetEntity(String entityName);
	public List<Record> findByEntityNameAndFieldNameAndValue(String entityName, String fieldName, String value);
	public RecordStatus insertRecord(List<Record> records);
	public RecordStatus deleteByRecord(Record record);
	public int getEntitySize(String entityName);
	public List<Record> findByEntityNameAndFieldNameAndValueAllRecord(String entityName, String fieldName, String value);
	public int countRecordTypeKeys(String entityName);
	public int countKeys(String entityName);
	public List<String> difBetweenEntities(String completeEntity, String targetEntity);
	public List<String> difBetweenEntities(String completeEntity1, String completeEntity2, String targetEntity);
	public List<String> difBetweenEntitiesChoice1(String choice1, String completeEntity2, String targetEntity);
}
