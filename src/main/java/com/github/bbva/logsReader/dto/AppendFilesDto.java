/**
 * 
 */
package com.github.bbva.logsReader.dto;

import java.io.File;

import com.github.bbva.logsReader.annt.DTO;

/**
 * @author jose
 * 
 */
@DTO
public class AppendFilesDto {

	private File[] files;
	private String environment;

	public AppendFilesDto(File[] files, String environment) {
		this.files = files;
		this.environment = environment;
	}

	public String getEnvironment() {
		return environment;
	}

	public File[] getFiles() {
		return files;
	}
}
