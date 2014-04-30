package com.github.bbva.logsReader.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.bbva.logsReader.db.DBConnection;


/**
 * 
 *  @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero - Neoris</a> 
 *
 */
@Controller
public class LogReaderControler {

	
	@Autowired private DBConnection connection;
	
	@RequestMapping("/Datoo")
	public String redirect(){
		System.out.println("\n\n\nDATO\n\n\n");
		return "test";
	}
	
	
	@ExceptionHandler(Exception.class)
	public void handleAllException(Exception ex) {
 
		connection.rollbak();
 
	}
	
	
}
