package br.com.simnetwork.BotByCasseb.ddialog.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.simnetwork.BotByCasseb.ddialog.model.DialogSchema;

public interface DialogSchemaRepository extends MongoRepository<DialogSchema, String>{
	
	public List<DialogSchema> findByNoPermissionRequired(Boolean noPermissionRequired);
	
}
