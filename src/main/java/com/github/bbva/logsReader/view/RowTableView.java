package com.github.bbva.logsReader.view;

import java.util.ArrayList;
import java.util.List;

public class RowTableView {

	private List<C> c = new ArrayList<RowTableView.C>();

	public List<C> getC() {
		return c;
	}

	public RowTableView() {
	}

	public void addCol(Object... values) {

		for (Object value : values) {
			if (value instanceof Object[]) {
				Object[] data = (Object[]) value;
				c.add(new C(data[0], data[0] + " " + data[1]));
			} else
				c.add(new C(value.toString(), value.toString()));
		}
	}

	@Override
	public String toString() {
		return String.format("Row [c=%s]", c);
	}

	private class C {
		private Object v;
		private String f;

		public C(Object v, String f) {
			this.v = v;
			this.f = f;
		}

		public String getF() {
			return f;
		}

		public Object getV() {
			return v;
		}

		@Override
		public String toString() {
			return String.format("C [v=%s, f=%s]", v, f);
		}

	}
}
