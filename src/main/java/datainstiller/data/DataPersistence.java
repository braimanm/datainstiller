/*
Copyright 2010-2012 Michael Braiman

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

package datainstiller.data;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.extended.ISO8601GregorianCalendarConverter;
import org.testng.annotations.Test;

import java.io.*;
import java.net.URL;

/**
 * @author Michael Braiman braimanm@gmail.com
 *
 * This class encapsulate object persistence capabilities. It allows to persist any derived from this class object with all his data.
 * All the class members which are not annotated with {@link XStreamOmitField} are serialized and deserialized to and from various formats 
 */
public abstract class DataPersistence {
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
	@Data(skip=true)
	private DataAliases aliases;

	protected DataPersistence() {
	}
	
	public DataAliases getDataAliases(){
		return aliases;
	}
	
	 protected XStream getXstream(){
		XStream xstream = new XStream();
		xstream.registerConverter(new ISO8601GregorianCalendarConverter());
		xstream.processAnnotations(this.getClass());
		return xstream;
	}
	
	private  <T extends DataPersistence> Object resolveAliases(DataPersistence data) {
		DataAliases aliases = data.aliases;
		data.aliases = null;
		String xml = data.toXML();
		for (String key : aliases.keySet()){
			String alias = "${" + key + "}";
			String value = aliases.get(key);
			xml = xml.replace(alias, value); 
		}
		return getXstream().fromXML(xml);
	}
	
	/**
	 * This method deserialize given XML string to the object 	
	 * @param xml XML string which represents deserialized object
	 * @param resolveAliases resolve aliases during serialization
	 * @return deserialized object
	 */
	@SuppressWarnings("unchecked")
	public  <T extends DataPersistence> T fromXml(String xml, boolean resolveAliases){	
		T data = (T) getXstream().fromXML(xml);
		if (resolveAliases) {
			data = (T) resolveAliases(data);
		}
		return data;
	}
	
	public  <T extends DataPersistence> T fromXml(String xml) {
		return fromXml(xml,false);
	}
	
	/**
	 * This method deserialize file represented by URL to the object
	 * @param url URL pointer to the file to be deserialized
	 * @param resolveAliases resolve aliases during serialization
	 * @return deserialized object
	 */
	@SuppressWarnings("unchecked")
	public  <T extends DataPersistence> T fromURL(URL url, boolean resolveAliases){
		T data=(T) getXstream().fromXML(url);
		if (resolveAliases) {
			data = (T) resolveAliases(data);
		}
		return  data;
	}
	
	public  <T extends DataPersistence> T fromURL(URL url){
		return fromURL(url,false);
	}
	
	/**
	 * This method deserialize {@link InputStream} to the object
	 * @param inputStream input stream to deserialize from
	 * @param resolveAliases resolve aliases during serialization
	 * @return deserialized object
	 */
	
	@SuppressWarnings("unchecked")
	public  <T extends DataPersistence> T fromInputStream(InputStream inputStream, boolean resolveAliases){
		T data=(T) getXstream().fromXML(inputStream);
		if (resolveAliases) {
			data = (T) resolveAliases(data);
		}
		return data;
	}
	
	public  <T extends DataPersistence> T fromInputStream(InputStream inputStream) {
		return fromInputStream(inputStream,false);
	}
	
	/**
	 * This method deserialize given resource file to the object 
	 * @param resourceFilePath path of resource file to deserialize from
	 * @param resolveAliases resolve aliases during serialization
	 * @return deserialized object
	 */
	public  <T extends DataPersistence> T fromResource(String resourceFilePath, boolean resolveAliases){
		URL url=Thread.currentThread().getContextClassLoader().getResource(resourceFilePath);
		if (url!=null) {
			return fromURL(url, resolveAliases);
		}
		File file=new File(resourceFilePath);
		if (file.exists()){
			return fromFile(file.getAbsolutePath(), resolveAliases);
		}
		throw new RuntimeException("File '" + resourceFilePath + "' was not found!");
	}
	
	public  <T extends DataPersistence> T fromResource(String resourceFile){
		return fromResource(resourceFile, false);
	}
	
	/**
	 * This method deserialize given file to the object 
	 * @param filePath file path to deserialize from
	 * @param resolveAliases resolve aliases during serialization
	 * @return deserialized object
	 */
	@SuppressWarnings("unchecked")
	public  <T extends DataPersistence> T fromFile(String filePath, boolean resolveAliases){
		File file=new File(filePath);
		if (!file.exists()){
			throw new RuntimeException("File " + filePath + " was not found");
		}
		T data=(T) getXstream().fromXML(file);
		if (resolveAliases) {
			data = (T) resolveAliases(data);
		}
		return data;
	}

	public  <T extends DataPersistence> T fromFile(String filePath){
		return fromFile(filePath, false);
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
		FileOutputStream fos=null;
		Writer writer=null;
		try {
			fos=new FileOutputStream(filePath);
			writer=new OutputStreamWriter(fos, "UTF-8");
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n");
			getXstream().toXML(this, writer);
		} catch ( IOException e) {
			throw new RuntimeException(e);
		} finally{
			
				try {
					if (writer!=null) writer.close();
					if (fos!=null) fos.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
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
	
	public void generateData(){
		DataPersistence obj = new DataGenerator().generate(this.getClass());
		deepCopy(obj, this);
	}
	
	public String generateXML(){
		DataPersistence obj = new DataGenerator().generate(this.getClass());
		return obj.toXML();
	}

	//Allows to generate data using IDE
	@Test
	public void generate() {
		System.out.println(generateXML());
	}
	
}
