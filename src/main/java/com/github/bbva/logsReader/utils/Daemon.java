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
	
	public void task() throws InterruptedException{
		log.info("Executing DAEMON.");
		filesUtils.loaderFiles();
	}
}
