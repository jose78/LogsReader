package com.github.bbva.logsReader.dto;

import java.util.List;

import com.github.bbva.logsReader.annt.DTO;

/**
 * 
 * @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero -
 *         Neoris</a>
 * 
 */
@DTO
public class ResultDTO {

	private List<TuplaDto> result;

	private String axisX;
	private String axisY;

	public ResultDTO(String axisX, String axisY) {
		this.axisX = axisX;
		this.axisY = axisY;
	}

	public ResultDTO  setResult(List<TuplaDto> result) {
		this.result = result;
		return this;
	}

	

	@Override
	public String toString() {
		StringBuilder sb= new StringBuilder("{");
		
		sb.append("\"axis_X\" : \"").append(axisX).append("\", ");
		sb.append("\"axis_Y\" : \"").append(axisY).append("\", ");
		sb.append("\"listData\" : [");
		
		String comma= "";
		for (TuplaDto tupla : result) {
			
			sb.append(comma).append("[").append(tupla).append("]");
			comma= ",\n    ";
		}
		
		sb.append("]}");
		return sb.toString();
	}

}
