package br.com.simnetwork.BotByCasseb.ddbs.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Item de uma tabela dinâmica.
 * @author <a href="mailto:felipe.casseb@gmail.com">By Casseb</a>
 * @since 1.5
 */
@Getter
@Setter
@NoArgsConstructor
public class Record {

	@Id
	private ObjectId objectId;
	
	private String entityName;
	private String key;
	private String fieldName;
	private String value;
	
	public Record(String entityName, String key, String fieldName, String value) {
		super();
		this.objectId = new ObjectId();
		this.entityName = entityName;
		this.key = key;
		this.fieldName = fieldName;
		this.value = value;
	}

}

