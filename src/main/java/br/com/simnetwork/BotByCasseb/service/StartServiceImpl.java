package br.com.simnetwork.BotByCasseb.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.simnetwork.BotByCasseb.ddbs.service.RecordService;
import br.com.simnetwork.BotByCasseb.ddialog.service.DialogSchemaService;
import br.com.simnetwork.BotByCasseb.ddialog.service.DialogService;

@Service("startService")
public class StartServiceImpl implements StartService {

	@Autowired
	DialogSchemaService dialogSchemaService;
	@Autowired
	DialogService dialogService;
	@Autowired
	RecordService entityService;
	@Autowired
	EasyWayService easyWayService;

	@PostConstruct
	public void executeSynchronize() {
		dialogSchemaService.synchronizeAllDialogSchema();
		dialogService.resetAllDialogs();
		easyWayService.loadConfFile();
		entityService.synchronizeStaticEntities();
	}
	
	/*
	@Scheduled(cron = "0 44 20 * * ?")
	public void enableRotaA() {
		dialogSchemaService.enableDialog("|D|Rota A|");
	}
	
	@Scheduled(cron = "0 42 20 * * ?")
	public void disableRotaA() {
		dialogSchemaService.disableDialog("|D|Rota A|");
	}
	*/

}
