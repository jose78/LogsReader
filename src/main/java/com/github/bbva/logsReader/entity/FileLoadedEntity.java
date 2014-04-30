package com.github.bbva.logsReader.entity;

import com.github.bbva.logsReader.annt.Column;
import com.github.bbva.logsReader.annt.Id;
import com.github.bbva.logsReader.annt.Table;

/**
 * 
 *  @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero - Neoris</a> 
 *
 */
@Table(name = "FILES_LOADED", schema = "CONTAINER_LOGS_DATA")
public class FileLoadedEntity {

	@Column(name = "ID")
	@Id(autoincrement = "id_file_loaded")
	private Integer id;


	@Column(name = "name")
	private String name;

	public FileLoadedEntity(String name) {
		setName(name);
	}

	public FileLoadedEntity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return String.format("FileLoadedEntity [id=%s, name=%s]", id, name);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public FileLoadedEntity setId(int id) {
		this.id = id;
		return this;
	}

	public FileLoadedEntity setName(String name) {
		this.name = name;
		return this;
	}

}