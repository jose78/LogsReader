package com.github.bbva.logsReader.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.bbva.logsReader.db.DBConnection;
import com.github.bbva.logsReader.entity.FileLoadedEntity;
import com.github.bbva.logsReader.entity.LogDTO;

/**
 * 
 * @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero -
 *         Neoris</a>
 * 
 */
public class FilesUtils {

	private static Logger log = Logger.getLogger(FilesUtils.class);

	@Value("${com.bbva-logReader.pathfiles.ten}")
	private String pathFilesTenMinut;

	@Value("${com.bbva-logReader.pathfiles.one}")
	private String pathFilesOneMinut;

	@Autowired
	private DBConnection connection;

	private Set<String> containerNameFilesLoaded;

	/**
	 * This method will be loaded automatcly when start the application-
	 */
	@SuppressWarnings("unused")
	private void init() {
		containerNameFilesLoaded = new TreeSet<String>();
		containerNameFilesLoaded.addAll(connection.read(String.class,
				"SELECT NAME FROM CONTAINER_LOGS_DATA.FILES_LOADED"));
		log.info(String.format("Cargados inicialmente %s archivos",
				containerNameFilesLoaded.size()));
	}

	public synchronized void loaderFiles() {
		loadFiles(pathFilesOneMinut, pathFilesTenMinut);

	}

	private void loadFiles(String... pathnames) {

		
		String fileName =null;
		
		
		/*
		 * 0º read all files of each directory and validate us that then can be
		 * loaded.
		 */
		for (String pathname : pathnames) {

			log.info(String.format("Scaning the directory %s", pathname));
			File directory = new File(pathname);
			File files[] = directory.listFiles();
			for (File file : files) {
				try {
					if (file.isFile()
							&& !containerNameFilesLoaded.contains(file
									.getAbsolutePath())) {
						fileName = file.getName();

						/*
						 * 1º save all new files loaded.
						 */
						connection.insert(new FileLoadedEntity(file
								.getAbsolutePath()));

						/*
						 * 2º retrieve and save in db all logDto.
						 */
						List<LogDTO> list = openFile(file);
						if (list == null)
							continue;

						List<LogDTO> lstError = new ArrayList<LogDTO>();
						for (LogDTO logDTO : list) {
							try {
								connection.insert(logDTO);
							} catch (Exception e) {
								lstError.add(logDTO);
							}
						}
						/*
						 * 3º save the file in the container of files readed
						 */
						containerNameFilesLoaded.add(fileName);

						log.info(String
								.format("Archivo %s cargado con %s registros ok y %s erroneos",
										file.getName(), list.size(),
										lstError.size()));
					}

				} catch (Exception e) {
					
					containerNameFilesLoaded.remove(fileName);
					connection.delete(new FileLoadedEntity(file.getAbsolutePath()));
					connection.delete(new LogDTO().setFileName(file.getAbsolutePath()));
					log.error(" Clean Error from DB -> OK.", e);
					e.printStackTrace();
				}
			}

		}
	}

	private List<LogDTO> openFile(File file) throws IOException {

		BufferedReader br = null;
		try {
			List<LogDTO> containerData = new ArrayList<LogDTO>();
			br = new BufferedReader(new FileReader(file));
			/*
			 * Retrieve the Head.
			 */
			String line = br.readLine();
			String[] head = line.split(",");

			/*
			 * Map the data.
			 */
			while ((line = br.readLine()) != null) {
				// sb.append(line);
				String[] source = line.split(",");
				containerData
						.add(loadData(head, source, file.getAbsolutePath()));
			}
			return containerData;
		} catch (Exception e) {
			log.error(String.format("Archivo %s mal formateado",
					file.getAbsolutePath()));
			return null;
		} finally {
			br.close();

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
	private LogDTO loadData(String[] head, String[] source, String pathFile) {
		if (source.length != head.length)
			throw new LogsReaderException("head:[%s] source:[%s]", head.length,
					source.length);
		Map<String, String> data = new TreeMap<String, String>();
		for (int index = 0; index < source.length; index++) {
			data.put(head[index], source[index]);
		}
		data.put("NAME_file", pathFile);
		return new LogDTO(data);
	}
}
