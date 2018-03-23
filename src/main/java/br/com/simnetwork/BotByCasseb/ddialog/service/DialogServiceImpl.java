package br.com.simnetwork.BotByCasseb.ddialog.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;

import br.com.simnetwork.BotByCasseb.botDialog.StepType;
import br.com.simnetwork.BotByCasseb.ddbs.model.Record;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordSchema;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordStatus;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordType;
import br.com.simnetwork.BotByCasseb.ddbs.repository.RecordRepository;
import br.com.simnetwork.BotByCasseb.ddbs.repository.RecordSchemaRepository;
import br.com.simnetwork.BotByCasseb.ddbs.service.KeyService;
import br.com.simnetwork.BotByCasseb.ddbs.service.ListService;
import br.com.simnetwork.BotByCasseb.ddbs.service.RecordService;
import br.com.simnetwork.BotByCasseb.ddialog.model.Dialog;
import br.com.simnetwork.BotByCasseb.ddialog.model.DialogSchema;
import br.com.simnetwork.BotByCasseb.ddialog.model.DialogStatus;
import br.com.simnetwork.BotByCasseb.ddialog.model.DialogStepSchema;
import br.com.simnetwork.BotByCasseb.ddialog.repository.DialogRepository;
import br.com.simnetwork.BotByCasseb.service.EasyWayService;
import br.com.simnetwork.BotByCasseb.service.StartService;
import br.com.simnetwork.BotByCasseb.telegramBot.model.Bot;
import br.com.simnetwork.BotByCasseb.telegramBot.model.BotUser;
import br.com.simnetwork.BotByCasseb.telegramBot.service.BotUserService;
import br.com.simnetwork.BotByCasseb.telegramBot.service.KeyboardService;

@Service("dialogService")
public class DialogServiceImpl implements DialogService {

	@Autowired
	private DialogRepository dialogRepo;
	@Autowired
	private DialogSchemaService dialogSchemaService;
	@Autowired
	private BotUserService botUserService;
	@Autowired
	private KeyboardService keyboardService;
	@Autowired
	private DialogStepSchemaService dialogStepSchemaService;
	@Autowired
	private RecordService entityService;
	@Autowired
	private DecisionService decisionService;
	@Autowired
	private ListService listService;
	@Autowired
	private EasyWayService easyWayService;
	@Autowired
	private StartService startService;
	@Autowired
	private RecordSchemaRepository recordSchemaRepo;
	@Autowired
	private KeyService keyService;
	@Autowired
	private RecordRepository recordRepo;

	@Override
	public void decideDialog(Update update) {
		if (dialogSchemaService.findDialogSchemabyNomeSchema("|D|Menu|") != null) {
			User user;
			String callBackData = null;
			String keyboardMessage = null;
			Message message;
			if (update.message() != null) {
				user = update.message().from();
				message = update.message();
				keyboardMessage = update.message().text();
			} else {
				user = update.callbackQuery().from();
				callBackData = update.callbackQuery().data();
				message = update.callbackQuery().message();
			}
			BotUser botUser = new BotUser(user);
			if (keyboardMessage != null && keyboardMessage.equals("Menu")
					&& dialogRepo.findById(botUser.getId()) != null) {
				dialogRepo.delete(dialogRepo.findById(botUser.getId()));
			}
			if (dialogRepo.findById(botUser.getId()) == null) {
				createDialog(user, dialogSchemaService.findDialogSchemabyNomeSchema("|D|Menu|"));
			}
			executeDialog(user, message, callBackData);
		}

	}

	@Override
	public void createDialog(User user, DialogSchema dialogSchema) {
		BotUser botUser = botUserService.createBotUser(user);
		Dialog dialog;
		if (botUser.getContact() == null) {
			dialog = new Dialog(botUser.getId(), dialogSchemaService.findDialogSchemabyNomeSchema("|D|Bem Vindo|"));
		} else {
			dialog = new Dialog(botUser.getId(), dialogSchema);
		}
		dialogRepo.save(dialog);
	}

	@Override
	public void executeDialog(User user, Message message, String callBackData) {

		boolean executeAgain = false;

		do {

			// Preparando dados para execução
			BotUser botUser = botUserService.locateBotUser(user.id());
			Dialog dialog = dialogRepo.findOne(botUser.getId());
			DialogSchema dialogSchema = dialog.getDialogSchema();
			DialogStepSchema dialogStepSchema = dialogSchema.getSteps().get(dialog.getCurrentStep());
			if (dialogStepSchema.getParameters().get("choicePular") != null) {
				List<String> keyboardOptions = new LinkedList<>();
				keyboardOptions.add("Menu");
				keyboardOptions.add("Pular");
				dialogStepSchema.setKeyboardOptions(keyboardOptions);
			}
			Keyboard keyboard = dialogStepSchemaService.getKeyboard(dialogStepSchema);
			InlineKeyboardMarkup inlineKeyboard = dialogStepSchemaService.getInlineKeyboard(dialogStepSchema);

			// Setando decisoes globais
			if (dialogStepSchema.getEntity() != null) {
				dialog.addDecision("global:entidade", dialogStepSchema.getEntity());
			}
			if (dialogStepSchema.getKey() != null) {
				if (dialogStepSchema.getKey().contains("user:")) {
					dialog.addDecision("global:atributo", dialogStepSchema.getKey().replaceFirst("user:", ""));
				}
				if (dialogStepSchema.getKey().contains("query:")) {
					dialog.addDecision("global:atributo", dialogStepSchema.getKey().replaceFirst("query:", ""));
				}
				if (dialogStepSchema.getKey().contains("update:")) {
					dialog.addDecision("global:atributo", dialogStepSchema.getKey().replaceFirst("update:", ""));
				}

			}
			dialog.addDecision("global:id telegram", botUser.getId() + "");
			dialog.addDecision("global:nome completo", botUser.getFirstName() + " " + botUser.getLastName());
			dialog.addDecision("global:telefone", botUser.getContact());

			// Atualizando listas de decisões
			Map<String, String> userDecisions = decisionService.getDecisionsFilter(dialog.getDecisions(), "user:");
			Map<String, String> globalDecisions = decisionService.getDecisionsFilter(dialog.getDecisions(), "global:");
			Map<String, String> dialogDecisions = decisionService.getDecisionsFilter(dialog.getDecisions(), "dialog:");
			Map<String, String> recordDecisions = decisionService.getDecisionsFilter(dialog.getDecisions(), "record:");
			Map<String, String> updateDecisions = decisionService.getDecisionsFilter(dialog.getDecisions(), "update:");
			userDecisions.remove("unico");

			// Preparando mensagem parametrizada
			if (dialogStepSchema.getBotMessage() != null) {
				for (String decision : dialog.getDecisions().keySet()) {
					String decisionChanged = "{{{" + decision + "}}}";
					dialogStepSchema.setBotMessage(dialogStepSchema.getBotMessage().replace(decisionChanged,
							dialog.getDecisions().get(decision)));
				}
			}

			// Tratando entity default
			if (dialogStepSchema.getEntity() == null && dialogSchema.getDefaultEntity() != null) {
				dialogStepSchema.setEntity(dialogSchema.getDefaultEntity());
			}

			// Tratamento da opção de pular passo
			boolean pular = false;
			if (message.text() != null) {
				if (message.text().equals("Pular")) {
					pular = true;
				}
			}

			// Execução baseado no tipo do passo---------------------------------

			if (dialogStepSchema.getStepType().equals(StepType.SIMPLEMESSAGE)) {
				executeSchemaSimpleMessage(botUser, dialogStepSchema, keyboard);
				executeAgain = true;
			}

			// Requisição dos dados de contato
			if (dialogStepSchema.getStepType().equals(StepType.REQUESTCONTACT)) {
				if (message.contact() == null) {
					executeRequestContact(botUser, dialogStepSchema);
					executeAgain = false;
				} else {
					if (!message.contact().userId().equals(botUser.getId())) {
						executeRequestContact(botUser, dialogStepSchema);
						executeAgain = false;
					} else {
						botUserService.updateBotUserContact(botUser, message.contact());
						executeAgain = true;
					}
				}
			}

			// Requisição de uma string
			if (dialogStepSchema.getStepType().equals(StepType.REQUESTSTRING)) {
				if (!dialog.getDialogStatus().equals(DialogStatus.AGUARDANDO)) {
					executeSchemaSimpleMessage(botUser, dialogStepSchema, keyboard);
					dialog.setDialogStatus(DialogStatus.AGUARDANDO);
					executeAgain = false;
				} else {
					if (!pular) {
						dialog.addDecision(dialogStepSchema.getKey(), message.text());
					}
					dialog.setDialogStatus(DialogStatus.INICIO);
					executeAgain = true;
				}
			}

			// Requisição de uma opção usando uma lista
			if (dialogStepSchema.getStepType().equals(StepType.REQUESTINLINEOPTION)) {
				if (!dialog.getDialogStatus().equals(DialogStatus.AGUARDANDO)) {
					executeRequestInlineOption(botUser, dialogStepSchema, inlineKeyboard);
					dialog.setDialogStatus(DialogStatus.AGUARDANDO);
					executeAgain = false;
				} else {
					if (!pular) {
						if (callBackData != null) {
							dialog.addDecision(dialogStepSchema.getKey(), callBackData);
							dialog.setDialogStatus(DialogStatus.INICIO);
							executeAgain = true;
						} else {
							executeRequestInlineOption(botUser, dialogStepSchema, inlineKeyboard);
							executeAgain = false;
						}
					} else {
						dialog.setDialogStatus(DialogStatus.INICIO);
						executeAgain = true;
					}

				}
			}

			// Requisição de uma opção usando uma lista para acessar outro dialog
			if (dialogStepSchema.getStepType().equals(StepType.REQUESTINLINEOPTIONLINK)) {
				if (!dialog.getDialogStatus().equals(DialogStatus.AGUARDANDO)) {
					List<String> options = new LinkedList<>();
					for (String option : dialogStepSchema.getInlineKeyboard()) {
						List<String> freeDialogs = dialogSchemaService.findByNoPermissionRequired();

						boolean havePermission = true;
						DialogSchema schema = null;
						String entityName = dialogStepSchema.getEntity();
						if (entityName == null) {
							schema = dialogSchemaService.findDialogSchemabyNomeSchema("|D|" + option + "|");
						} else {
							schema = dialogSchemaService
									.findDialogSchemabyNomeSchema("|D|" + option + " " + entityName + "|");
						}
						boolean isFree = freeDialogs.contains(schema.getNomeSchema());
						boolean isActive = schema.isActive();
						String permissionKey = botUser.getId() + "-" + botUser.getFirstName() + " "
								+ botUser.getLastName() + "-" + schema.getNomeSchema();
						if (entityService.findByKeys("Permissão", permissionKey).isEmpty()) {
							havePermission = false;
						}

						if (isActive && (isFree || havePermission)) {
							options.add(option);
						}
					}
					inlineKeyboard = keyboardService.getSimpleInlineKeyboard(options);
					executeRequestInlineOption(botUser, dialogStepSchema, inlineKeyboard);
					dialog.setDialogStatus(DialogStatus.AGUARDANDO);
					executeAgain = false;
				} else {
					if (callBackData != null) {
						dialog.addDecision(dialogStepSchema.getKey(), callBackData);
						dialog.setDialogStatus(DialogStatus.INICIO);
						executeAgain = true;
					} else {
						executeRequestInlineOption(botUser, dialogStepSchema, inlineKeyboard);
						executeAgain = false;
					}

				}
			}

			// Requisição de confirmação dos dados
			if (dialogStepSchema.getStepType().equals(StepType.REQUESTCONFIRMATION)) {
				if (!dialog.getDialogStatus().equals(DialogStatus.AGUARDANDO)) {

					StringBuilder updatedMessage = new StringBuilder();
					updatedMessage.append(dialogStepSchema.getBotMessage());
					updatedMessage.append("\n\n");
					for (String decisionKey : userDecisions.keySet()) {
						updatedMessage.append(decisionKey + " : " + userDecisions.get(decisionKey) + "\n");
					}
					for (String decisionKey : recordDecisions.keySet()) {
						if (!decisionKey.equals("unico")) {
							updatedMessage.append(decisionKey + " : " + recordDecisions.get(decisionKey) + "\n");
						}
					}
					/*
					 * for (String decisionKey : userDecisions.keySet()) {
					 * updatedMessage.append(decisionKey + " : " + userDecisions.get(decisionKey) +
					 * "\n"); }
					 * 
					 * 
					 * if (!recordDecisions.isEmpty()) { updatedMessage.append("\n");
					 * updatedMessage.append("Os seguintes registros:\n");
					 * recordDecisions.remove("unico"); for (String decision :
					 * recordDecisions.values()) { updatedMessage.append(decision + "\n"); } }
					 * 
					 * if (!updateDecisions.isEmpty()) { updatedMessage.append("\n");
					 * updatedMessage.append("Serão alterados para:\n");
					 * updateDecisions.remove("unico"); for (String decisionKey :
					 * updateDecisions.keySet()) { updatedMessage.append(decisionKey + ":" +
					 * updateDecisions.get(decisionKey) + "\n"); } }
					 */

					dialogStepSchema.setBotMessage(updatedMessage.toString());

					List<String> options = new LinkedList<String>();
					options.add("Sim");
					options.add("Não");
					inlineKeyboard = keyboardService.getSimpleInlineKeyboard(options);

					executeRequestInlineOption(botUser, dialogStepSchema, inlineKeyboard);
					dialog.setDialogStatus(DialogStatus.AGUARDANDO);
					executeAgain = false;
				} else {
					if (callBackData != null) {
						if (callBackData.equals("Sim")) {
							dialog.setDialogStatus(DialogStatus.INICIO);
							executeAgain = true;
						} else {
							executeCustomSimpleMessage(botUser, "Ação cancelada", keyboard);
							dialog.setDialogStatus(DialogStatus.FIM);
						}
					} else {
						executeRequestInlineOption(botUser, dialogStepSchema, inlineKeyboard);
						executeAgain = false;
					}
				}
			}

			// Insert de registro no banco
			if (dialogStepSchema.getStepType().equals(StepType.INSERT)) {
				Map<String, String> decisions = new HashMap<String, String>();
				decisions.putAll(recordDecisions);
				decisions.putAll(userDecisions);

				List<RecordSchema> schemas = recordSchemaRepo.findByEntityName(dialogStepSchema.getEntity());

				String userKey = botUser.getId() + "-" + botUser.getFirstName() + " " + botUser.getLastName();
				for (RecordSchema schema : schemas) {
					if (schema.getType().equals(RecordType.SELF)) {
						decisions.put(schema.getFieldName(), userKey);
					}
				}

				// Abaixo customização para cliente LORENZO
				if (dialogStepSchema.getEntity().equals("Pedido Padrão")
								|| dialogStepSchema.getEntity().equals("Monte sua marmita")) {
					entityService.deleteByKey("Pedido Padrão", userKey);
					entityService.deleteByKey("Monte sua marmita", userKey);
				}
				
				RecordStatus recordStatus = entityService.insertRecord(dialogStepSchema.getEntity(), decisions);

				switch (recordStatus) {
				case SUCESSO:
					executeCustomSimpleMessage(botUser, "Registro salvo com sucesso", null);
					break;
				case ENTIDADE_INEXISTENTE:
					executeCustomSimpleMessage(botUser, "Não foi definido a estrutura desta entidade no XML", null);
					break;
				case CAMPO_OBRIGATORIO_NULL:
					executeCustomSimpleMessage(botUser, "Não foi informado todos os campos obrigatórios", null);
					break;
				case CHAVE_NULL:
					executeCustomSimpleMessage(botUser, "Não foi informado a chave do registro", null);
					break;
				case REGISTRO_JA_EXISTE:
					executeCustomSimpleMessage(botUser, "O registro em questão já existe", null);
				default:
					break;
				}

				dialog.setDecisions(decisionService.cleanDecisions(dialog.getDecisions(), "user:"));

				executeAgain = true;

			}

			// Update unico registro de uma tabela
			if (dialogStepSchema.getStepType().equals(StepType.UPDATEONLY)) {
				String entityName = dialogStepSchema.getEntity();
				Map<String, String> decisions = new HashMap<String, String>();
				decisions.putAll(recordDecisions);
				decisions.putAll(userDecisions);
				entityService.resetEntity(entityName);
				entityService.insertRecord(dialogStepSchema.getEntity(), decisions);
			}

			// Alterna dados entre tabelas
			if (dialogStepSchema.getStepType().equals(StepType.SWITCH)) {
				String entityTarget = dialogStepSchema.getInlineKeyboard().remove(0);
				String entityModel = dialogStepSchema.getInlineKeyboard().remove(0);

				entityService.resetEntity(entityTarget);

				List<Record> recordsModel = entityService.findByEntityName(entityModel);
				for (Record recordModel : recordsModel) {
					recordModel.setEntityName(entityTarget);
					recordModel.setObjectId(null);
					recordRepo.save(recordModel);
				}

			}

			// Requisição de Record
			if (dialogStepSchema.getStepType().equals(StepType.REQUESTRECORD)) {
				if (!dialog.getDialogStatus().equals(DialogStatus.AGUARDANDO)) {
					List<String> records = new LinkedList<>();

					if (dialogStepSchema.getSimpleList() == null && dialogStepSchema.getAdvancedList() == null) {
						if (dialogStepSchema.getEntityToChange() != null) {
							records = easyWayService.smartInsertOptions(dialogStepSchema, dialog.getDecisions());
							if (records == null) {
								records = entityService.findByFields(dialogStepSchema.getEntity(),
										dialog.getDecisions());
							}
						} else {
							records = entityService.findByFields(dialogStepSchema.getEntity(), dialog.getDecisions());
						}

					} else {
						if (dialogStepSchema.getSimpleList() != null) {
							records = listService.getSimpleList(dialogStepSchema.getSimpleList(),
									dialog.getDecisions());
						}
						if (dialogStepSchema.getAdvancedList() != null) {
							records = listService.getAdvancedList(dialogStepSchema.getAdvancedList(),
									dialog.getDecisions());
						}

					}
					if (!records.isEmpty()) {
						executeRequestInlineOption(botUser, dialogStepSchema,
								keyboardService.getSimpleInlineKeyboard(records));
						dialog.setDialogStatus(DialogStatus.AGUARDANDO);
						executeAgain = false;
					} else {
						executeCustomSimpleMessage(botUser, "Não há registros", inlineKeyboard);
						executeAgain = true;
					}

				} else {
					if (!pular) {
						if (callBackData != null) {
							dialog.addDecision(dialogStepSchema.getKey(), callBackData);
							for (Record record : entityService.findByKeys(dialogStepSchema.getEntity(), callBackData)) {
								dialog.addDecision("global:recordField:" + record.getFieldName(), record.getValue());
							}
							dialog.setDialogStatus(DialogStatus.INICIO);
							executeAgain = true;
						} else {
							executeRequestInlineOption(botUser, dialogStepSchema, inlineKeyboard);
							executeAgain = false;
						}
					} else {
						dialog.setDialogStatus(DialogStatus.INICIO);
						executeAgain = true;
					}

				}
			}

			// Link de um diálogo para outro
			if (dialogStepSchema.getStepType().equals(StepType.LINK)) {
				String dialogName = dialogDecisions.get("unico");
				dialogRepo.delete(dialogRepo.findById(botUser.getId()));
				if (dialogStepSchema.getEntity() == null) {
					dialogName = "|D|" + dialogName + "|";
				} else {
					dialogName = "|D|" + dialogName + " " + dialogStepSchema.getEntity() + "|";
				}
				dialogSchema = dialogSchemaService.findDialogSchemabyNomeSchema(dialogName);
				createDialog(user, dialogSchema);
				dialog = dialogRepo.findOne(botUser.getId());
				dialogSchema = dialog.getDialogSchema();
				dialogStepSchema = dialogSchema.getSteps().get(dialog.getCurrentStep());
				keyboard = dialogStepSchemaService.getKeyboard(dialogStepSchema);
				inlineKeyboard = dialogStepSchemaService.getInlineKeyboard(dialogStepSchema);
				dialog.setCurrentStep(0);
				executeAgain = true;
			}

			// Mostra os dados de um record
			if (dialogStepSchema.getStepType().equals(StepType.SHOWRECORD)) {
				String recordKey = recordDecisions.get("unico");
				String entityName = dialogStepSchema.getEntity();
				List<Record> record = entityService.findByKeys(entityName, recordKey);
				if (record.size() != 0) {
					StringBuilder resposta = new StringBuilder();
					resposta.append("Registro " + record.get(0).getKey() + "\n\n");
					for (Record recordField : record) {
						resposta.append(recordField.getFieldName());
						resposta.append(" : ");
						resposta.append(recordField.getValue());
						resposta.append("\n");
					}
					executeCustomSimpleMessage(botUser, resposta.toString(), null);
					dialog.setDecisions(decisionService.cleanDecisions(dialog.getDecisions(), "record:"));
				}
				executeAgain = true;
			}

			// Inserir registro na lista de decision
			if (dialogStepSchema.getStepType().equals(StepType.INSERTDECISION)) {
				for (String key : dialogStepSchema.getParameters().keySet()) {
					dialog.addDecision(key, dialogStepSchema.getParameters().get(key));
				}
				executeAgain = true;
			}

			// Atualizar dados de um record
			if (dialogStepSchema.getStepType().equals(StepType.UPDATE)) {
				Map<String, String> updates = new HashMap<String, String>();
				String recordKey = recordDecisions.get("unico");
				String entityName = dialogStepSchema.getEntity();
				updates = updateDecisions;
				for (String key : updates.keySet()) {
					boolean ok = true;
					if (!key.equals("unico")) {
						ok = entityService.setValue(entityService.findByKeys(entityName, recordKey, key),
								updates.get(key));
						if (!ok) {
							executeCustomSimpleMessage(botUser, "Algo deu errado", null);
						}
					}

				}
				executeCustomSimpleMessage(botUser, "Registro atualizado", inlineKeyboard);
				dialog.setDecisions(decisionService.cleanDecisions(dialog.getDecisions(), "record:"));
				executeAgain = true;

			}

			// Excluindo um record
			if (dialogStepSchema.getStepType().equals(StepType.DELETE)) {

				String entityName = dialogStepSchema.getEntity();
				if (!recordDecisions.isEmpty()) {
					String recordKey = recordDecisions.get("unico");
					entityService.deleteRecord(entityName, recordKey);
				} else {
					entityService.resetEntity(entityName);
				}
				executeCustomSimpleMessage(botUser, "Registro deletado", inlineKeyboard);
				dialog.setDecisions(decisionService.cleanDecisions(dialog.getDecisions(), "record:"));
				executeAgain = true;
			}

			// Delay
			if (dialogStepSchema.getStepType().equals(StepType.DELAY)) {
				int seconds = 0;
				if (dialogStepSchema.getParameters().containsKey("timeInSeconds")) {
					seconds = Integer.parseInt(dialogStepSchema.getParameters().get("timeInSeconds"));
				} else {
					seconds = 2;
				}

				try {
					Thread.sleep(seconds * 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				executeAgain = true;
			}

			// Requisição de boolean
			if (dialogStepSchema.getStepType().equals(StepType.REQUESTBOOLEAN)) {
				if (!dialog.getDialogStatus().equals(DialogStatus.AGUARDANDO)) {

					List<String> options = new LinkedList<String>();
					options.add("Sim");
					options.add("Não");
					inlineKeyboard = keyboardService.getSimpleInlineKeyboard(options);

					executeRequestInlineOption(botUser, dialogStepSchema, inlineKeyboard);
					dialog.setDialogStatus(DialogStatus.AGUARDANDO);
					executeAgain = false;
				} else {
					if (!pular) {
						if (callBackData != null) {
							if (callBackData.equals("Sim")) {
								dialog.addDecision(dialogStepSchema.getKey(), "true");
							}
							if (callBackData.equals("Não")) {
								dialog.addDecision(dialogStepSchema.getKey(), "false");
							}
							dialog.setDialogStatus(DialogStatus.INICIO);
							executeAgain = true;
						} else {
							executeRequestInlineOption(botUser, dialogStepSchema, inlineKeyboard);
							executeAgain = false;
						}
					} else {
						dialog.setDialogStatus(DialogStatus.INICIO);
						executeAgain = true;
					}

				}
			}

			// Atualizando a estrutura
			if (dialogStepSchema.getStepType().equals(StepType.REFRESH_STRUCTURE)) {
				startService.executeSynchronize();
				return;
			}

			// Desabilitando um dialog
			if (dialogStepSchema.getStepType().equals(StepType.DISABLE_DIALOG)) {
				String dialogName = dialogDecisions.get("unico");
				dialogName = "|D|" + dialogName + "|";
				dialogSchemaService.disableDialog(dialogName);
				executeAgain = true;
			}

			// Habilitando um dialog
			if (dialogStepSchema.getStepType().equals(StepType.ENABLE_DIALOG)) {
				String dialogName = dialogDecisions.get("unico");
				dialogName = "|D|" + dialogName + "|";
				dialogSchemaService.enableDialog(dialogName);
				executeAgain = true;
			}

			// Mostrando todos os dados de uma lista de entities
			if (dialogStepSchema.getStepType().equals(StepType.SHOWALLRECORDFIELDS)) {
				StringBuilder resposta = new StringBuilder();
				for (String entityName : dialogStepSchema.getInlineKeyboard()) {
					List<Record> records = entityService.findByEntityName(entityName);
					executeCustomSimpleMessage(botUser, resposta.toString(), null);
					resposta = new StringBuilder();
					if (records.size() != 0) {
						resposta.append("<b>" + entityName + "</b>");
						executeCustomSimpleMessage(botUser, resposta.toString(), null);
						resposta = new StringBuilder();
						List<String> keys = keyService.parseStringKey(records);
						for (String key : keys) {
							List<Record> record = entityService.findByKeys(entityName, key);
							resposta.append(key.replace(" null", "") + "\n");
							if (record.size() != 1) {
								for (Record recordField : record) {
									resposta.append("<b>" + recordField.getFieldName() + "</b>");
									resposta.append(": ");
									resposta.append(recordField.getValue().replace(" null", ""));
									resposta.append("\n");
								}
							}
							executeCustomSimpleMessage(botUser, resposta.toString(), null);
							resposta = new StringBuilder();
						}
					}
					// resposta.append("\n");
				}
				executeCustomSimpleMessage(botUser, resposta.toString(), null);
				executeAgain = true;
				// dialog.setDialogStatus(DialogStatus.FIM);//<--Futuramente isto pode mudar...
			}
			
			
			// Delete Self
			if (dialogStepSchema.getStepType().equals(StepType.DELETESELF)) {
				List<RecordSchema> schemas = recordSchemaRepo.findByTypeAndIsKey(RecordType.SELF, true);
				String userKey = botUser.getId() + "-" + botUser.getFirstName() + " " + botUser.getLastName();
				for(RecordSchema schema : schemas) {
					entityService.deleteRecord(schema.getEntityName(), userKey);
				}
				executeAgain = true;
			}

			// ------------------------------------------------------

			// Conferindo fim do diálogo
			if (dialog.getDialogStatus().equals(DialogStatus.FIM)) {
				dialogRepo.delete(dialogRepo.findById(botUser.getId()));
			}

			// Avanço do passo
			if (executeAgain) {
				dialog.setCurrentStep(dialog.getCurrentStep() + 1);
			}

			// Oficialização das mudanças do diálogo
			if (!dialog.getDialogStatus().equals(DialogStatus.FIM)) {
				if (dialogSchema.getSteps().get(dialog.getCurrentStep()) == null) {
					dialogRepo.delete(dialogRepo.findById(botUser.getId()));
					createDialog(user, dialogSchemaService.findDialogSchemabyNomeSchema("|D|Menu|"));
					executeAgain = true;
				} else {
					dialogRepo.save(dialog);
					dialogStepSchema = dialog.getDialogSchema().getSteps().get(dialog.getCurrentStep());
					keyboard = dialogStepSchemaService.getKeyboard(dialogStepSchema);
					inlineKeyboard = dialogStepSchemaService.getInlineKeyboard(dialogStepSchema);
				}
			}

		} while (executeAgain);

	}

	@Override
	public void resetAllDialogs() {
		dialogRepo.deleteAll();
	}

	private void executeSchemaSimpleMessage(BotUser botUser, DialogStepSchema dialogStepSchema, Keyboard keyboard) {
		Bot.sendMessage(botUser.getId().toString(), dialogStepSchema.getBotMessage(), keyboard);
	}

	private void executeCustomSimpleMessage(BotUser botUser, String text, Keyboard keyboard) {
		Bot.sendMessage(botUser.getId().toString(), text, keyboard);
	}

	private void executeRequestContact(BotUser botUser, DialogStepSchema dialogStepSchema) {
		Bot.requestContact(botUser.getId().toString(), dialogStepSchema.getBotMessage());
	}

	private void executeRequestInlineOption(BotUser botUser, DialogStepSchema dialogStepSchema,
			InlineKeyboardMarkup keyboard) {
		Bot.requestInlineOption(botUser.getId().toString(), dialogStepSchema.getBotMessage(), keyboard);
	}

}
