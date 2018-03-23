package br.com.simnetwork.BotByCasseb.ddbs.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.collections.CaseInsensitiveKeyMap;
import org.hibernate.validator.internal.xml.FieldType;
import org.hibernate.validator.internal.xml.ValidatedByType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.simnetwork.BotByCasseb.ddbs.model.Record;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordSchema;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordStatus;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordType;
import br.com.simnetwork.BotByCasseb.ddbs.repository.AdvancedListRepository;
import br.com.simnetwork.BotByCasseb.ddbs.repository.RecordRepository;
import br.com.simnetwork.BotByCasseb.ddbs.repository.RecordSchemaRepository;
import br.com.simnetwork.BotByCasseb.ddialog.model.DialogSchema;
import br.com.simnetwork.BotByCasseb.ddialog.service.DecisionService;
import br.com.simnetwork.BotByCasseb.ddialog.service.DialogSchemaService;
import br.com.simnetwork.BotByCasseb.telegramBot.model.BotUser;
import br.com.simnetwork.BotByCasseb.telegramBot.service.BotUserService;

@Service("recordService")
public class RecordServiceImpl implements RecordService {

	@Autowired
	private BotUserService botUserService;
	@Autowired
	private DialogSchemaService dialogSchemaService;
	@Autowired
	private RecordRepository recordRepo;
	@Autowired
	private DecisionService decisionService;
	@Autowired
	private RecordSchemaRepository recordSchemaRepo;
	@Autowired
	private KeyService keyService;
	@Autowired
	private AdvancedListRepository advancedListRepo;

	public void synchronizeStaticEntities() {

		synchronizeBotUserEntity();
		synchronizeDialogSchemaEntity();

		//Apagando os schemas definidos por xml antigos
		advancedListRepo.deleteAll();

		synchronizeDefaultPermissions();
	}

	private void synchronizeDefaultPermissions() {
		Map<String, String> content = null;
		
		String admin = "336050938-Felipe Casseb";
		
		if(findByKeys("Permissão",admin+"-|D|Administração|").isEmpty()) {
			content = new HashMap<String, String>();
			content.put("Id Telegram", admin);
			content.put("Diálogo", "|D|Administração|");
			insertRecord("Permissão", content);
		}
		
		if(findByKeys("Permissão",admin+"-|D|Permissão|").isEmpty()) {
			content = new HashMap<String, String>();
			content.put("Id Telegram", admin);
			content.put("Diálogo", "|D|Permissão|");
			insertRecord("Permissão", content);
		}
		
		if(findByKeys("Permissão",admin+"-|D|Adicionar Permissão|").isEmpty()) {
			content = new HashMap<String, String>();
			content.put("Id Telegram", admin);
			content.put("Diálogo", "|D|Adicionar Permissão|");
			insertRecord("Permissão", content);
		}
		
		if(findByKeys("Permissão",admin+"-|D|Consultar Permissão|").isEmpty()) {
			content = new HashMap<String, String>();
			content.put("Id Telegram", admin);
			content.put("Diálogo", "|D|Consultar Permissão|");
			insertRecord("Permissão", content);
		}
		
		if(findByKeys("Permissão",admin+"-|D|Editar Permissão|").isEmpty()) {
			content = new HashMap<String, String>();
			content.put("Id Telegram", admin);
			content.put("Diálogo", "|D|Editar Permissão|");
			insertRecord("Permissão", content);
		}
		
		if(findByKeys("Permissão",admin+"-|D|Excluir Permissão|").isEmpty()) {
			content = new HashMap<String, String>();
			content.put("Id Telegram", admin);
			content.put("Diálogo", "|D|Excluir Permissão|");
			insertRecord("Permissão", content);
		}
		
		if(findByKeys("Permissão",admin+"-|D|Atualizar|").isEmpty()) {
			content = new HashMap<String, String>();
			content.put("Id Telegram", admin);
			content.put("Diálogo", "|D|Atualizar|");
			insertRecord("Permissão", content);
		}
	}

	public List<Record> findByEntityName(String entityName) {
		return recordRepo.findByEntityName(entityName);
	}

	public Record findByKeys(String entityName, String key, String fieldName) {
		return recordRepo.findByEntityNameAndKeyAndFieldName(entityName, key, fieldName);
	}

	public List<Record> findByKeys(String entityName, String key) {
		return recordRepo.findByEntityNameAndKey(entityName, key);
	}

	public List<Record> findByKeys(String entityName, List<String> keys) {
		return recordRepo.findByEntityNameAndKeyIn(entityName, keys);
	}

	public List<String> findByFields(String entityName, Map<String, String> decisions) {
		List<Record> records = new LinkedList<>();

		if (decisions == null) {
			records = recordRepo.findByEntityName(entityName);
		} else {
			Map<String, String> queryDecisions = decisionService.getDecisionsFilter(decisions, "query:");
			queryDecisions.remove("unico");
			if (!queryDecisions.isEmpty()) {
				records = recordRepo.findByEntityName(entityName);
				for (String fieldName : queryDecisions.keySet()) {
					records = findByEntityNameAndFieldNameAndValueAllRecord(entityName, fieldName, queryDecisions.get(fieldName), records);
				}
			} else {
				records = recordRepo.findByEntityName(entityName);
			}
		}

		List<String> result = keyService.parseStringKey(records);
		return result;

	}

	public List<Record> findByEntityNameAndFieldNameAndValue(String entityName, String fieldName, String value) {
		return recordRepo.findByEntityNameAndFieldNameAndValueContains(entityName, fieldName, value);
	}

	public void deleteByKey(String entityName, String key) {
		recordRepo.delete(findByKeys(entityName, key));
	}
	
	public RecordStatus deleteByRecord(Record record) {
		if(record.getEntityName()==null || record.getKey()==null) {
			return RecordStatus.FORMATO_INCORRETO;
		}
		
		List<Record> records = recordRepo.findByEntityNameAndKey(record.getEntityName(), record.getKey());
		
		if(records.isEmpty()) {
			return RecordStatus.REGISTRO_INEXISTENTE;
		}
		
		deleteByKey(record.getEntityName(), record.getKey());
		
		return RecordStatus.SUCESSO;
	}

	public RecordType getType(Record record) {
		return recordSchemaRepo.findByEntityNameAndFieldName(record.getEntityName(), record.getFieldName()).getType();
	}

	public RecordType getType(String entityName, String fieldName) {
		return recordSchemaRepo.findByEntityNameAndFieldName(entityName, fieldName).getType();
	}

	public Boolean setValue(Record record, String newValue) {
		record = recordRepo.findByEntityNameAndKeyAndFieldName(record.getEntityName(), record.getKey(), record.getFieldName());
		if (validateValue(record, newValue)) {
			record.setValue(newValue);
			recordRepo.save(record);
			return true;
		} else {
			return false;
		}
	}

	public Boolean validateValue(Record record, String newValue) {
		RecordType type = getType(record);

		switch (type) {
		case STRING:
			return true;
		case INTEGER:
			return validateInteger(newValue);
		case BOOLEAN:
			return validateBoolean(newValue);
		case RECORD:
			return validateRecord(record, newValue);
		case AUTOINCREMENT:
			return false;
		case SELF:
			return true;
		default:
			return false;
		}

	}

	public boolean validateInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public boolean validateBoolean(String value) {
		return Boolean.parseBoolean(value);
	}

	public boolean validateRecord(Record record, String value) {
		RecordSchema schema = recordSchemaRepo.findByEntityNameAndFieldName(record.getEntityName(), record.getFieldName());
		return !findByKeys(schema.getEntityType(), value).isEmpty();
	}

	public boolean validateBotUser(String value) {
		try {
			return botUserService.locateBotUser(Integer.parseInt(value)) != null;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean validateDialogSchema(String value) {
		try {
			return dialogSchemaService.findDialogSchemabyNomeSchema(value) != null;
		} catch (Exception e) {
			return false;
		}
	}

	public Integer getInteger(Record record) {
		if (validateInteger(record.getValue())) {
			return Integer.parseInt(record.getValue());
		}
		return null;
	}

	public Boolean getBoolean(Record record) {
		if (validateBoolean(record.getValue())) {
			return Boolean.parseBoolean(record.getValue());
		}
		return null;
	}

	public List<Record> validateRecord(Record record) {
		if (validateRecord(record, record.getValue())) {
			return findByKeys(record.getEntityName(), record.getValue());
		}
		return null;
	}

	public BotUser getBotUser(Record record) {
		if (validateBotUser(record.getValue())) {
			return botUserService.locateBotUser(Integer.parseInt(record.getValue()));
		}
		return null;
	}

	public DialogSchema getDialogSchema(Record record) {
		if (validateDialogSchema(record.getValue())) {
			return dialogSchemaService.findDialogSchemabyNomeSchema(record.getValue());
		}
		return null;
	}

	// <------------------

	public void synchronizeBotUserEntity() {
		recordRepo.delete(recordRepo.findByEntityName("botUser"));

		recordSchemaRepo.save(new RecordSchema(1, "botUser", "id", RecordType.STRING, null, true, true,true));
		recordSchemaRepo.save(new RecordSchema(2, "botUser", "userName", RecordType.STRING, null, true, true,true));

		for (BotUser botUser : botUserService.locateAllBotUsers()) {

			Map<String, String> content = new HashMap<String, String>();
			content.putIfAbsent("id", botUser.getId().toString());
			content.putIfAbsent("userName", botUser.getFirstName() + " " + botUser.getLastName());
			RecordStatus recordStatus = insertRecord("botUser", content);
		}

	}

	private void synchronizeDialogSchemaEntity() {

		recordRepo.delete(recordRepo.findByEntityName("dialogSchema"));

		recordSchemaRepo.save(new RecordSchema(1, "dialogSchema", "nomeSchema", RecordType.STRING, null, true, true,true));
		recordSchemaRepo.save(
				new RecordSchema(1, "dialogSchema", "noPermissionRequired", RecordType.BOOLEAN, null, false, false,true));

		for (DialogSchema dialogSchema : dialogSchemaService.findAllDialogSchema()) {
			Map<String, String> content = new HashMap<String, String>();
			content.putIfAbsent("nomeSchema", dialogSchema.getNomeSchema());
			content.putIfAbsent("noPermissionRequired", dialogSchema.isNoPermissionRequired() + "");
			insertRecord("dialogSchema", content);
		}

	}

	public RecordStatus insertRecord(List<Record> records) {
		Map<String, String> content = new HashMap<String, String>();

		String entityName = records.get(0).getEntityName();

		for (Record record : records) {
			if (record.getEntityName() == null || !record.getEntityName().equals(entityName)) {
				return RecordStatus.FORMATO_INCORRETO;
			} else {
				content.put(record.getFieldName(), record.getValue());
			}
		}

		return insertRecord(entityName, content);
	}

	public RecordStatus insertRecord(String entityName, Map<String, String> content) {

		// Validando Existência da Entity
		List<RecordSchema> recordSchemas = recordSchemaRepo.findByEntityName(entityName);    
		if (recordSchemas.isEmpty()) {
			return RecordStatus.ENTIDADE_INEXISTENTE;
		}

		// Substituindo Valores Autoincrement e Defaults
		for (RecordSchema fieldSchema : recordSchemas) {
			if (fieldSchema.getDefaultValue() != null) {
				content.put(fieldSchema.getFieldName(), fieldSchema.getDefaultValue());
				continue;
			}
			if (fieldSchema.getType().equals(RecordType.AUTOINCREMENT)) {
				content.put(fieldSchema.getFieldName(), getMaxValue(entityName, fieldSchema.getFieldName()) + 1 + "");
			}
		}

		// Tratando chaves
		String key = "";
		List<RecordSchema> keys = recordSchemaRepo.findByEntityNameAndIsKey(entityName, true);

		if (content.containsKey(keys)) {
			return RecordStatus.CHAVE_NULL;
		} else {
			key = content.get(keys.get(0).getFieldName());
			if (keys.size() != 1) {
				keys.remove(0);
				for (RecordSchema keySchema : keys) {
					key = key + "-" + content.get(keySchema.getFieldName());
				}
			}
		}
		
		//Validando registro já existente
		if(!recordRepo.findByEntityNameAndKey(entityName, key).isEmpty()) {
			return RecordStatus.REGISTRO_JA_EXISTE;
		}

		// Validando campos obrigatórios
		for (RecordSchema fieldSchema : recordSchemaRepo.findByEntityNameAndNotNull(entityName, true)) {
			if (content.get(fieldSchema.getFieldName()) == null) {
				return RecordStatus.CAMPO_OBRIGATORIO_NULL;
			}
		}

		// Fazendo a persistência
		for (RecordSchema fieldSchema : recordSchemas) {
			String fieldValue = content.get(fieldSchema.getFieldName());
			if (fieldValue != null) {
				Record record = new Record(entityName, key, fieldSchema.getFieldName(), fieldValue);
				if(fieldSchema.getType().equals(RecordType.RECORD)) {
					if(!validateRecord(record, fieldValue)) {
						return RecordStatus.ITEM_VINCULADO_NAO_EXISTE;
					}
				}
				recordRepo.save(record);
			}else {
				if(fieldSchema.getType().equals(RecordType.BOOLEAN)) {
					Record record = new Record(entityName, key, fieldSchema.getFieldName(), "False");
					recordRepo.save(record);
				}
			}
		}

		return RecordStatus.SUCESSO;

	}

	@Override
	public void deleteRecord(String entityName, String recordKey) {
		recordRepo.delete(findByKeys(entityName, recordKey));
	}

	public void resetEntity(String entityName) {
		recordRepo.delete(recordRepo.findByEntityName(entityName));
	}

	public List<Record> findByEntityNameAndFieldName(String entityName, String fieldName) {
		return recordRepo.findByEntityNameAndFieldName(entityName, fieldName);
	}

	private Integer getMaxValue(String entityName, String fieldName) {
		List<Record> records = recordRepo.findByEntityNameAndFieldName(entityName, fieldName);
		Integer biggest = 0;
		if (records != null) {
			for (Record record : records) {
				if (Integer.parseInt(record.getValue()) > biggest) {
					biggest = Integer.parseInt(record.getValue());
				}
			}
		}
		return biggest;
	}
	
	public int getEntitySize(String entityName) {
		String fieldName = recordSchemaRepo.findByEntityNameAndIsKey(entityName,true).get(0).getFieldName();
		return recordRepo.findByEntityNameAndFieldName(entityName, fieldName).size();
	}
	
	public List<Record> findByEntityNameAndFieldNameAndValueAllRecord(String entityName, String fieldName, String value){
		List<Record> records = recordRepo.findByEntityNameAndFieldNameAndValue(entityName, fieldName, value);
		List<String> keys = new LinkedList<>();
		for(Record record : records) {
			keys.add(record.getKey());
		}
		return findByKeys(entityName, keys);
	}
	
	public List<Record> findByEntityNameAndFieldNameAndValueAllRecord(String entityName, String fieldName, String value, List<Record> bd){
		List<Record> records = new LinkedList<Record>();
		for(Record record : bd) {
			if(record.getEntityName().equals(entityName) && record.getFieldName().equals(fieldName) && record.getValue().contains(value)) {
				records.add(record);
			}
		}
		
		List<String> keys = new LinkedList<>();
		for(Record record : records) {
			keys.add(record.getKey());
		}
		return findByKeys(entityName, keys);
	}
	
	public int countRecordTypeKeys(String entityName) {
		List<RecordSchema> schemas = recordSchemaRepo.findByEntityNameAndIsKey(entityName, true);
		int result = 0;
		for(RecordSchema schema : schemas) {
			if(schema.getType().equals(RecordType.RECORD)) {
				result++;
			}
		}
		return result;
	}
	
	public int countKeys(String entityName) {
		return recordSchemaRepo.findByEntityNameAndIsKey(entityName, true).size();
	}
	
	public List<String> difBetweenEntities(String completeEntity, String targetEntity){
		RecordSchema recordTargetSchema = recordSchemaRepo.findByEntityNameAndEntityType(targetEntity,completeEntity).get(0);
		List<String> completeKeys = null;
		if(recordTargetSchema.getRecordLimits()!=null) {
			String field = recordTargetSchema.getRecordLimits().split("=")[0];
			String value = recordTargetSchema.getRecordLimits().split("=")[1];
			completeKeys = keyService.parseStringKey(findByEntityNameAndFieldNameAndValue(completeEntity, field, value));
		}else {
			completeKeys = keyService.parseStringKey(findByEntityName(completeEntity));
		}
		List<String> targetKeys = keyService.parseStringKey(findByEntityName(completeEntity));
		completeKeys.removeAll(targetKeys);
		return completeKeys;
		
	}
	
	public List<String> difBetweenEntities(String completeEntity1, String completeEntity2, String targetEntity){
		List<String> completePossibilities = new LinkedList<>();
		RecordSchema recordTargetSchema = recordSchemaRepo.findByEntityNameAndEntityType(targetEntity,completeEntity1).get(0);
		List<String> completeKeys1 = null;
		if(recordTargetSchema.getRecordLimits()!=null) {
			String field = recordTargetSchema.getRecordLimits().split("=")[0];
			String value = recordTargetSchema.getRecordLimits().split("=")[1];
			completeKeys1 = keyService.parseStringKey(findByEntityNameAndFieldNameAndValue(completeEntity1, field, value));
		}else {
			completeKeys1 = keyService.parseStringKey(findByEntityName(completeEntity1));
		}
		
		recordTargetSchema = recordSchemaRepo.findByEntityNameAndEntityType(targetEntity,completeEntity2).get(0);
		List<String> completeKeys2 = null;
		if(recordTargetSchema.getRecordLimits()!=null) {
			String field = recordTargetSchema.getRecordLimits().split("=")[0];
			String value = recordTargetSchema.getRecordLimits().split("=")[1];
			completeKeys2 = keyService.parseStringKey(findByEntityNameAndFieldNameAndValue(completeEntity2, field, value));
		}else {
			completeKeys2 = keyService.parseStringKey(findByEntityName(completeEntity2));
		}
		
		
		List<String> targetKeys = keyService.parseStringKey(findByEntityName(targetEntity));
		
		for(String entity1 : completeKeys1) {
			for(String entity2 : completeKeys2) {
				completePossibilities.add(entity1+"-"+entity2);
			}
		}
		
		completePossibilities.removeAll(targetKeys);
		
		return completePossibilities;
	}
	
	public List<String> difBetweenEntitiesChoice1(String choice1, String completeEntity2, String targetEntity){
		List<String> completePossibilities = new LinkedList<>();
		
		RecordSchema recordTargetSchema = recordSchemaRepo.findByEntityNameAndEntityType(targetEntity,completeEntity2).get(0);
		List<String> completeKeys2 = null;
		if(recordTargetSchema.getRecordLimits()!=null) {
			String field = recordTargetSchema.getRecordLimits().split("=")[0];
			String value = recordTargetSchema.getRecordLimits().split("=")[1];
			completeKeys2 = keyService.parseStringKey(findByEntityNameAndFieldNameAndValue(completeEntity2, field, value));
		}else {
			completeKeys2 = keyService.parseStringKey(findByEntityName(completeEntity2));
		}
		
		List<String> targetKeys = keyService.parseStringKey(findByEntityName(targetEntity));
		
		for(String entity2 : completeKeys2) {
			completePossibilities.add(choice1+"-"+entity2);
		}
		
		completePossibilities.removeAll(targetKeys);
		
		return completePossibilities;
	}

	@Override
	public void synchronizeStaticEntitiesTest() {
		// TODO Auto-generated method stub
		
	}
	
}
