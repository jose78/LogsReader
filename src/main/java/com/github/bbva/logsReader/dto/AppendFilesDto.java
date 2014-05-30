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
public class AppendFilesDto implements Comparable<AppendFilesDto>{

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

	@Override
	public int compareTo(AppendFilesDto o) {
		
		int res= files.length - o.getFiles().length;
		for (int index= 0; res == 0 && index <files.length; index++) {
			res = getFiles()[index].getName().compareTo(o.getFiles()[index].getName());
		}
		
		if(res != 0)
			return res;
		
		return  getEnvironment().compareTo(o.getEnvironment());
	}
}
