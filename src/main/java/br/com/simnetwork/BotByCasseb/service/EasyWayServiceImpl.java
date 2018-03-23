package br.com.simnetwork.BotByCasseb.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.simnetwork.BotByCasseb.botDialog.StepType;
import br.com.simnetwork.BotByCasseb.ddbs.model.AdvancedList;
import br.com.simnetwork.BotByCasseb.ddbs.model.AdvancedListOption;
import br.com.simnetwork.BotByCasseb.ddbs.model.Record;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordSchema;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordStatus;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordType;
import br.com.simnetwork.BotByCasseb.ddbs.model.SimpleList;
import br.com.simnetwork.BotByCasseb.ddbs.repository.AdvancedListRepository;
import br.com.simnetwork.BotByCasseb.ddbs.repository.RecordRepository;
import br.com.simnetwork.BotByCasseb.ddbs.repository.RecordSchemaRepository;
import br.com.simnetwork.BotByCasseb.ddbs.service.ExternalService;
import br.com.simnetwork.BotByCasseb.ddbs.service.KeyService;
import br.com.simnetwork.BotByCasseb.ddbs.service.RecordService;
import br.com.simnetwork.BotByCasseb.ddialog.model.DialogSchema;
import br.com.simnetwork.BotByCasseb.ddialog.model.DialogStepSchema;
import br.com.simnetwork.BotByCasseb.ddialog.repository.DialogSchemaRepository;
import br.com.simnetwork.BotByCasseb.ddialog.service.DecisionService;
import br.com.simnetwork.BotByCasseb.model.ConfFileStatus;
import br.com.simnetwork.BotByCasseb.model.ConfLineType;

@Service("easyWayService")
public class EasyWayServiceImpl implements EasyWayService {

	@Autowired
	private ExternalService externalService;
	@Autowired
	private RecordSchemaRepository recordSchemaRepo;
	@Autowired
	private AdvancedListRepository advancedListRepo;
	@Autowired
	private DialogSchemaRepository dialogSchemaRepo;
	@Autowired
	private RecordRepository recordRepo;
	@Autowired
	private DecisionService decisionService;
	@Autowired
	private RecordService entityService;
	@Autowired
	private KeyService keyService;

	private List<String> readConfFile() {

		/*
		 * Charset charset = Charset.forName("UTF-8"); Path path; try { path =
		 * Paths.get("/"
		 * +getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+
		 * "easyWay.conf"); //path =
		 * Paths.get(getClass().getClassLoader().getResource("easyWay.conf").toURI());
		 * List<String> lines = Files.readAllLines(path, charset); return lines; //}
		 * catch (URISyntaxException e) { //e.printStackTrace(); } catch (IOException e)
		 * { e.printStackTrace(); }
		 * 
		 * return null;
		 * 
		 */
		List<String> result = new LinkedList<>();

		try {
			// URL oracle = new URL("http://dontpad.com/cassebotConf.txt");
			URL oracle = new URL("http://dontpad.com/casseb/producao/Lorenzo.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream(), "UTF-8"));
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				result.add(inputLine);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	private ConfFileStatus validateConfFile() {

		return ConfFileStatus.SUCESSO;

	}

	public ConfFileStatus loadConfFile() {
		recordSchemaRepo.delete(recordSchemaRepo.findByCreatedByXml(true));

		ConfFileStatus status = null;
		if (!validateConfFile().equals(ConfFileStatus.SUCESSO)) {
			return status;
		}

		List<String> linhas = readConfFile();

		// Classificando linhas
		Map<Integer, ConfLineType> linhasClassificadas = new HashMap<Integer, ConfLineType>();
		ConfLineType step = ConfLineType.OBJETO;
		int i = 0;
		for (String linha : linhas) {
			switch (linha) {
			case "Entities":
				linhasClassificadas.put(i++, ConfLineType.OBJETO);
				step = ConfLineType.ENTIDADE;
				break;
			case "Lists":
				linhasClassificadas.put(i++, ConfLineType.OBJETO);
				step = ConfLineType.LIST;
				break;
			case "Dialogs":
				linhasClassificadas.put(i++, ConfLineType.OBJETO);
				step = ConfLineType.DIALOG;
				break;
			case "Smart":
				linhasClassificadas.put(i++, ConfLineType.OBJETO);
				step = ConfLineType.SMART;
				break;
			case "":
				linhasClassificadas.put(i++, ConfLineType.SEPARADOR);
				switch (step) {
				case ENTIDADE_ITEM:
					step = ConfLineType.ENTIDADE;
					break;
				case LIST_ITEM:
					step = ConfLineType.LIST;
					break;
				case DIALOG_ITEM:
					step = ConfLineType.DIALOG;
					break;
				default:
					break;
				}
				break;
			default:
				switch (step) {
				case ENTIDADE:
					linhasClassificadas.put(i++, ConfLineType.ENTIDADE);
					step = ConfLineType.ENTIDADE_ITEM;
					break;
				case LIST:
					linhasClassificadas.put(i++, ConfLineType.LIST);
					step = ConfLineType.LIST_ITEM;
					break;
				case DIALOG:
					linhasClassificadas.put(i++, ConfLineType.DIALOG);
					step = ConfLineType.DIALOG_ITEM;
					break;
				case SMART:
					linhasClassificadas.put(i++, ConfLineType.SMART);
					step = ConfLineType.SMART;
					break;
				case ENTIDADE_ITEM:
					linhasClassificadas.put(i++, ConfLineType.ENTIDADE_ITEM);
					break;
				case LIST_ITEM:
					linhasClassificadas.put(i++, ConfLineType.LIST_ITEM);
					break;
				case DIALOG_ITEM:
					linhasClassificadas.put(i++, ConfLineType.DIALOG_ITEM);
					break;
				default:
					break;
				}
				break;
			}
		}

		// Adicionando conteúdo SMART
		List<String> smartsLines = new LinkedList<>();
		for (Integer index : linhasClassificadas.keySet()) {
			switch (linhasClassificadas.get(index)) {
			case SMART:
				smartsLines.add(linhas.get(index));
				break;
			default:
				break;
			}
		}
		for (String smartLine : smartsLines) {
			Map<List<String>, Map<Integer, ConfLineType>> results = createNewSmartLines(linhas, linhasClassificadas,
					smartLine);
			for (List<String> result : results.keySet()) {
				linhas = result;
				linhasClassificadas = results.get(result);
			}
		}

		// Preparando Objectos RecordSchema
		String entityName = "";
		List<RecordSchema> records = new LinkedList<>();
		int j = 0;
		for (Integer index : linhasClassificadas.keySet()) {
			switch (linhasClassificadas.get(index)) {
			case ENTIDADE:
				entityName = linhas.get(index);
				j = 1;
				break;
			case ENTIDADE_ITEM:
				RecordSchema recordSchema = createRecordSchema(linhas.get(index), entityName);
				if (recordSchema != null) {
					recordSchema.setOrder(j++);
					records.add(recordSchema);
				} else {
					return ConfFileStatus.ERRO_ENTIDADE_ITENS;
				}

			default:
				break;
			}
		}

		RecordStatus recordSchemaStatus = externalService.insertRecordSchema(records);

		if (!recordSchemaStatus.equals(RecordStatus.SUCESSO)) {
			return ConfFileStatus.ERRO_ENTIDADE_ITENS;
		}

		// Preparando List
		List<AdvancedList> lists = new LinkedList<>();
		AdvancedList currentList = null;
		for (Integer index : linhasClassificadas.keySet()) {
			switch (linhasClassificadas.get(index)) {
			case LIST:
				if (currentList != null) {
					lists.add(currentList);
				}
				currentList = new AdvancedList();
				currentList.setListName(linhas.get(index));
				break;
			case LIST_ITEM:
				String[] itensArray = linhas.get(index).split("\\|");
				List<String> itens = new LinkedList<String>(Arrays.asList(itensArray));
				if (itens.get(0).equals("->")) {
					currentList.addItem(linhas.get(index).substring("->|".length()), AdvancedListOption.ADD);
				}
				if (itens.get(0).equals("<-")) {
					currentList.addItem(linhas.get(index).substring("<-|".length()), AdvancedListOption.REMOVE);
				}
			default:
				break;
			}
		}

		lists.add(currentList);
		if (lists.get(0) != null) {
			for (AdvancedList list : lists) {
				advancedListRepo.save(list);
			}
		}

		// Preparando Dialogs
		String dialogName = "";
		List<DialogSchema> dialogSchemas = new LinkedList<>();
		DialogSchema currentDialogSchema = null;
		List<DialogStepSchema> currentSteps = null;
		int currentStep = 0;
		for (Integer index : linhasClassificadas.keySet()) {
			switch (linhasClassificadas.get(index)) {
			case DIALOG:
				if (currentDialogSchema != null) {
					currentStep = 1;
					for (DialogStepSchema stepToAdd : currentSteps) {
						currentDialogSchema.addStep(currentStep++, stepToAdd);
					}
					dialogSchemas.add(currentDialogSchema);
				}
				currentDialogSchema = new DialogSchema();

				String[] itensArray = linhas.get(index).split("\\|");
				List<String> itens = new LinkedList<String>(Arrays.asList(itensArray));

				currentDialogSchema.setNomeSchema("|D|" + itens.get(0) + "|");
				currentSteps = new LinkedList<>();

				if (itens.size() > 1) {
					if (itens.contains("free")) {
						currentDialogSchema.setNoPermissionRequired(true);
						continue;
					}
					if (itens.contains("onlyAdmin")) {
						currentDialogSchema.setOnlyAdmin(true);
						continue;
					}

				}
				break;
			case DIALOG_ITEM:
				if (currentSteps != null) {
					currentSteps.addAll(createDialogStepSchemas(linhas.get(index)));
				}

			default:
				break;
			}
		}

		if (currentDialogSchema != null) {
			currentStep = 1;
			for (DialogStepSchema stepToAdd : currentSteps) {
				currentDialogSchema.addStep(currentStep++, stepToAdd);
			}
			dialogSchemas.add(currentDialogSchema);
		}

		for (DialogSchema dialogSchema : dialogSchemas) {
			dialogSchemaRepo.save(dialogSchema);
		}

		return ConfFileStatus.SUCESSO;

	}

	private RecordSchema createRecordSchema(String line, String entityName) {

		try {
			// Dividindo a linha
			String[] itensArray = line.split("\\|");
			List<String> itens = new LinkedList<String>(Arrays.asList(itensArray));
			if (itens.size() < 2) {
				return null;
			}

			// Setando principais dados
			RecordSchema record = new RecordSchema();
			record.setEntityName(entityName);
			record.setFieldName(itens.get(0));
			record.setType(RecordType.valueOf(itens.get(1)));
			record.setCreatedByXml(true);

			// Itens adicionais
			if (itens.size() > 2) {
				itens.remove(record.getFieldName());
				itens.remove(record.getType().toString());

				for (String item : itens) {
					if (item.equals("isKey")) {
						record.setIsKey(true);
						continue;
					}
					if (item.contains("default:")) {
						record.setDefaultValue(item.substring("default:".length()));
						continue;
					}
					if (item.equals("notNull")) {
						record.setNotNull(true);
						continue;
					}
					if (item.startsWith("type:")) {
						record.setEntityType(item.substring("type:".length()));
					}
					if (item.startsWith("recordLimits:")) {
						record.setRecordLimits(item.substring("recordLimits:".length()));
					}
				}
			}

			return record;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public SimpleList createSimpleList(String line) {

		SimpleList list = new SimpleList();
		list.setListName(line);
		List<String> itens = new LinkedList<String>(Arrays.asList(line.split("\\|")));

		for (String item : itens) {
			if (item.startsWith("Entity:")) {
				list.setEntityName(item.substring("Entity:".length()));
				continue;
			}
			if (item.startsWith("Field:")) {
				list.setFieldName(item.substring("Field:".length()));
				continue;
			}
			if (item.startsWith("Value:")) {
				list.setValue(item.substring("Value:".length()));
				continue;
			}
			if (item.startsWith("Show:")) {
				list.setFieldToShow(item.substring("Show:".length()));
				continue;
			}
		}

		// Retorno final
		return list;
	}

	public List<DialogStepSchema> createDialogStepSchemas(String line) {
		List<DialogStepSchema> steps = new LinkedList<>();
		List<String> itens = new LinkedList<String>(Arrays.asList(line.split("\\|")));
		List<String> subItens = null;
		if (itens.get(0).contains(":")) {
			subItens = new LinkedList<String>(Arrays.asList(itens.get(0).split(":")));
		}
		DialogStepSchema step = null;

		// Inteligência-------------------------
		// Envio de mensagens
		if (itens.get(0).equals("Messages")) {
			itens.remove(0);
			for (String item : itens) {
				step = new DialogStepSchema();
				step.setStepType(StepType.SIMPLEMESSAGE);
				step.setBotMessage(item);
				steps.add(step);

				step = new DialogStepSchema();
				step.setStepType(StepType.DELAY);
				steps.add(step);
			}
		}

		// Solicitar contato para atualizar os dados do usuário
		if (itens.get(0).equals("RequestContact")) {
			step = new DialogStepSchema();
			step.setStepType(StepType.REQUESTCONTACT);
			step.setBotMessage("Pressione o botão abaixo para fornecer seu número de celular");
			steps.add(step);
		}

		// Menu
		if (itens.get(0).startsWith("Menu")) {
			itens.remove(0);
			step = new DialogStepSchema();
			step.setStepType(StepType.REQUESTINLINEOPTIONLINK);
			step.setBotMessage("Escolha uma das opções abaixo");
			step.setKey("dialog:dialog");
			List<String> inlineKeyboard = new LinkedList<>();
			for (String item : itens) {
				inlineKeyboard.add(item);
			}
			step.setInlineKeyboard(inlineKeyboard);
			if (subItens != null) {
				step.setEntity(subItens.get(1));
			}
			steps.add(step);

			step = new DialogStepSchema();
			step.setStepType(StepType.LINK);
			if (subItens != null) {
				step.setEntity(subItens.get(1));
			}
			steps.add(step);
		}

		// Insert
		if (itens.get(0).startsWith("Insert")) {
			List<RecordSchema> recordSchemasKeys = recordSchemaRepo.findByEntityNameAndIsKey(subItens.get(1), true);
			List<RecordSchema> recordSchemasNotNull = recordSchemaRepo.findByEntityNameAndNotNull(subItens.get(1),
					true);
			List<RecordSchema> allImportantRecordSchema = recordSchemasKeys;
			allImportantRecordSchema.addAll(recordSchemasNotNull);

			step = new DialogStepSchema();
			step.setStepType(StepType.SIMPLEMESSAGE);
			step.setBotMessage("Iniciando a Inserção de um novo registro");
			steps.add(step);

			if (itens.size() == 1) {
				for (RecordSchema schema : allImportantRecordSchema) {
					step = new DialogStepSchema();
					switch (schema.getType()) {
					case RECORD:
						step.setStepType(StepType.REQUESTRECORD);
						step.setKey("record:" + schema.getFieldName());
						step.setBotMessage("Escolha uma das opções abaixo");
						step.setEntity(schema.getEntityType());
						step.setEntityToChange(subItens.get(1));
						break;
					case STRING:
						step.setStepType(StepType.REQUESTSTRING);
						step.setKey("user:" + schema.getFieldName());
						step.setBotMessage("Digite o {{{global:atributo}}} ");
						step.setEntity(subItens.get(1));
						break;
					case BOOLEAN:
						step.setStepType(StepType.REQUESTBOOLEAN);
						step.setKey("user:" + schema.getFieldName());
						step.setBotMessage("Pressione o {{{global:atributo}}}");
						step.setEntity(subItens.get(1));
						break;
					case SELF:
						continue;
					default:
						break;
					}
					steps.add(step);
				}

				step = new DialogStepSchema();
				step.setBotMessage("Confirma os seguintes dados fornecidos?");
				step.setStepType(StepType.REQUESTCONFIRMATION);
				steps.add(step);

				step = new DialogStepSchema();
				step.setEntity(subItens.get(1));
				step.setStepType(StepType.INSERT);
				steps.add(step);

			}
		}

		// UpdateOnly
		if (itens.get(0).startsWith("OnlyUpdate")) {
			List<RecordSchema> recordSchemasKeys = recordSchemaRepo.findByEntityNameAndIsKey(subItens.get(1), true);
			List<RecordSchema> recordSchemasNotNull = recordSchemaRepo.findByEntityNameAndNotNull(subItens.get(1),
					true);
			List<RecordSchema> allImportantRecordSchema = recordSchemasKeys;
			allImportantRecordSchema.addAll(recordSchemasNotNull);

			step = new DialogStepSchema();
			step.setStepType(StepType.SIMPLEMESSAGE);
			step.setBotMessage("Iniciando a Inserção de um novo registro");
			steps.add(step);

			if (itens.size() == 1) {
				for (RecordSchema schema : allImportantRecordSchema) {
					step = new DialogStepSchema();
					switch (schema.getType()) {
					case RECORD:
						step.setStepType(StepType.REQUESTRECORD);
						step.setKey("record:" + schema.getFieldName());
						step.setBotMessage("Escolha uma das opções abaixo");
						step.setEntity(schema.getEntityType());
						step.setEntityToChange(subItens.get(1));
						break;
					case STRING:
						step.setStepType(StepType.REQUESTSTRING);
						step.setKey("user:" + schema.getFieldName());
						step.setBotMessage("Digite o {{{global:atributo}}} ");
						step.setEntity(subItens.get(1));
						break;
					case BOOLEAN:
						step.setStepType(StepType.REQUESTBOOLEAN);
						step.setKey("user:" + schema.getFieldName());
						step.setBotMessage("Pressione o {{{global:atributo}}}");
						step.setEntity(subItens.get(1));
						break;
					case SELF:
						continue;
					default:
						break;
					}
					steps.add(step);
				}

				step = new DialogStepSchema();
				step.setBotMessage("Confirma os seguintes dados fornecidos?");
				step.setStepType(StepType.REQUESTCONFIRMATION);
				steps.add(step);

				step = new DialogStepSchema();
				step.setEntity(subItens.get(1));
				step.setStepType(StepType.UPDATEONLY);
				steps.add(step);

			}
		}

		// Switch
		if (itens.get(0).equals("Switch")) {
			itens.remove(0);
			List<String> inlineKeyboard = new LinkedList<>();
			for (String item : itens) {
				inlineKeyboard.add(item);
			}
			step = new DialogStepSchema();
			step.setStepType(StepType.SWITCH);
			step.setInlineKeyboard(inlineKeyboard);
			steps.add(step);
		}

		// Delete
		if (itens.get(0).startsWith("Delete")) {

			step = new DialogStepSchema();
			step.setStepType(StepType.REQUESTRECORD);
			step.setKey("record:" + subItens.get(1));
			step.setBotMessage("Escolha qual registro deseja deletar");
			step.setEntity(subItens.get(1));
			steps.add(step);

			step = new DialogStepSchema();
			step.setBotMessage("Confirma os seguintes dados fornecidos?");
			step.setStepType(StepType.REQUESTCONFIRMATION);
			steps.add(step);

			step = new DialogStepSchema();
			step.setEntity(subItens.get(1));
			step.setStepType(StepType.DELETE);
			steps.add(step);
		}

		// Select
		if (itens.get(0).startsWith("Select")) {
			String entityName = subItens.get(1);
			List<RecordSchema> recordSchemas = recordSchemaRepo.findByEntityName(entityName);
			for (RecordSchema recordSchema : recordSchemas) {
				step = new DialogStepSchema();
				step.setStepType(StepType.SIMPLEMESSAGE);
				step.setBotMessage(
						"Iniciando a busca de algum registro para ver detalhes.\nPressione Pular para desconsiderar algum campo na busca");
				steps.add(step);

				step = new DialogStepSchema();
				step.addParameter("choicePular", "choicePular");
				switch (recordSchema.getType()) {
				case STRING:
					step.setStepType(StepType.REQUESTSTRING);
					step.setKey("query:" + recordSchema.getFieldName());
					step.setBotMessage("Digite o atributo {{{global:atributo}}} da entidade {{{global:entidade}}}");
					step.setEntity(entityName);
					break;
				case BOOLEAN:
					step.setStepType(StepType.REQUESTBOOLEAN);
					step.setKey("query:" + recordSchema.getFieldName());
					step.setBotMessage("Pressione o atributo {{{global:atributo}}} da entidade {{{global:entidade}}}");
					step.setEntity(entityName);
					break;
				case RECORD:
					step.setStepType(StepType.REQUESTRECORD);
					step.setKey("query:" + recordSchema.getFieldName());
					step.setBotMessage("Escolha uma das opções abaixo o atributo {{{global:atributo}}}");
					step.setEntity(recordSchema.getEntityType());
					SimpleList simpleList = new SimpleList();
					simpleList.setListName("All" + recordSchema.getEntityType());
					simpleList.setEntityName(recordSchema.getEntityType());
					step.setSimpleList(simpleList);
					break;
				default:
					break;
				}
				steps.add(step);
			}

			step = new DialogStepSchema();
			step.setStepType(StepType.REQUESTRECORD);
			step.setKey("record:" + entityName);
			step.setBotMessage("Escolha o registro que deseja ver detalhes");
			step.setEntity(entityName);
			steps.add(step);

			step = new DialogStepSchema();
			step.setStepType(StepType.SHOWRECORD);
			step.setEntity(entityName);
			steps.add(step);
		}

		// AllSelect
		if (itens.get(0).startsWith("AllSelect")) {
			String entityName = subItens.get(1);

			step = new DialogStepSchema();
			step.setStepType(StepType.REQUESTRECORD);
			step.setKey("record:" + entityName);
			step.setBotMessage("Escolha o registro que deseja ver detalhes");
			step.setEntity(entityName);
			steps.add(step);

			step = new DialogStepSchema();
			step.setStepType(StepType.SHOWRECORD);
			step.setEntity(entityName);
			steps.add(step);
		}

		// AllDelete
		if (itens.get(0).startsWith("AllDelete")) {
			String entityName = subItens.get(1);

			step = new DialogStepSchema();
			step.setBotMessage("Cuidado, esta opção irá apagar todos os registros " + entityName
					+ " cadastrados, deseja realmente continuar?");
			step.setStepType(StepType.REQUESTCONFIRMATION);
			steps.add(step);

			step = new DialogStepSchema();
			step.setStepType(StepType.DELETE);
			step.setEntity(entityName);
			steps.add(step);
		}

		// Update
		if (itens.get(0).startsWith("Update")) {
			String entityName = subItens.get(1);
			List<RecordSchema> recordSchemas = recordSchemaRepo.findByEntityName(entityName);

			step = new DialogStepSchema();
			step.setStepType(StepType.SIMPLEMESSAGE);
			step.setBotMessage(
					"Iniciando a busca para editar um registro.\nPressione Pular para desconsiderar algum campo na busca");
			steps.add(step);

			for (RecordSchema recordSchema : recordSchemas) {
				step = new DialogStepSchema();
				step.addParameter("choicePular", "choicePular");
				switch (recordSchema.getType()) {
				case STRING:
					step.setStepType(StepType.REQUESTSTRING);
					step.setKey("query:" + recordSchema.getFieldName());
					step.setBotMessage("Digite o atributo {{{global:atributo}}} da entidade {{{global:entidade}}}");
					step.setEntity(entityName);
					break;
				case BOOLEAN:
					step.setStepType(StepType.REQUESTBOOLEAN);
					step.setKey("query:" + recordSchema.getFieldName());
					step.setBotMessage("Pressione o atributo {{{global:atributo}}} da entidade {{{global:entidade}}}");
					step.setEntity(entityName);
					break;
				case RECORD:
					step.setStepType(StepType.REQUESTRECORD);
					step.setKey("query:" + recordSchema.getFieldName());
					step.setBotMessage("Escolha uma das opções abaixo o atributo {{{global:atributo}}}");
					step.setEntity(recordSchema.getEntityType());
					SimpleList simpleList = new SimpleList();
					simpleList.setListName("All" + recordSchema.getEntityType());
					simpleList.setEntityName(recordSchema.getEntityType());
					step.setSimpleList(simpleList);
					break;
				default:
					break;
				}
				steps.add(step);
			}

			step = new DialogStepSchema();
			step.setStepType(StepType.REQUESTRECORD);
			step.setKey("record:" + entityName);
			step.setBotMessage("Escolha o registro que deseja editar");
			step.setEntity(entityName);
			steps.add(step);

			step = new DialogStepSchema();
			step.setStepType(StepType.SIMPLEMESSAGE);
			step.setBotMessage(
					"Comece a preencher os campos que deseja alterar.\nPressione Pular para desconsiderar algum campo na edição");
			steps.add(step);

			for (RecordSchema recordSchema : recordSchemas) {
				step = new DialogStepSchema();
				step.addParameter("choicePular", "choicePular");
				switch (recordSchema.getType()) {
				case STRING:
					step.setStepType(StepType.REQUESTSTRING);
					step.setKey("update:" + recordSchema.getFieldName());
					step.setBotMessage("Digite o novo {{{global:atributo}}}.\nConteúdo Atual: {{{global:recordField:"
							+ recordSchema.getFieldName() + "}}}");
					step.setEntity(entityName);
					break;
				case BOOLEAN:
					step.setStepType(StepType.REQUESTBOOLEAN);
					step.setKey("update:" + recordSchema.getFieldName());
					step.setBotMessage("Pressione o novo {{{global:atributo}}}.\nConteúdo Atual: {{{global:recordField:"
							+ recordSchema.getFieldName() + "}}}");
					step.setEntity(entityName);
					break;
				case RECORD:
					step.setStepType(StepType.REQUESTRECORD);
					step.setKey("update:" + recordSchema.getFieldName());
					step.setBotMessage("Pressione o novo {{{global:atributo}}}.\nConteúdo Atual: {{{global:recordField:"
							+ recordSchema.getFieldName() + "}}}");
					step.setEntity(recordSchema.getEntityType());
					SimpleList simpleList = new SimpleList();
					simpleList.setListName("All" + recordSchema.getEntityType());
					simpleList.setEntityName(recordSchema.getEntityType());
					step.setSimpleList(simpleList);
					break;
				default:
					break;
				}
				steps.add(step);
			}

			step = new DialogStepSchema();
			step.setBotMessage("Confirma os seguintes dados fornecidos?");
			step.setStepType(StepType.REQUESTCONFIRMATION);
			steps.add(step);

			step = new DialogStepSchema();
			step.setEntity(subItens.get(1));
			step.setStepType(StepType.UPDATE);
			steps.add(step);

		}

		// Atualizar estrutura

		if (itens.get(0).equals("RefreshStructure")) {

			step = new DialogStepSchema();
			step.setStepType(StepType.SIMPLEMESSAGE);
			step.setBotMessage(
					"Com esta ação, as definições de entities, dialogs e masts serão subrepostos pelo conteúdo atual do arquivo de configuração, qualquer diálogo ativo neste momento será finalizado.");
			steps.add(step);

			step = new DialogStepSchema();
			step.setBotMessage("Confirma a atualização?");
			step.setStepType(StepType.REQUESTCONFIRMATION);
			steps.add(step);

			step = new DialogStepSchema();
			step.setStepType(StepType.REFRESH_STRUCTURE);
			steps.add(step);
		}

		// Desativar dialog

		if (itens.get(0).startsWith("Disable")) {
			String dialogName = subItens.get(1);

			step = new DialogStepSchema();
			step.setStepType(StepType.INSERTDECISION);
			step.addParameter("dialog:dialog", dialogName);
			steps.add(step);

			step = new DialogStepSchema();
			step.setStepType(StepType.DISABLE_DIALOG);
			steps.add(step);
		}

		// Ativar dialog

		if (itens.get(0).startsWith("Enable")) {
			String dialogName = subItens.get(1);

			step = new DialogStepSchema();
			step.setStepType(StepType.INSERTDECISION);
			step.addParameter("dialog:dialog", dialogName);
			steps.add(step);

			step = new DialogStepSchema();
			step.setStepType(StepType.ENABLE_DIALOG);
			steps.add(step);
		}

		// Mostrar todos os dados de n entidades

		if (itens.get(0).equals("ShowAllRecordFields")) {
			itens.remove(0);
			step = new DialogStepSchema();
			step.setStepType(StepType.SHOWALLRECORDFIELDS);
			List<String> inlineKeyboard = new LinkedList<>();
			for (String item : itens) {
				inlineKeyboard.add(item);
			}
			step.setInlineKeyboard(inlineKeyboard);
			steps.add(step);
		}
		
		// Deleta registros cuja chave é do tipo self
		
		if (itens.get(0).equals("SelfDelete")) {
			step = new DialogStepSchema();
			step.setStepType(StepType.DELETESELF);
			steps.add(step);
		}

		return steps;

	}

	public List<String> smartInsertOptions(DialogStepSchema dialogStepSchema, Map<String, String> dialogDecisions) {

		String entityToInsert = dialogStepSchema.getEntityToChange();
		RecordSchema recordSchema = recordSchemaRepo.findByEntityNameAndFieldName(entityToInsert,
				dialogStepSchema.getKey().substring("record:".length()));
		String entityRecord = dialogStepSchema.getEntity();
		int countRecordsKeys = entityService.countRecordTypeKeys(entityToInsert);
		int countKeys = entityService.countKeys(entityToInsert);
		String key1 = null;
		String key2 = null;
		String field1 = null;
		for (RecordSchema schema : recordSchemaRepo.findByEntityNameAndIsKey(entityToInsert, true)) {
			switch (schema.getOrder()) {
			case 1:
				key1 = schema.getEntityType();
				field1 = schema.getFieldName();
				break;
			case 2:
				key2 = schema.getEntityType();
				break;
			default:
				break;
			}
		}

		if (countRecordsKeys == countKeys) {
			switch (countKeys) {
			case 1:
				return entityService.difBetweenEntities(entityRecord, entityToInsert);
			case 2:
				List<String> result = new LinkedList<>();
				if (recordSchema.getOrder() == 1) {
					List<String> possibilities = entityService.difBetweenEntities(key1, key2, entityToInsert);
					for (String possibility : possibilities) {
						List<String> key = keyService.parseStringKey(possibility);
						int countKey1 = entityService.countKeys(key1);
						if (countKey1 == 1) {
							result.add(key.get(0));
						} else {
							result.add(key.get(0) + "-" + key.get(1));
						}
					}
				}
				if (recordSchema.getOrder() == 2) {
					List<String> possibilities = entityService
							.difBetweenEntitiesChoice1(dialogDecisions.get("record:" + field1), key2, entityToInsert);
					for (String possibility : possibilities) {
						List<String> key = keyService.parseStringKey(possibility);
						int countKey1 = entityService.countKeys(key1);
						int countKey2 = entityService.countKeys(key2);
						if (countKey1 == 1 && countKey2 == 1) {
							result.add(key.get(1));
						}
						if (countKey1 == 2 && countKey2 == 1) {
							result.add(key.get(2));
						}
						if (countKey1 == 1 && countKey2 == 2) {
							result.add(key.get(1) + "-" + key.get(2));
						}
						if (countKey1 == 2 && countKey2 == 2) {
							result.add(key.get(2) + "-" + key.get(3));
						}
					}
				}
				return keyService.removeRepeatedValue(result);
			default:
				break;
			}
		} else {
			return null;
		}
		return null;

	}

	public Map<List<String>, Map<Integer, ConfLineType>> createNewSmartLines(List<String> lines,
			Map<Integer, ConfLineType> linhasClassificadas, String lineTarget) {

		Map<List<String>, Map<Integer, ConfLineType>> result = new HashMap<List<String>, Map<Integer, ConfLineType>>();

		// CRUDFree
		if (lineTarget.startsWith("CrudFree:")) {
			String entityName = lineTarget.substring("CrudFree:".length());
			boolean isMenu = false;
			int index = 0;
			for (String line : lines) {
				if (isMenu) {
					lines.set(index, line + "|" + entityName);
					break;
				}
				if (line.equals("Menu|free")) {
					isMenu = true;
				}
				index++;
			}

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Dialogs");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.OBJETO);

			lines.add(entityName + "|free");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG);

			lines.add("Menu:" + entityName + "|Adicionar|Editar|Consultar|Excluir");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG_ITEM);

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Adicionar " + entityName + "|free");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG);

			lines.add("Insert:" + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG_ITEM);

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Editar " + entityName + "|free");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG);

			lines.add("Update:" + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG_ITEM);

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Consultar " + entityName + "|free");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG);

			lines.add("Select:" + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG_ITEM);

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Excluir " + entityName + "|free");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG);

			lines.add("Delete:" + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG_ITEM);
		}

		// CRUD
		if (lineTarget.startsWith("Crud:")) {
			String entityName = lineTarget.substring("Crud:".length());
			boolean isMenu = false;
			int index = 0;
			for (String line : lines) {
				if (isMenu) {
					lines.set(index, line + "|" + entityName);
					break;
				}
				if (line.equals("Menu|free")) {
					isMenu = true;
				}
				index++;
			}

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Dialogs");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.OBJETO);

			lines.add(entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG);

			lines.add("Menu:" + entityName + "|Adicionar|Editar|Consultar|Excluir");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG_ITEM);

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Adicionar " + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG);

			lines.add("Insert:" + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG_ITEM);

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Editar " + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG);

			lines.add("Update:" + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG_ITEM);

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Consultar " + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG);

			lines.add("Select:" + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG_ITEM);

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Excluir " + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG);

			lines.add("Delete:" + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG_ITEM);
		}

		// Basic
		if (lineTarget.equals("Basic")) {

			boolean isMenu = false;
			int index = 0;
			for (String line : lines) {
				if (isMenu) {
					lines.set(index, line + "|Administração");
					break;
				}
				if (line.equals("Menu|free")) {
					isMenu = true;
				}
				index++;
			}

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Entities");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.OBJETO);

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Permissão");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.ENTIDADE);

			lines.add("Id Telegram|RECORD|isKey|type:botUser|");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.ENTIDADE_ITEM);

			lines.add("Diálogo|RECORD|isKey|type:dialogSchema|recordLimits:noPermissionRequired=false");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.ENTIDADE_ITEM);

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Dialogs");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.OBJETO);

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Administração");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG);

			lines.add("Menu|Permissão|Atualizar");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG_ITEM);

			String entityName = "Permissão";

			lines.add(entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG);

			lines.add("Menu:" + entityName + "|Adicionar|Editar|Consultar|Excluir");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG_ITEM);

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Adicionar " + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG);

			lines.add("Insert:" + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG_ITEM);

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Editar " + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG);

			lines.add("Update:" + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG_ITEM);

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Consultar " + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG);

			lines.add("Select:" + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG_ITEM);

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Excluir " + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG);

			lines.add("Delete:" + entityName);
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG_ITEM);

			lines.add("");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.SEPARADOR);

			lines.add("Atualizar");
			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG);

			lines.add("RefreshStructure");

			linhasClassificadas.put(lines.size() - 1, ConfLineType.DIALOG_ITEM);
		}

		result.put(lines, linhasClassificadas);

		return result;

	}

}
