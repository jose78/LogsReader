package com.github.bbva.logsReader.dto;

import com.github.bbva.logsReader.annt.Column;
import com.github.bbva.logsReader.annt.DTO;

@DTO
public class DonutDTO {

	@Column(name="responsecode")
	private String  responsecode  ;
	
	@Column(name="NUMBER" )
	private Integer number;
	
	
	public DonutDTO() {
	}
	
	
	
	
	public Integer getNumber() {
		return number;
	}
	
	public String getResponsecode() {
		return responsecode;
	}
	
}
