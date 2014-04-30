package com.github.bbva.logsReader.rest;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.bbva.logsReader.db.DBConnection;
import com.github.bbva.logsReader.db.RepositoryCollections;


/**
 * 
 *  @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero - Neoris</a> 
 *
 */
@Controller
public class LogReaderControler {

	
	@Autowired private DBConnection connection;
	@Autowired private RepositoryCollections repository;
	
	@RequestMapping("/Datoo")
	public String redirect(){
		System.out.println("\n\n\nDATO\n\n\n");
		return "index.html";
	}
	
	@RequestMapping(value ={"/StandardDeviation"},method={RequestMethod.GET}  , produces = "text/plain" )
	public @ResponseBody String getStandardDeviation(){
		Map<String, String> map = repository.getStandardDeviation(null, null);
		
		StringBuilder sb= new StringBuilder();
		for (Entry<String, String> item : map.entrySet()) {
			sb.append(item.getKey()).append("	").append(item.getValue()).append("\n");
		}
		return sb.toString();
	}
}
