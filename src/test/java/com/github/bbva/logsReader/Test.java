/**
 * 
 */
package com.github.bbva.logsReader;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.bbva.logsReader.db.DBConnection;
import com.github.bbva.logsReader.db.RepositoryCollections;
import com.github.bbva.logsReader.dto.InfoServicesDTO;
import com.github.bbva.logsReader.entity.FileLoadedEntity;
import com.github.bbva.logsReader.utils.Daemon;
import com.github.bbva.logsReader.utils.FilesUtils;
import com.google.gson.Gson;

/**
 * @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero -
 *         Neoris</a>
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class Test {

	private static Logger log = Logger.getLogger(Test.class);

	@Autowired
	RepositoryCollections repository;
	@Autowired
	FilesUtils filesUtils;
	@Autowired
	Daemon daemon;
	@Autowired
	DBConnection connection;

	@org.junit.Test
	public void loadFiles() throws Exception {
		try {
			filesUtils.loaderFiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@org.junit.Test
	public void testConnectionDBRead() throws Exception {
		connection.delete(new FileLoadedEntity("perrea.txt"));
		connection.insert(new FileLoadedEntity("perrea.txt"));

		log.info(connection.read(String.class,
				"SELECT NAME FROM CONTAINER_LOGS_DATA.FILES_LOADED"));

	}

	@org.junit.Test
	public void testDaemonLoader() throws Exception {
		filesUtils.loaderFiles();
	}

	@org.junit.Test
	public void tesStandardDeviation() throws Exception {

		String inicioFechaIntervalo = null; // "01/01/2013";
		String finFechaIntervalo = null; // "22/4/2014";
		log.info(new Gson().toJson(repository.getStandardDeviation(
				inicioFechaIntervalo, finFechaIntervalo)));
	}

	@org.junit.Test
	public void testInfoService() throws Exception {
		List<InfoServicesDTO> lsr = repository.getListService("Login SLOD");
		for (InfoServicesDTO infoServicesDTO : lsr) {
			log.info("InfoService-> " + infoServicesDTO);
		}

	}

	@org.junit.Test
	public void testListCalled() throws Exception {
		log.info(repository.getListaByGroupTimeElapsed("1", "Login ENPP BMES","30"));
	}
}
