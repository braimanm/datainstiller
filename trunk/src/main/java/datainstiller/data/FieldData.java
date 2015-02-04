package datainstiller.data;

public class FieldData {
	private Class<?>[] clasz = null;
	private String alias = null;
	private String generatorType = null;
	private String pattern = "";
	private String value = "";
	private int nArray = 0;
	private int nRecursion = 0;
	private boolean skipField;
	private FieldDataStore fieldDataStore;
	
	public FieldData() {
	}
	
	public FieldData(Data data) {
		clasz = (data.clasz().equals(void.class)) ? null : data.clasz();
		alias = data.alias().isEmpty() ? null : data.alias();
		generatorType = data.generatorType().isEmpty() ? null : data.generatorType();
		pattern = data.pattern();
		value = data.value();
		nArray = data.nArray();
		nRecursion = data.nRecursion();
		skipField = data.skip();
	}

	void setParentStore(FieldDataStore fieldDataStore) {
		this.fieldDataStore = fieldDataStore;
	}

	public String resolveAlias() {
		if (fieldDataStore!=null && alias!=null){
			return (String) fieldDataStore.getAliases().get(alias);
		}
		return null;
	}

	public boolean skip() {
		return skipField;
	}

	public String generatorType() {
		return generatorType;
	}

	public String value() {
		return value;
	}

	public String pattern() {
		return pattern;
	}

	public String alias() {
		return alias;
	}
	
	public int nArray() {
		return nArray;
	}

	public int nRecursion() {
		return nRecursion;
	}

	public Class<?>[] clasz() {
		return clasz;
	}

	public FieldData setClasz(Class<?>[] clasz) {
		this.clasz = clasz;
		return this;
	}
	
	public FieldData setClasz(Class<?> clasz) {
		this.clasz = new Class<?>[] {clasz};
		return this;
	}

	public FieldData setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public FieldData setGeneratorType(String generatorType) {
		this.generatorType = generatorType;
		return this;
	}

	public FieldData setPattern(String pattern) {
		this.pattern = pattern;
		return this;
	}

	public FieldData setValue(String value) {
		this.value = value;
		return this;
	}

	public FieldData setnArray(int nArray) {
		this.nArray = nArray;
		return this;
	}

	public FieldData setnRecursion(int nRecursion) {
		this.nRecursion = nRecursion;
		return this;
	}

	public FieldData setSkipField(boolean skipField) {
		this.skipField = skipField;
		return this;
	}

	
	
}
