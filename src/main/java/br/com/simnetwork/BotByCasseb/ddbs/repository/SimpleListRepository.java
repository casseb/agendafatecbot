package br.com.simnetwork.BotByCasseb.ddbs.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.simnetwork.BotByCasseb.ddbs.model.RecordSchema;
import br.com.simnetwork.BotByCasseb.ddbs.model.SimpleList;

public interface SimpleListRepository extends MongoRepository<SimpleList, String>{

}
