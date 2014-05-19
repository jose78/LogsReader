package com.github.bbva.logsReader.dto;

import com.github.bbva.logsReader.annt.Column;
import com.github.bbva.logsReader.annt.DTO;

@DTO
public class ErrorNowDTO {

	@Column(name="application")
	private String  application ;
	@Column(name="label")
	private String  label  ;
	
	@Column(name="NUM_ERR" )
	private Integer numberOfError;
	
	
	public ErrorNowDTO() {
	}
	
	
	
	public ErrorNowDTO(String application, String label, Integer numberOfError) {
		super();
		this.application = application;
		this.label = label;
		this.numberOfError = numberOfError;
	}



	public String getApplication() {
		return application;
	}
	public String getLabel() {
		return label;
	}
	public Integer getNumberOfError() {
		return numberOfError;
	}


	@Override
	public String toString() {
		return String.format(
				"ErrorNowDTO [application=%s, label=%s, numberOfError=%s]",
				application, label, numberOfError);
	}
	
	
	
}
