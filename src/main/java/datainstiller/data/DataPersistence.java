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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.extended.ISO8601GregorianCalendarConverter;

/**
 * @author Michael Braiman braimanm@gmail.com
 *
 * This class encapsulate object persistence capabilities. It allows to persist any derived from this class object with all his data.
 * All the class members which are not annotated with {@link XStreamOmitField} are serialized and deserialized to and from various formats 
 */
public class DataPersistence {
	@XStreamAlias("xmlns")
	@XStreamAsAttribute
	protected String xmlns;
	
	@XStreamAlias("xmlns:xsi")
	@XStreamAsAttribute
	protected String xsi;
	
	@XStreamAlias("xsi:schemaLocation")
	@XStreamAsAttribute
	protected String schemaLocation;
	
	protected DataAliases aliases;

	
	public static XStream getXstream() {
		return getXstream(null);
	}
	
	 protected static XStream getXstream(Class<?> clz){
		XStream xstream = new XStream();
		xstream.registerConverter(new ISO8601GregorianCalendarConverter());
		if (clz != null ){
			xstream.processAnnotations(clz);
		}
		return xstream;
	}
	
	protected static <T extends DataPersistence> Object resolveAliases(DataPersistence data, Class<T> forClass) {
		DataAliases aliases = data.aliases;
		data.aliases = null;
		String xml = data.toXML();
		for (String key : aliases.keySet()){
			String alias = "${" + key + "}";
			String value = aliases.get(key);
			xml = xml.replace(alias, value); 
		}
		return getXstream(forClass).fromXML(xml);
	}
	
	/**
	 * This method deserialize given XML string to the object 	
	 * @param xml XML string which represents deserialized object
	 * @param forClass class to deserialize 
	 * @return deserialized object
	 */
	@SuppressWarnings("unchecked")
	public static <T extends DataPersistence> T fromXml(String xml, Class<T> forClass, boolean resolveAliases){	
		T data = (T) getXstream(forClass).fromXML(xml);
		if (resolveAliases) {
			data = (T) resolveAliases(data,forClass);
		}
		return data;
	}
	
	/**
	 * This method deserialize file represented by URL to the object
	 * @param url URL pointer to the file to be deserialized
	 * @param forClass class to deserialize
	 * @return deserialized object
	 */
	@SuppressWarnings("unchecked")
	public static <T extends DataPersistence> T fromURL(URL url, Class<T> forClass, boolean resolveAliases){
		T data=(T) getXstream(forClass).fromXML(url);
		if (resolveAliases) {
			data = (T) resolveAliases(data,forClass);
		}
		return  data;
	}
	
	/**
	 * This method deserialize {@link InputStream} to the object
	 * @param inputStream input stream to deserialize from
	 * @param forClass class to deserialize
	 * @return deserialized object
	 */
	
	@SuppressWarnings("unchecked")
	public static <T extends DataPersistence> T fromInputStream(InputStream inputStream,Class<T> forClass, boolean resolveAliases){
		T data=(T) getXstream(forClass).fromXML(inputStream);
		if (resolveAliases) {
			data = (T) resolveAliases(data,forClass);
		}
		return data;
	}
	
	/**
	 * This method deserialize given resource file to the object 
	 * @param resourceFile resource file to deserialize from
	 * @param forClass class to deserialize
	 * @return deserialized object
	 */
	public static <T extends DataPersistence> T fromResource(String resourceFile,Class<T> forClass, boolean resolveAliases){
		URL url=Thread.currentThread().getContextClassLoader().getResource(resourceFile);
		return fromURL(url, forClass, resolveAliases);
	}
	
	/**
	 * This method deserialize given file to the object 
	 * @param filePath file path to deserialize from
	 * @param forClass class to deserialize
	 * @return deserialized object
	 */
	@SuppressWarnings("unchecked")
	public static <T extends DataPersistence> T fromFile(String filePath, Class<T> forClass, boolean resolveAliases){
		File file=new File(filePath);
		if (!file.exists()){
			throw new RuntimeException("File " + filePath + " was not found");
		}
		T data=(T) getXstream(forClass).fromXML(file);
		if (resolveAliases) {
			data = (T) resolveAliases(data,forClass);
		}
		return data;
	}

	/**
	 * This method serializes this object to the given XML string
	 * @return XML representation of this object 
	 */
	public String toXML(){
		String header="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n";
		XStream xstream=getXstream(this.getClass());
		String xml=xstream.toXML(this);
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
			getXstream(this.getClass()).toXML(this, writer);
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
	public static void deepCopy(Object source,Object target){
		XStream xstream=getXstream();
		String xml=xstream.toXML(source);
		xstream.fromXML(xml,target);
	}
	
	public void generateData(){
		DataGenerator.getInstance().generate(this);
	}
	
	public String generateXML(){
		DataPersistence obj = DataGenerator.getInstance().generate(this.getClass());
		return obj.toXML();
	}
	
}
