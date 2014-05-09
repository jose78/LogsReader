/**
 * 
 */
package com.github.bbva.logsReader.utils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *  @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero - Neoris</a> 
 * 
 */
public class Daemon {

	private static Logger log = Logger.getLogger(Daemon.class);
	
	@Autowired
	FilesUtils filesUtils;
	
	private boolean semaphore = true;
	
	public void task() throws InterruptedException{
		if(semaphore && (semaphore = inExecution())){}
	}
	
	synchronized private boolean inExecution(){
		semaphore = false;
		log.info("Executing DAEMON.");
		filesUtils.loaderFiles();
		log.info("END DAEMON.");
		return true;		
	}
}
