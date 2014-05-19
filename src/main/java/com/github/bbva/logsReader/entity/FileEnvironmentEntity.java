package com.github.bbva.logsReader.entity;

import com.github.bbva.logsReader.annt.Column;
import com.github.bbva.logsReader.annt.Entity;
import com.github.bbva.logsReader.annt.Table;

@Entity
@Table(name = "CONTAINER_LOGS_DATA", schema = "FILE_ENVIRONMENT")
public class FileEnvironmentEntity {

	@Column(name = "ID")
	private Integer id;
	@Column(name = "NAMEFILE")
	private String nameFile;
	@Column(name = "ENVIRONMENT")
	private String environment;

	@Column(name = "numberOfLine")
	private Integer numberOfLine;

	public FileEnvironmentEntity(Object object, String string, String environment, Integer numberOfLine) {
		
		this.environment = environment;
		this.numberOfLine = numberOfLine;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the nameFile
	 */
	public String getNameFile() {
		return nameFile;
	}

	/**
	 * @param nameFile
	 *            the nameFile to set
	 */
	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}

	/**
	 * @return the environment
	 */
	public String getEnvironment() {
		return environment;
	}

	/**
	 * @param environment
	 *            the environment to set
	 */
	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	/**
	 * @return the numberOfLine
	 */
	public Integer getNumberOfLine() {
		return numberOfLine;
	}

	/**
	 * @param numberOfLine
	 *            the numberOfLine to set
	 */
	public void setNumberOfLine(Integer numberOfLine) {
		this.numberOfLine = numberOfLine;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("FileEnvironmentEntity [id=%s, nameFile=%s, environment=%s, numberOfLine=%s]",
						id, nameFile, environment, numberOfLine);
	}

}
