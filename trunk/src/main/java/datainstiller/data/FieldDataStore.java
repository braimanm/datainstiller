package datainstiller.data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class FieldDataStore {
	private Map<Field,FieldData> fieldDataStore;
	private DataAliases aliases;
	
	FieldDataStore() {
		fieldDataStore = new HashMap<Field, FieldData>();
		aliases = new  DataAliases();
	}
	
	public DataAliases getAliases() {
		return aliases;
	}

	public void setAliases(DataAliases aliases) {
		this.aliases.putAll(aliases);
	}
	
	private Field getFieldFromClass(Class<?> clasz, String fieldName){
		for (Field field : clasz.getDeclaredFields()){
			if (field.getName().equals(fieldName)){
				return field;
			}
		}
		return null;
	}
	
	public FieldData getData(Field field){
		return fieldDataStore.get(field);  
	}
	
	public FieldData getData(Class<?> clasz, String fieldName){
		Field field = getFieldFromClass(clasz, fieldName);
		if (field==null) {
			return null;
		}
		return getData(field);  
	}
	
	public void setData(Class<?> clasz, String fieldName, FieldData data){
		Field field = getFieldFromClass(clasz, fieldName);
		if (field!=null) {
			setData(field, data);
		} else {
			throw new RuntimeException("Field '" + fieldName + "' not exists in class '" + clasz.getSimpleName() + "'");
		}
	}

	public void setData(Field field, FieldData data) {
		data.setParentStore(this);
		fieldDataStore.put(field,data);
	}
	
	public boolean containsKey(Field key){
		return fieldDataStore.containsKey(key);
	}

	
}
