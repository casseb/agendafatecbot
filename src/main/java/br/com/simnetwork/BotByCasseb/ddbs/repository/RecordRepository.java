package br.com.simnetwork.BotByCasseb.ddbs.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.simnetwork.BotByCasseb.ddbs.model.Record;

public interface RecordRepository extends MongoRepository<Record, ObjectId> {

	public List<Record> findByEntityName(String entityName);
	
	public Record findByEntityNameAndKeyAndFieldName(String entityName, String key, String fieldName);
	
	public List<Record> findByEntityNameAndKey(String entityName, String key);
	
	public List<Record> findByEntityNameAndFieldName(String entityName, String fieldName);
	
	public List<Record> findByEntityNameAndFieldNameAndValue(String entityName, String fieldName, String value);
	
	public List<Record> findByEntityNameAndFieldNameAndValueContains(String entityName, String fieldName, String value);

	public List<Record> findByEntityNameAndKeyIn(String entityName, List<String> keys);
	
	
}
