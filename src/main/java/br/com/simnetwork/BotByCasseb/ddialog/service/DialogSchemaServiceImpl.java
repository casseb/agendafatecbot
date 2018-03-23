package br.com.simnetwork.BotByCasseb.ddialog.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.simnetwork.BotByCasseb.ddialog.model.DialogSchema;
import br.com.simnetwork.BotByCasseb.ddialog.repository.DialogSchemaRepository;

@Service("dialogSchemaService")
public class DialogSchemaServiceImpl implements DialogSchemaService{

	@Autowired
	private DialogSchemaRepository dialogSchemaRepo;
	
	@Override
	public void synchronizeDialogSchema(String dialogSchemaName) {
		
	}

	@Override
	public void synchronizeAllDialogSchema() {
		dialogSchemaRepo.deleteAll();
		
		
	}

	@Override
	public DialogSchema findDialogSchemabyNomeSchema(String nomeSchema) {
		DialogSchema dialogSchema = dialogSchemaRepo.findOne(nomeSchema);
		return dialogSchema;
	}

	@Override
	public List<DialogSchema> findAllDialogSchema() {
		return dialogSchemaRepo.findAll();
	}
	
	public List<String> findByNoPermissionRequired(){
		List<DialogSchema> dialogs = dialogSchemaRepo.findByNoPermissionRequired(true);
		List<String> result = new LinkedList<>();
		for(DialogSchema dialog : dialogs) {
			result.add(dialog.getNomeSchema());
		}
		return result;
	}

	@Override
	public void disableDialog(String dialogSchemaName) {
		DialogSchema dialogSchema = dialogSchemaRepo.findOne(dialogSchemaName);
		dialogSchema.setActive(false);
		dialogSchemaRepo.save(dialogSchema);
		
	}

	@Override
	public void enableDialog(String dialogSchemaName) {
		DialogSchema dialogSchema = dialogSchemaRepo.findOne(dialogSchemaName);
		dialogSchema.setActive(true);
		dialogSchemaRepo.save(dialogSchema);
	}

}
