package br.com.simnetwork.BotByCasseb.ddbs.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.simnetwork.BotByCasseb.ddbs.model.AdvancedList;
import br.com.simnetwork.BotByCasseb.ddbs.model.Record;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordSchema;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordStatus;
import br.com.simnetwork.BotByCasseb.ddbs.model.SimpleList;
import br.com.simnetwork.BotByCasseb.ddbs.repository.AdvancedListRepository;
import br.com.simnetwork.BotByCasseb.ddbs.repository.RecordSchemaRepository;
import br.com.simnetwork.BotByCasseb.ddbs.repository.SimpleListRepository;

@Service("externalService")
public class ExternalServiceImpl implements ExternalService {

	@Autowired
	private SimpleListRepository simpleListRepo;
	@Autowired
	private AdvancedListRepository advancedListRepo;
	@Autowired
	private ListService listService;
	@Autowired
	private RecordSchemaRepository recordSchemaRepo;
	@Autowired
	private RecordService entityService;

	public List<Record> queryBySimpleList(SimpleList simpleList) {

		if (simpleList.getListName() == null) {
			return null;
		}

		if (simpleList.getEntityName() == null && simpleList.getFieldName() == null && simpleList.getValue() == null) {

			simpleList = simpleListRepo.findOne(simpleList.getListName());
			if (simpleList == null) {
				return null;
			}

		}
		simpleListRepo.save(simpleList);

		if (simpleList.getFieldName() != null && simpleList.getValue() != null) {
			return listService.getSimpleList(simpleList, ExternalQueryOption.WITHVALUE);
		} else {
			return listService.getSimpleList(simpleList, ExternalQueryOption.ALL);
		}

	}

	public List<SimpleList> queryAllSimpleLists() {
		return simpleListRepo.findAll();
	}

	public RecordStatus insertRecordSchema(List<RecordSchema> recordSchemas) {
		
		boolean haveKey = false;
		
		for (RecordSchema recordSchema : recordSchemas) {
			if(recordSchema.getIsKey()!=null && recordSchema.getIsKey()) {
				haveKey = true;
			}
		}
		
		if(!haveKey) {
			return RecordStatus.FORMATO_INCORRETO;
		}
		
		if(recordSchemaRepo.findByEntityName(recordSchemas.get(0).getEntityName()).isEmpty()) {
			for (RecordSchema recordSchema : recordSchemas) {
				if(recordSchema.getCreatedByXml()==null) {
					recordSchema.setCreatedByXml(false);
				}
				recordSchemaRepo.save(recordSchema);
			}
		}else {
			return RecordStatus.REGISTRO_JA_EXISTE;
		}
		
		return RecordStatus.SUCESSO;
		
	}

	public List<RecordSchema> getAllRecordSchema() {
		return recordSchemaRepo.findAll();
	}

	public RecordStatus insertRecord(List<Record> records) {
		return entityService.insertRecord(records);
	}

}
