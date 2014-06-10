package com.github.bbva.logsReader.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.bbva.logsReader.entity.LogEntity;

/**
 * 
 * @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero -
 *         Neoris</a>
 * 
 */
@Component
public class FilesUtils {

	private static Logger log = Logger.getLogger(FilesUtils.class);

	@Value("${logReader.csv.heads}")
	private String[] head ;

	/**
	 * Map a line in a <code>LogDTO</code> object.
	 * 
	 * @param source
	 * @param application
	 * @param environment
	 * @return
	 */
	public LogEntity loadData(String[] source, String application,
			String environment) {
		if (source.length != head.length)
			throw new LogsReaderException("head:[%s] source:[%s]", head.length,
					source.length);
		Map<String, String> data = new TreeMap<String, String>();
		for (int index = 0; index < source.length; index++) {
			data.put(head[index], source[index]);
		}
		data.put("application", application);
		data.put("environment", environment);
		return new LogEntity(data);
	}

	private class BackwardsFileInputStream extends InputStream {
		public BackwardsFileInputStream(File file) throws IOException {
			assert (file != null) && file.exists() && file.isFile()
					&& file.canRead();

			raf = new RandomAccessFile(file, "r");
			currentPositionInFile = raf.length();
			currentPositionInBuffer = 0;
		}

		public int read() throws IOException {
			if (currentPositionInFile <= 0)
				return -1;
			if (--currentPositionInBuffer < 0) {
				currentPositionInBuffer = buffer.length;
				long startOfBlock = currentPositionInFile - buffer.length;
				if (startOfBlock < 0) {
					currentPositionInBuffer = buffer.length
							+ (int) startOfBlock;
					startOfBlock = 0;
				}
				raf.seek(startOfBlock);
				raf.readFully(buffer, 0, currentPositionInBuffer);
				return read();
			}
			currentPositionInFile--;
			return buffer[currentPositionInBuffer];
		}

		public void close() throws IOException {
			raf.close();
		}

		private final byte[] buffer = new byte[4096];
		private final RandomAccessFile raf;
		private long currentPositionInFile;
		private int currentPositionInBuffer;
	}

	public List<String> head(File file, String encoding, int numberOfLinesToRead)
			throws IOException {
		assert (file != null) && file.exists() && file.isFile()
				&& file.canRead();
		assert numberOfLinesToRead > 0;
		assert encoding != null;

		LinkedList<String> lines = new LinkedList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), encoding));
		for (String line = null; (numberOfLinesToRead-- > 0)
				&& (line = reader.readLine()) != null;) {
			lines.addLast(line);
		}
		reader.close();
		return lines;
	}

	public List<String> tail(File file, int numberOfLinesToRead)
			throws IOException {
		return tail(file, "utf-8", numberOfLinesToRead);
	}

	public List<String> tail(File file, String encoding, int numberOfLinesToRead)
			throws IOException {
		assert (file != null) && file.exists() && file.isFile()
				&& file.canRead();
		assert numberOfLinesToRead > 0;
		assert (encoding != null) && encoding.matches("(?i)(utf8).*");

		LinkedList<String> lines = new LinkedList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new BackwardsFileInputStream(file), encoding));
		for (String line = null; (numberOfLinesToRead-- > 0)
				&& (line = reader.readLine()) != null;) {
			// Reverse the order of the characters in the string
			char[] chars = line.toCharArray();
			for (int j = 0, k = chars.length - 1; j < k; j++, k--) {
				char temp = chars[j];
				chars[j] = chars[k];
				chars[k] = temp;
			}
			lines.addFirst(new String(chars));
		}
		reader.close();
		return lines;
	}

	public void clear() {
		try {
			new File("file.tmp").delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
