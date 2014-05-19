package com.github.bbva.logsReader.dto;

import java.util.ArrayList;
import java.util.List;

import com.github.bbva.logsReader.annt.DTO;

/**
 * 
 * @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero -
 *         Neoris</a>
 * 
 */
@DTO
public class ResultDTO<T> {

	private T listData;
	private String[] heads;
	private String axisX;
	private String axisY;

	public ResultDTO() {
	}

	public ResultDTO(String axisX, String axisY, T result , String...heads) {
		this.heads = heads;
		this.axisX = axisX;
		this.axisY = axisY;
		this.listData = result;

//		List<Object> lst = new ArrayList<Object>();
//		lst.add(axisX);
//		lst.add(axisY);
//		listData.add(0, lst);

	}

	public String getAxisX() {
		return axisX;
	}

	public String getAxisY() {
		return axisY;
	}

	public T getListData() {
		return listData;
	}

	public void setAxisX(String axisX) {
		this.axisX = axisX;
	}

	public void setAxisY(String axisY) {
		this.axisY = axisY;
	}

	public void setListData(T listData) {
		this.listData = listData;
	}
//
//	private Object[][] buildArray(T lst) {
//		int i = 0;
//		int j = 0;
//		Object[][] result = new Object[lst.size()][lst.get(0).size()];
//		for (List list : lst) {
//			for (Object object : list) {
//				result[j][i++] = object;
//			}
//			j++;
//		}
//		return result;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.lang.Object#toString()
//	 */
//	@Override
//	public String toString() {
//
//		StringBuilder sb = new StringBuilder();
//
//		sb.append("{  \"axis_X\" : \"").append(this.axisX).append("\" ,");
//		sb.append("  \"axis_Y\" : \"").append(this.axisY).append("\" ,");
//		sb.append("  \"listData\" : [");
//
////		sb.append("["); 
////
////		String comma= "";
////		for (String head : heads) {
////			sb.append(comma).append("\"").append(head).append("\"");
////			comma= ", ";
////		}
////		sb.append("] \n");
//		
//		for (int index_j = 0; index_j < listData.size(); index_j++) {
//			sb.append(" ,\n       [");
//			String commaInternal = ", ";
//			sb.append("\"").append(listData.get(index_j).get(0)).append("\"");
//			for (int index_i = 1; index_i < listData.get(index_j).size(); index_i++) {
//				sb.append(commaInternal).append(listData.get(index_j).get(index_i));
//			}
//			for(Object obj: listData.get(index_j)){
//				
//			}
//			sb.append("]");
//		}
//
//		sb.append("]}");
//		return sb.toString();
//	}

}
