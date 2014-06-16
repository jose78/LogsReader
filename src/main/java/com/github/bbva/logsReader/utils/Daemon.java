/**
 * 
 */
package com.github.bbva.logsReader.utils;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.bbva.logsReader.db.DBConnection;
import com.github.bbva.logsReader.entity.LogEntity;
import com.github.bbva.logsReader.utils.enums.EnumSource;

/**
 * @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero -
 *         Neoris</a>
 * 
 */
public class Daemon {

	private static Logger log = Logger.getLogger(Daemon.class);

	private Boolean flagStart = true;

	@Autowired
	DBConnection connection;

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
		
		for (EnumSource source : EnumSource.values()) {

			if (source.isExecutable()) {
				try {
					List<String> lstLinesBuzz = filesUtils.tail(
							source.getFileBuzz(), source.getNumLines());
					saveData(lstLinesBuzz,EnumSource.APP_BUZZ, source.name());
				} catch (Exception e) {
					e.printStackTrace();
				}
				

				try {
					List<String> lstLinesNxt = filesUtils.tail(
							source.getFileNxt(), source.getNumLines());
					saveData(lstLinesNxt, EnumSource.APP_NXT, source.name());
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.info("Delete tmp file BUZZ:" + source.clearBuzz());
				log.info("Delete tmp file NXT/WALLET:" + source.clearNxt());
				
			}
		}

		log.info("END DAEMON.");
		return true;
	}

	String lineBuzzOne= "";
	private void saveData(List<String> lstLines, String application, String env) {
		for (String line : lstLines) {
			try {
				if (line.length() > 0) {
					
					LogEntity entity = filesUtils.loadData(line.split(","),	application, env);
					connection.insert(entity);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Boolean isStartUpDaemon() {
		return flagStart;
	}

	public void setStartUpDaemon(Boolean flagStart) {
		this.flagStart = flagStart;
	}
}
