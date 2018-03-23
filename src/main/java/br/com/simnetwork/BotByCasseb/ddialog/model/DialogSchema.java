package br.com.simnetwork.BotByCasseb.ddialog.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class DialogSchema {

	@Id @NonNull
	private String nomeSchema;
	private boolean noPermissionRequired = false;
	private boolean onlyAdmin = false;
	private String defaultEntity;
	private boolean active = true;
	private Map<Integer,DialogStepSchema> steps = new HashMap<>();
	
	public void addStep(Integer i, DialogStepSchema step) {
		this.steps.put(i, step);
	}
	
}
