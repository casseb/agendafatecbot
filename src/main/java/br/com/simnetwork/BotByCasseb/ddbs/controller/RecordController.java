package br.com.simnetwork.BotByCasseb.ddbs.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.simnetwork.BotByCasseb.ddbs.model.Record;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordSchema;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordStatus;
import br.com.simnetwork.BotByCasseb.ddbs.model.SimpleList;
import br.com.simnetwork.BotByCasseb.ddbs.service.ExternalQueryOption;
import br.com.simnetwork.BotByCasseb.ddbs.service.ExternalService;
import br.com.simnetwork.BotByCasseb.ddbs.service.RecordService;


@RestController
@CrossOrigin
public class RecordController {
	
	@Autowired
	private RecordService entityService;
	@Autowired
	private ExternalService externalService;
	
	@Deprecated
	@RequestMapping(value = "/{entityname}")
	public ResponseEntity<Collection<Record>> getByEntityName(@PathVariable("entityname") String entityName){
		List<Record> records = entityService.findByEntityName(entityName);
		return new ResponseEntity<Collection<Record>>(records, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/getbysimplelist")
	public ResponseEntity<Collection<Record>> getBySimplesList(@RequestBody SimpleList simpleList){
		List<Record> records = externalService.queryBySimpleList(simpleList);
		if(records != null) {
			return new ResponseEntity<Collection<Record>>(records, HttpStatus.OK);
		}else {
			return new ResponseEntity<Collection<Record>>(HttpStatus.NOT_ACCEPTABLE);
		}
		
	}
	
	@RequestMapping(value = "/allsimplelist")
	public ResponseEntity<Collection<SimpleList>> getAllSimpleList(){
		List<SimpleList> lists = externalService.queryAllSimpleLists();
		return new ResponseEntity<Collection<SimpleList>>(lists, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/insertrecordschema")
	public RecordStatus insertRecordSchema(@RequestBody List<RecordSchema> recordSchema) {
		return externalService.insertRecordSchema(recordSchema);
	}
	
	@RequestMapping(value = "/allrecordschema")
	public ResponseEntity<Collection<RecordSchema>> allRecordSchema(){
		List<RecordSchema> records = externalService.getAllRecordSchema();
		return new ResponseEntity<Collection<RecordSchema>>(records, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/insertrecord")
	public RecordStatus insertRecord(@RequestBody List<Record> records) {
		return externalService.insertRecord(records);
	}
	
	@RequestMapping(value = "/updaterecord")
	public RecordStatus updateRecord(@RequestBody Record record) {
		if(entityService.setValue(record, record.getValue())) {
			return RecordStatus.SUCESSO;
		}else {
			return RecordStatus.FORMATO_INCORRETO;
		}
	}
	
	@RequestMapping(value = "/deleterecord")
	public RecordStatus deleteRecord(@RequestBody Record record) {
		return entityService.deleteByRecord(record);
	}
	
	@RequestMapping(value = "/deleterecordsimplelist")
	public RecordStatus deleteRecord(@RequestBody SimpleList simpleList) {
		List<Record> records = externalService.queryBySimpleList(simpleList);
		if(records != null) {
			for(Record record : records) {
				entityService.deleteByRecord(record);
			}
		}
		return RecordStatus.SUCESSO;
	}
}
