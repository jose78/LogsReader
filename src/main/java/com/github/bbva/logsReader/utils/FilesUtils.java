package com.github.bbva.logsReader.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.bbva.logsReader.db.DBConnection;
import com.github.bbva.logsReader.db.RepositoryCollections;
import com.github.bbva.logsReader.dto.AppendFilesDto;
import com.github.bbva.logsReader.entity.FileEnvironmentEntity;
import com.github.bbva.logsReader.entity.LogEntity;

/**
 * 
 * @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero -
 *         Neoris</a>
 * 
 */
public class FilesUtils {

	private static Logger log = Logger.getLogger(FilesUtils.class);

	@Value("${com.bbva-logReader.csv.heds}")
	private String[] head;

	// @Value("${com.bbva-logReader.pathfiles.ten}")
	// private String pathFilesTenMinut;
	//
	// @Value("${com.bbva-logReader.pathfiles.one}")
	// private String pathFilesOneMinut;

	private java.util.Queue<AppendFilesDto> queue = null;

	@Autowired
	private RepositoryCollections repository;
	@Autowired
	private DBConnection connection;

	/**
	 * This method will be loaded automatcly when start the application-
	 */
	@SuppressWarnings("unused")
	private void init() {
		queue = new PriorityQueue<AppendFilesDto>();
		// containerNameFilesLoaded = new TreeSet<String>();
		// containerNameFilesLoaded.addAll(connection.read(String.class,
		// "SELECT NAME FROM CONTAINER_LOGS_DATA.FILES_LOADED"));
		// log.info(String.format("Cargados inicialmente %s archivos",
		// containerNameFilesLoaded.size()));
	}

	public void loadFiles(AppendFilesDto appendFilesDto) {

		queue.add(appendFilesDto);

		loadFilesFromQueue();

	}

	private synchronized void loadFilesFromQueue() {

		while (!queue.isEmpty()) {

			/*
			 * Rtrieve and remove the head of the queue
			 */
			AppendFilesDto appendFilesDto = queue.poll();
			for (File file : appendFilesDto.getFiles()) {
				load(file, appendFilesDto.getEnvironment());
			}
		}
	}

	private void load(File file, String environment) {

		String fileName = null;
		FileEnvironmentEntity fileEnvironmentEntity = repository
				.getFileEnvironmentEntity(file, environment);

		Integer numberOfLine = 1;
		Integer cont = 0;
		Integer contError = 0;
		BufferedReader br = null;

		try {
			if (fileEnvironmentEntity != null)
				numberOfLine = fileEnvironmentEntity.getNumberOfLine();

			br = new BufferedReader(new FileReader(file));
			String line = null;
			
			while ((line = br.readLine()) != null) {

				if (cont >= numberOfLine) {
					try {
						LogEntity logEntity = loadData(head, line.split(","),
								file.getName(), environment);
						connection.insert(logEntity);
					} catch (Exception e) {
						contError++;
						log.error(String
								.format("file:[%s] environment:[%s] Nº line:[%s] Error:[%s] ",
										file.getName(), environment, cont,
										line, e.getMessage()));
					}
				}
				cont++;

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			log.info(String.format(
					"In the file %s:Rows append %s OK and %s lines with ERROR",
					file.getName(), ((cont - numberOfLine) - contError),
					contError));
			
			if (fileEnvironmentEntity != null) {
				fileEnvironmentEntity.setNumberOfLine(cont);
				connection.update(fileEnvironmentEntity);
			} else {
				connection.insert(new FileEnvironmentEntity(null, file
						.getName(), environment, cont));
			}

			
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Map each line in a <code>LogDTO</code> object.
	 * 
	 * @param head
	 *            array with the name of the column
	 * @param source
	 *            array wth the value.
	 * @return Object.
	 */
	private LogEntity loadData(String[] head, String[] source, String pathFile,
			String environment) {
		if (source.length != head.length)
			throw new LogsReaderException("head:[%s] source:[%s]", head.length,
					source.length);
		Map<String, String> data = new TreeMap<String, String>();
		for (int index = 0; index < source.length; index++) {
			data.put(head[index], source[index]);
		}
		data.put("application", pathFile.contains("enpp2") ? "BUZZ"
				: "NXT/WALLET");
		data.put("environment", environment);
		return new LogEntity(data);
	}
}
