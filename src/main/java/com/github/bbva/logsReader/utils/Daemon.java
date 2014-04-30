/**
 * 
 */
package com.github.bbva.logsReader.utils;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.github.bbva.logsReader.db.DBConnection;
import com.github.bbva.logsReader.entity.LogDTO;

/**
 *  @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero - Neoris</a> 
 * 
 */
public class Daemon {

	private static Logger log = Logger.getLogger(Daemon.class);
	@Autowired
	FilesUtils filesUtils;
	@Autowired
	DBConnection connection;
	
	/**
	 * Number of <B>second</b> used to sleep the thread.
	 * <b>NOTE:</b> By defaylt is 5 secods.
 	 */
	@Value(value = "${com.bbva-logReader.daemon.time.sleep}")
	public int timeSec;
	
	
	public int getTimeSec() {
		return timeSec;
	}
	
	public void setTimeSec(int timeSec) {
		this.timeSec = timeSec;
	}
	public void init() throws InterruptedException{
		log.info("Starting DAEMON 1s.");
		startDaemon();
		log.info("Starting DAEMON  2s.");
	}
	
	@Async
	public void startDaemon() throws InterruptedException{
		log.info("Starting DAEMON.");
		while (true) {
			/*
			 * Sleep the thread $timeSec secods and execute the load of new Files.
			 */
			Thread.sleep(timeSec*1000);
			filesUtils.loaderFiles();
		}
	}
	

	@ExceptionHandler(LogsReaderException.class)
	public void handleAllException(LogsReaderException ex) {
		connection.rollbak();
		log.error(" rollback OK.", ex);
	}

}
