package com.github.bbva.logsReader.view;

/**
 * 
 * @author jose
 * 
 */
public class ColumnTableView {

	private String label;
	private String type;
	private String id;

	public ColumnTableView() {
		// TODO Auto-generated constructor stub
	}

	public ColumnTableView(String label, String type, String id) {
		super();
		this.label = label;
		this.type = type;
		this.id = id;
	}

	public ColumnTableView(String label, String type) {
		super();
		this.label = label;
		this.type = type;
		this.id = label;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return String.format("Column [label=%s, type=%s, id=%s]", label, type,
				id);
	}
}
