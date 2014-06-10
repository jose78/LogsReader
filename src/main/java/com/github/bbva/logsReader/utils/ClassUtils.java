package com.github.bbva.logsReader.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.github.bbva.logsReader.annt.Column;
import com.github.bbva.logsReader.annt.Id;
import com.github.bbva.logsReader.utils.enums.TypeConverterEnum;

/**
 * 
 *  @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero - Neoris</a> 
 * 
 */
@Component
public class ClassUtils {

	private static Logger log = Logger.getLogger(ClassUtils.class);
	public <T> void setValueInColumn(T data, String columnName, Object value) {

		String format= null;
		try {
			Field[] fields = data.getClass().getDeclaredFields();
			for (Field field : fields) {
				Column anntColumn = field.getAnnotation(Column.class);
				if (anntColumn != null
						&& anntColumn.name().toLowerCase()
								.equals(columnName.toLowerCase())) {
					format = value.getClass().getSimpleName() +" to " + field.getType();
					value = TypeConverterEnum.get(value.getClass().getSimpleName(), field.getType().getSimpleName()).converterData(value);
					field.setAccessible(true);
					field.set(data, value);
					break;
				}
			}
		} catch (Exception e) { 
			log.error("Error format:"+format , e);
			e.printStackTrace();
		}
	}

	public <T> void setIdInEntity(T entity, String nameColumn, Object value) {
		try {
			Field[] fields = entity.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				Column annt = field.getAnnotation(Column.class);
				boolean flagIsSelectable = annt != null
						&& annt.name().equals(nameColumn);
				if (flagIsSelectable)
					field.set(entity, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> Object[] getValueClass(T entity, Class annotationClass,
			Class... annotationClassToExclude) {

		try {
			List<Object> lstResult = new ArrayList<Object>();
			Field[] fields = entity.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				Object annt = field.getAnnotation(annotationClass);
				boolean flagIsSelectable = true && annt != null;

				for (int index = 0; index < annotationClassToExclude.length
						&& flagIsSelectable; index++) {
					flagIsSelectable = field
							.getAnnotation(annotationClassToExclude[index]) == null;
				}
				Object result= null;
				if (flagIsSelectable && (result= field.get(entity)) != null)
					lstResult.add(result);

			}
			return lstResult.toArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieve an array with the specific value in the annotation. 
	 * <b>NOTE:</b> For each annotation will be validated and if:
	 * <li> Has other annotation and this is contained in list of annotation to exclude.
	 * <li> Has a null value in the atribute
	 * then:
	 * -> this value will not include in the result array.
	 * @param data
	 * @param annotationClass
	 * @param providerValueAnnt
	 * @param valueNulable this param indicates if the atribute must be null or not. 
	 * @param annotationClassToExclude
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> String[] getValueAnnt(T data, Class annotationClass,
			ProviderValueAnnt<String> providerValueAnnt,
			boolean valueNulable, Class... annotationClassToExclude) {

		try {
			List<String> lstResult = new ArrayList<String>();
			Field[] fields = data.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				Object annt = field.getAnnotation(annotationClass);
				boolean flagIsSelectable = true && annt != null
						&& (valueNulable || field.get(data) != null);

				for (int index = 0; index < annotationClassToExclude.length
						&& flagIsSelectable; index++) {
					flagIsSelectable = field
							.getAnnotation(annotationClassToExclude[index]) == null;
				}
				if (flagIsSelectable) {
					lstResult.add(providerValueAnnt.get(annt));
				}
			}
			return lstResult.toArray(new String[lstResult.size()]);
		} catch (Exception e) {
			 throw new LogsReaderException(e);
		}
	}

	/**
	 * Retrieve an array with the <code>@Column</code> annotation that have the <code>@ID</code> annotation.
	 * @param clazz
	 * @return
	 */
	public <T> Column[] getIdColumnd(Class<T> clazz) {

		List<Column> lstResult = new ArrayList<Column>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {

			Column annt = field.getAnnotation(Column.class);
			boolean flagIsSelectable = true && annt != null
					&& field.getAnnotation(Id.class) != null;

			if (flagIsSelectable) {
				lstResult.add(annt);
			}
		}
		return lstResult.toArray(new Column[lstResult.size()]);
	}

	public interface ProviderValueAnnt<T> {
		T get(Object annt);
	}

	/**
	 * Retrieve an array with the name of the <b>id</b> columns.
	 * 
	 * @param clazz
	 * @return arraya with the name of the id columns
	 */
	public <T> String[] getIdColumndName(Class<T> clazz) {
		try {
			List<String> lstResult = new ArrayList<String>();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {

				Column annt = field.getAnnotation(Column.class);
				boolean flagIsSelectable = true && annt != null
						&& field.getAnnotation(Id.class) != null;

				if (flagIsSelectable) {
					lstResult.add(annt.name());
				}
			}
			return lstResult.toArray(new String[lstResult.size()]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
