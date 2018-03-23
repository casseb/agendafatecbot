package br.com.simnetwork.BotByCasseb.ddbs.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.logging.DelegatingBasicLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.simnetwork.BotByCasseb.ddbs.model.AdvancedList;
import br.com.simnetwork.BotByCasseb.ddbs.model.AdvancedListOption;
import br.com.simnetwork.BotByCasseb.ddbs.model.Record;
import br.com.simnetwork.BotByCasseb.ddbs.model.RecordSchema;
import br.com.simnetwork.BotByCasseb.ddbs.model.SimpleList;
import br.com.simnetwork.BotByCasseb.ddbs.repository.RecordSchemaRepository;
import br.com.simnetwork.BotByCasseb.service.EasyWayService;

@Service("listService")
public class ListServiceImpl implements ListService {

	@Autowired
	private RecordService entityService;
	@Autowired
	private EasyWayService easyWayService;
	@Autowired
	private RecordSchemaRepository recordSchemaRepo;

	@Override
	public List<String> getSimpleList(SimpleList simpleList, Map<String, String> dialogDecisions) {

		List<String> keys = new LinkedList<>();

		// Sem definir campo para busca será retornado todos os registros
		if (simpleList.getFieldName() == null) {
			keys = entityService.findByFields(simpleList.getEntityName(), null);
			// Montagem da query para busca
		} else {
			Map<String, String> decisions = new HashMap<String, String>();
			String value = simpleList.getValue();
			if (dialogDecisions != null) {
				for (String dialogDecision : dialogDecisions.keySet()) {
					if (value.equals(dialogDecision)) {
						value = dialogDecisions.get(dialogDecision);
					}
				}
			}
			if (simpleList.getFieldName().equals("global:key")) {
				keys.add(entityService.findByKeys(simpleList.getEntityName(), value).get(0).getKey());
			} else {
				decisions.put("query:" + simpleList.getFieldName(), value);
				keys = entityService.findByFields(simpleList.getEntityName(), decisions);
			}
		}

		// Adaptação da lista respeitando o campo escolhido para apresentação
		if (simpleList.getFieldToShow() == null) {
			return keys;
		} else {
			return getValueOfCustomFieldName(simpleList.getEntityName(), keys, simpleList.getFieldToShow());
		}
	}

	@Override
	public List<String> getAdvancedList(AdvancedList advancedList, Map<String, String> dialogDecisions) {
		List<String> result = new LinkedList<String>();

		for (String key : advancedList.getItensByAdvancedListOption(AdvancedListOption.ADD).keySet()) {
			SimpleList simpleList = null;
			if (simpleList == null) {
				simpleList = easyWayService.createSimpleList(key);
			}
			result.addAll(getSimpleList(simpleList, dialogDecisions));
		}
		result = removeRepeatedValue(result);

		for (String key : advancedList.getItensByAdvancedListOption(AdvancedListOption.REMOVE).keySet()) {
			SimpleList simpleList = null;
			if (simpleList == null) {
				simpleList = easyWayService.createSimpleList(key);
			}
			result.removeAll(getSimpleList(simpleList, dialogDecisions));
		}

		return removeRepeatedValue(result);
	}

	private List<String> removeRepeatedValue(List<String> strings) {
		return new LinkedList<String>(new HashSet<String>(strings));
	}

	private List<String> getValueOfCustomFieldName(String entityName, List<String> keys, String fieldToShow) {
		List<String> result = new LinkedList<String>();

		List<Record> records = entityService.findByKeys(entityName, keys);
		for (Record record : records) {
			if (record.getFieldName().equals(fieldToShow)) {
				result.add(record.getValue());
			}
		}

		return result;
	}

	public List<Record> getSimpleList(SimpleList simpleList, ExternalQueryOption option) {

		switch (option) {
		case ALL:
			return entityService.findByEntityName(simpleList.getEntityName());
		case WITHVALUE:
			return entityService.findByEntityNameAndFieldNameAndValue(simpleList.getEntityName(),
					simpleList.getFieldName(), simpleList.getValue());
		default:
			return null;
		}

	}

	

}
