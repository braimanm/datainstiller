/*
Copyright 2010-2024 Michael Braiman braimanm@gmail.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.braimanm.datainstiller.data;

import com.braimanm.datainstiller.context.DataContext;
import com.braimanm.datainstiller.generators.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.extended.ISO8601GregorianCalendarConverter;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.apache.commons.jexl3.JexlContext;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Michael Braiman braimanm@gmail.com
 * This class encapsulate object persistence capabilities. It allows to persist any derived from this class object with all his data.
 * All the class members which are not annotated with {@link XStreamOmitField} are serialized and deserialized to and from various formats 
 */
@SuppressWarnings("unused")
public abstract class DataPersistence {
	@XStreamOmitField
	private JexlContext jexlContext;
	@Data(skip = true)
    @XStreamAlias("xmlns")
    @XStreamAsAttribute
    protected String xmlns;
    @Data(skip = true)
    @XStreamAlias("xmlns:xsi")
    @XStreamAsAttribute
	protected String xsi;
    @Data(skip = true)
    @XStreamAlias("xsi:schemaLocation")
    @XStreamAsAttribute
	protected String schemaLocation;
	@Data(skip = true)
	private DataAliases aliases;

	protected DataPersistence() {
	}
	
	public DataAliases getDataAliases() {
		if (aliases == null) {
			aliases = new DataAliases();
		}
		return aliases;
	}

	protected void removeAliases() {
		this.aliases = null;
	}

	protected XStream getXstream() {
		XStream xstream = new XStream();
		xstream.addPermission(AnyTypePermission.ANY);
		xstream.registerConverter(new ISO8601GregorianCalendarConverter());
		xstream.processAnnotations(this.getClass());
		return xstream;
	}

	protected void initJexlContext() {
		DataAliases ga = DataContext.getGlobalAliases();
		if (!ga.containsKey("now")) {
			ga.put("now", LocalDateTime.now());
			ga.put("DateTimeFormatter", DateTimeFormatter.BASIC_ISO_DATE);
			ga.put("AddressGen", new AddressGenerator());
			ga.put("AlphaNumericGen", new AlphaNumericGenerator());
			ga.put("ListGen", new CustomListGenerator());
			ga.put("DateGen", new DateGenerator());
			ga.put("HumanNameGen", new HumanNameGenerator());
			ga.put("NumberGen", new NumberGenerator());
			ga.put("WordGen", new WordGenerator());
			ga.put("File2ListGen", new File2ListGenerator());
		}
	}

	private <T extends DataPersistence> void retainFields(T target) {
		Class<?> cls = this.getClass();
		do {
			for (Field field : cls.getDeclaredFields()) {
				field.setAccessible(true);
				if (!field.isAnnotationPresent(XStreamOmitField.class) ||
						(field.isAnnotationPresent(Data.class) && !field.getAnnotation(Data.class).skip())) {
					Object defaultPrimitiveValue = null;
					if (field.getType().isPrimitive()) {
						switch (field.getType().getName()) {
							case "byte":
								defaultPrimitiveValue = (byte) 0;
								break;
							case "short":
								defaultPrimitiveValue = (short) 0;
								break;
							case "int":
								defaultPrimitiveValue = 0;
								break;
							case "long":
								defaultPrimitiveValue = 0L;
								break;
							case "float":
								defaultPrimitiveValue = 0.0f;
								break;
							case "double":
								defaultPrimitiveValue = 0.0d;
								break;
							case "char":
								defaultPrimitiveValue = '\u0000';
								break;
							case "boolean":
								defaultPrimitiveValue = false;
								break;
						}
					}
					Object value;
					try {
						value = field.get(this);
						if (value != null) {
							if (!value.equals(defaultPrimitiveValue)) {
								field.set(target, value);
							}
						}
					} catch (IllegalAccessException ignore) {}
				}
			}

			cls = cls.getSuperclass();
		} while (!cls.equals(DataPersistence.class));
	}

	protected <T extends DataPersistence> void finalizeData(T data) {
		retainFields(data);
	}

	/**
	 * This method deserialize given XML string to the object 	
	 * @param xml XML string which represents deserialized object
	 * @return deserialized object
	 */
	@SuppressWarnings("unchecked")
	public <T extends DataPersistence> T fromXml(String xml) {
		initJexlContext();
		T data = (T) getXstream().fromXML(xml);
		finalizeData(data);
		return data;
	}

	/**
	 * This method deserialize file represented by URL to the object
	 * @param url URL pointer to the file to be deserialized
	 * @return deserialized object
	 */
	@SuppressWarnings("unchecked")
	public <T extends DataPersistence> T fromURL(URL url) {
		initJexlContext();
		T data=(T) getXstream().fromXML(url);
		finalizeData(data);
		return data;
	}

	/**
	 * This method deserialize {@link InputStream} to the object
	 * @param inputStream input stream to deserialize from
	 * @return deserialized object
	 */
	
	@SuppressWarnings("unchecked")
	public <T extends DataPersistence> T fromInputStream(InputStream inputStream) {
		initJexlContext();
		T data=(T) getXstream().fromXML(inputStream);
		finalizeData(data);
		return data;
	}

	/**
	 * This method deserialize given file to the object
	 * @param filePath file path to deserialize from
	 * @return deserialized object
	 */
	@SuppressWarnings("unchecked")
	public  <T extends DataPersistence> T fromFile(String filePath){
		initJexlContext();
		File file=new File(filePath);
		if (!file.exists()){
			throw new RuntimeException("File " + filePath + " was not found");
		}
		T data=(T) getXstream().fromXML(file);
		finalizeData(data);
		return data;
	}

	/**
	 * This method deserialize given resource file to the object 
	 * @param resourceFilePath path of resource file to deserialize from
	 * @return deserialized object
	 */
	public  <T extends DataPersistence> T fromResource(String resourceFilePath){
		URL url=Thread.currentThread().getContextClassLoader().getResource(resourceFilePath);
		if (url!=null) {
			return fromURL(url);
		}
		File file=new File(resourceFilePath);
		if (file.exists()){
			return fromFile(file.getAbsolutePath());
		}
		throw new RuntimeException("File '" + resourceFilePath + "' was not found!");
	}

	/**
	 * This method serializes this object to the given XML string
	 * @return XML representation of this object 
	 */
	public String toXML(){
		String header="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n";
		XStream xstream=getXstream();
		String xml = xstream.toXML(this).replaceAll(" xmlns=.*", ">"); // Remove xml namespaces;
		return header + xml;
	}
	
	/**
	 * This method serializes this object to the given file
	 * @param filePath file path to serialize this object
	 */
	public void toFile(String filePath){
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" + getXstream().toXML(this);
		try {
			Files.write(Paths.get(filePath), xml.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Copying one object to another
	 * @param source object to copy from
	 * @param target object to copy to
	 */
	public void deepCopy(DataPersistence source, DataPersistence target){
		XStream xstream=getXstream();
		String xml=xstream.toXML(source);
		xstream.fromXML(xml,target);
	}

	protected DataGenerator getGenerator() {
		return new DataGenerator(getXstream());
	}

	public void generateData(){
		DataPersistence obj = getGenerator().generate(this.getClass());
		deepCopy(obj, this);
	}
	
	public String generateXML(){
		DataPersistence obj = getGenerator().generate(this.getClass());
		return obj.toXML();
	}

	//Allows to generate data using IDE
	@Test
	protected void generate() {
		System.out.println(generateXML());
	}
	
}
