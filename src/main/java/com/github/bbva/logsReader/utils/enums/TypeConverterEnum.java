/**
 * 
 */
package com.github.bbva.logsReader.utils.enums;

import java.math.BigDecimal;

/**
 * @author jose
 * 
 */
public enum TypeConverterEnum {
	
	INTEGER_TO_STRING(new InternalCommand<String>() {
		public String executeChange(Object value) {
			return value + "";
		}
	}),
	INTEGER_TO_LONG(new InternalCommand<Long>() {
		public Long executeChange(Object value) {
			return Long.parseLong(value+"");
		}
	}),
	LONG_TO_INTEGER(new InternalCommand<Integer>() {
		public Integer executeChange(Object value) {
			return Integer.parseInt(value + "");
		}
	}),

	LONG_TO_INT(new InternalCommand<Integer>() {
		public Integer executeChange(Object value) {
			return Integer.parseInt(value + "");
		}
	}),
	INTEGER_TO_INTEGER(new InternalCommand<Integer>() {
		public Integer executeChange(Object value) {
			return Integer.parseInt(value + "");
		}
	}),
	STRING_TO_INT(new InternalCommand<Integer>() {
		public Integer executeChange(Object value) {
			return Integer.parseInt(value + "");
		}
	}),
	LONG_TO_STRING(new InternalCommand<String>() {
		public String executeChange(Object value) {
			return value + "";
		}
	}),

	BIGDECIMAL_TO_DOUBLE(new InternalCommand<Double>() {
		public Double executeChange(Object value) {
			return ((BigDecimal) value).doubleValue();
		}
	}), BIGDECIMAL_TO_INT(new InternalCommand<Integer>() {
		public Integer executeChange(Object value) {
			return ((BigDecimal) value).intValue();
		}
	}), BIGDECIMAL_TO_LONG(new InternalCommand<Long>() {
		public Long executeChange(Object value) {
			return ((BigDecimal) value).longValue();
		}
	}), STRING_TO_STRING(new InternalCommand<String>() {
		public String executeChange(Object value) {
			return value.toString();
		}
	});

	InternalCommand<?> cmd;

	TypeConverterEnum(InternalCommand<?> cmd) {
		this.cmd = cmd;
	}

	@SuppressWarnings("unchecked")
	public <T> T converterData(Object value) {
		return (T) cmd.executeChange(value);
	}

	public static TypeConverterEnum get(String nameOrigin, String nameDestiny) {

		String pattern = String.format("%S_TO_%S", nameOrigin, nameDestiny);
		for (TypeConverterEnum type : TypeConverterEnum.values()) {
			if (pattern.equals(type.name()))
				return type;
		}
		return null;
	}

	private interface InternalCommand<T> {

		T executeChange(Object value);

	}
}
