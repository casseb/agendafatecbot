package br.com.simnetwork.BotByCasseb.ddbs.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import br.com.simnetwork.BotByCasseb.ddbs.service.RecordService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class SimpleList{
	
	@Id
	private String listName;
	private String entityName;
	private String fieldName;
	private String fieldToShow;
	private String value;
	
}
