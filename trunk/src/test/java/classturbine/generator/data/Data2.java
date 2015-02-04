package classturbine.generator.data;

import datainstiller.data.Data;
import datainstiller.data.DataPersistence;

public class Data2 extends DataPersistence {
	byte byte1;
	Byte byte2;
	short short1;
	Short short2;
	int int1;
	Integer int2;
	long long1;
	Long long2;
	float float1;
	Float float2;
	double double1;
	Double double2;
	boolean boolean1;
	Boolean boolean2;
	char char1;
	Character char2;
	@Data(value = "value2", alias = "ALIAS2")
	String string;
	
	public class Data3 extends DataPersistence {
		String string;
	}
	
	public static class Data4 extends DataPersistence {
		String string;
	}
	
	
	
}
