package br.com.simnetwork.BotByCasseb.ddbs.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecordSchema {

	@Id
	private ObjectId objectId;
	
	private Integer order;
	private String entityName;
	private String fieldName;
	private RecordType type;
	private String defaultValue;
	private Boolean isKey;
	private Boolean notNull;
	private Boolean createdByXml;
	private String entityType;
	private String recordLimits;
	
	public RecordSchema(int order, String entityName, String fieldName, RecordType type, String defaultValue,
			Boolean isKey, Boolean notNull, Boolean createdByXml) {
		super();
		this.objectId = new ObjectId();
		this.order = order;
		this.entityName = entityName;
		this.fieldName = fieldName;
		this.type = type;
		this.defaultValue = defaultValue;
		this.isKey = isKey;
		this.notNull = notNull;
		this.createdByXml = createdByXml;
	}
	
	
	
}
