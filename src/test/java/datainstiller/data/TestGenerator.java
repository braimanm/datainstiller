package classturbine.generator.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

import classturbine.generator.data.Data2.Data3;
import classturbine.generator.data.Data2.Data4;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import datainstiller.data.Data;
import datainstiller.data.DataGenerator;
import datainstiller.data.DataPersistence;

@XStreamAlias("data1")
public class TestGenerator extends DataPersistence {
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
	@Data(value = "value", alias = "ALIAS")
	String string;
	Data2 data;
	@Data(clasz=ArrayList.class)
	List<String> list;
	@Data(clasz=HashSet.class)
	Set<String> set;
	
	//@Test
	public void test_generic_functionality(){
		String xml = generateXML();
		System.out.println(xml);
		TestGenerator test = fromXml(xml,this.getClass(),false);
		Assert.assertEquals(xml, test.toXML());
	}
	
	@Test
	public void test_generators_and_aliases(){
		 TestGenerator test = DataPersistence.fromResource("Data1.xml", this.getClass(), true);
		 Assert.assertNull(test.aliases);
		 Assert.assertTrue(test.string.matches("\\(\\d{3}\\) \\d{3}-\\d{4}"));
		 Assert.assertTrue(test.data.string.matches("\\d{2} \\D+ \\d{4}"));
		 for (String data : test.list){
			 Assert.assertTrue(data.matches("\\d{2} \\D+ \\d{4}"));
		 }
		 for (String data : test.set ){
			 Assert.assertTrue(data.matches("\\w+ \\w+"));
		 }
		 System.out.println(test.toXML());
	}
	
	
	//@Test
	public void test_inner_classes_generation(){
		DataGenerator gen = new DataGenerator();
		Data3 data3 = gen.generate(Data3.class);
		Assert.assertNull(data3);
		Data4 data4 =  gen.generate(Data4.class);
		Assert.assertNotNull(data4.string);
	}

}
