/**
 * 
 */
package com.github.bbva.logsReader.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.bbva.logsReader.dto.AppendFilesDto;

/**
 * @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero -
 *         Neoris</a>
 * 
 */
public class Daemon {

	private static Logger log = Logger.getLogger(Daemon.class);
	
	private Boolean flagStart = true;
	
	@Value("${com.bbva-logReader.pathfiles.one}")
	private String path ;
	private String environment = "one";

	@Autowired
	FilesUtils filesUtils;

	private Boolean semaphore = true;

	public void task() throws InterruptedException {
		if (flagStart && semaphore && (semaphore = inExecution())) {
		}
	}

	
	protected void destroy() throws Throwable {
		// TODO Auto-generated method stub
		log.info("finalize DAEMON.");
		super.finalize();
	}
	synchronized private boolean inExecution() {
		semaphore = false;
		log.info("Executing DAEMON.");

		/*
		 * retrieve the currents files
		 */
		File directory = new File(path);
		File[] files= directory.listFiles(new FileFilter());

		filesUtils.loadFiles(new AppendFilesDto(files, environment));
		
		log.info("END DAEMON.");
		return true;
	}
	
	public Boolean isStartUpDaemon(){
		return flagStart;
	}

	public void setStartUpDaemon(Boolean flagStart) {
		this.flagStart = flagStart;
	}
	private class FileFilter implements FilenameFilter {

		String currentDate = null;

		public FileFilter() {
			SimpleDateFormat sfd = new SimpleDateFormat("yyyyMMdd");
			currentDate = sfd.format(new Date(System.currentTimeMillis()));
		}

		@Override
		public boolean accept(File dir, String name) {
			// TODO Auto-generated method stub
			return name.contains(currentDate) && !name.contains("xml");
		}
	}
	
}
