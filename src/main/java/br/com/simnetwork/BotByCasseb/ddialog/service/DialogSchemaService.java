package br.com.simnetwork.BotByCasseb.ddialog.service;

import java.util.List;

import br.com.simnetwork.BotByCasseb.ddialog.model.DialogSchema;

public interface DialogSchemaService {

	public void synchronizeDialogSchema(String dialogSchemaName);
	
	public void synchronizeAllDialogSchema();
	
	public DialogSchema findDialogSchemabyNomeSchema(String nomeSchema);
	
	public List<DialogSchema> findAllDialogSchema();
	
	public List<String> findByNoPermissionRequired();
	
	public void disableDialog(String dialogSchemaName);

	public void enableDialog(String dialogSchemaName);
	
}
