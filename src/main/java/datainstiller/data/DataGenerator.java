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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.collections.ArrayConverter;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.converters.enums.EnumConverter;
import com.thoughtworks.xstream.converters.enums.EnumSetConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.core.util.Primitives;

import datainstiller.generators.AddressGenerator;
import datainstiller.generators.AlphaNumericGenerator;
import datainstiller.generators.CustomListGenerator;
import datainstiller.generators.DateGenerator;
import datainstiller.generators.File2ListGenerator;
import datainstiller.generators.GeneratorInterface;
import datainstiller.generators.HumanNameGenerator;
import datainstiller.generators.NumberGenerator;
import datainstiller.generators.WordGenerator;

public class DataGenerator {
	private int nArray = 3;
	private int recursionLevel = 0; 
	private FieldDataStore fieldDataStore;
	private Map<String,Integer> classes;
	private Map<String,GeneratorInterface> generatorStore;
	private XStream xstream;
	
	public DataGenerator() {
		this(null);
	}
	
	public DataGenerator(List<DataValueConverter> converters){
		fieldDataStore = new FieldDataStore();
		generatorStore = new HashMap<>();
		registerGenerator("ADDRESS", new AddressGenerator());
		registerGenerator("ALPHANUMERIC", new AlphaNumericGenerator());
		registerGenerator("CUSTOM_LIST",new CustomListGenerator());
		registerGenerator("DATE",new DateGenerator());
		registerGenerator("HUMAN_NAMES",new HumanNameGenerator());
		registerGenerator("WORD",new WordGenerator());
		registerGenerator("NUMBER",new NumberGenerator());
		registerGenerator("FILE2LIST",new File2ListGenerator());
		xstream = new XStream();
        if (converters!=null) {
            for (Converter converter : converters){
            	xstream.registerConverter(converter);
            }
        }
	}
	
	public GeneratorInterface getGenerator(String generator){
		return  generatorStore.get(generator);
	}
	
	public void registerGenerator(String key, GeneratorInterface generator){
		generatorStore.put(key, generator);
	}
		
	public int getRecursionLevel() {
		return recursionLevel;
	}
	
	public void setRecursionLevel(int recursionLevel) {
		this.recursionLevel = recursionLevel;
	}
	
	private String getGeneratedValue(FieldData fieldData){
		String value = fieldData.value();
		String alias = fieldData.alias();
		String aliasValue = (alias!=null) ? (String) fieldDataStore.getAliases().get(alias) : null;
		
		if (aliasValue!=null){
			return "${" + alias + "}";
		}
		
		if (fieldData.generatorType()!=null){
			GeneratorInterface generator = generatorStore.get(fieldData.generatorType());
			if (generator!=null){
				value = generator.generate(fieldData.pattern() ,fieldData.value());
			} else {
				throw new GeneratorNotFoundException("Generator " + fieldData.generatorType() + " was not found!");
			}
		}
		
		if (alias!=null) {
			fieldDataStore.getAliases().put(alias, value);
			return "${" + alias + "}";
		}
		
		return value;
	}
	
	private String generateValueForField(Class<?> cls,Field field){
		String returnValue=null;
		FieldData fieldData = fieldDataStore.getData(field);
		if (fieldData!=null){
			returnValue = getGeneratedValue(fieldData);
		}
		if(cls.isArray()){
			cls = cls.getComponentType();
		}
		if (cls.isPrimitive() || Primitives.unbox(cls)!=null || cls.isEnum()) {
			if (returnValue==null || returnValue.isEmpty()){
				return "0";
			};
			if (returnValue.startsWith("${")) {
				return fieldData.resolveAlias();
			}
		} 
		if (cls.equals(Date.class)){
			String defaultPattern = "yyyy-MM-dd HH:mm:ss.S z";
			return new SimpleDateFormat(defaultPattern).format(new Date());
		}
		
		if (returnValue==null){
				return field.getName();
		}
			
		return returnValue;
	}
	
	private boolean isInnerClass(Class<?> cls){
		if (cls.isMemberClass() && !Modifier.isStatic(cls.getModifiers())){
			System.err.println("[WARNING] Only static nested classes are supported. Class " + cls.getCanonicalName() +" should be declared as static!");
			return true;
		} 
		return false;
	}
	
	private void processAnnotations(Class<?> clasz){
		MetaData metaData = clasz.getAnnotation(MetaData.class);
		if (metaData!=null){
			for (Data data: metaData.value()){
				Class<?> cls = clasz;
				if (data.fieldClass()!=void.class){
					cls = data.fieldClass();
				}
				if (data.fieldName().trim().isEmpty()){
					throw new AnnotationProcessingException("Field 'fieldName' must be provided in MetaData annotation" + data);
				}
				fieldDataStore.setData(cls, data.fieldName(), new FieldData(data));
			}
		}
		Class<?> superClasz = clasz;
		do {
			for (Field field : superClasz.getDeclaredFields()){
				Data data = field.getAnnotation(Data.class);
				if (data==null){
					continue;
				}
				if (!fieldDataStore.containsKey(field)){
				    fieldDataStore.setData(field, new FieldData(data));
				}
			}
			superClasz = superClasz.getSuperclass();
		} while (superClasz!=null);
	}
	
	@SuppressWarnings("restriction")
	private Class<?> getGenericTypeOrString(Field field,int argumentNum){
		Type type = field.getGenericType();
		if (type!=null && type instanceof sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl){
			Type realType = ((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)type).getActualTypeArguments()[argumentNum];
			if (!(realType instanceof sun.reflect.generics.reflectiveObjects.WildcardTypeImpl)){
				if (realType instanceof sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl){
					System.err.println("[WARNING] Collection of collection is not supported in field " + field);
					return null;
				} else {
					return (Class<?>) realType;
				}
			}
		}
		return String.class;
	}
	
	public <T> T generate(Class<T> cls) {
		if (isInnerClass(cls)){
			return null;
		}
		
		classes = new HashMap<>();
		T obj = generate(cls,null,false);
		
		if (fieldDataStore.getAliases().size()>0 ){
			boolean dataAliasesFound = false;
			Class<?> clz = cls;
			do {
				for (Field field : clz.getDeclaredFields()) {
					if (field.getType().equals(DataAliases.class)){
						try {
							field.setAccessible(true);
							field.set(obj,fieldDataStore.getAliases());
							dataAliasesFound =true;
							break;
						} catch (IllegalArgumentException | IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}
				}
				clz=clz.getSuperclass();
			} while(clz!=null);
			
			if (!dataAliasesFound) {
				throw new AliasWriteException("Can't save aliases! The generated class or its supper class should have DataAliases type field declared.");
			}
		}
		
		return obj;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes", "restriction" })
	private <T> T generate(Class<T> cls, Field ffield,boolean arrayOrColection) {
		processAnnotations(cls);

		Converter conv = xstream.getConverterLookup().lookupConverterForType(cls);
		
		if (conv instanceof DataValueConverter) {
			String stringValue = generateValueForField(cls,ffield);
			Object value = ((DataValueConverter) conv).fromString(stringValue,cls);
			return (T) value;
		}
		
		if (conv instanceof SingleValueConverter){
			String stringValue = generateValueForField(cls,ffield);
			Object value = ((SingleValueConverter) conv).fromString(stringValue);
			return (T) value;
		}
		
		if (conv instanceof ArrayConverter){
			int n=nArray;
			FieldData fieldData = fieldDataStore.getData(ffield);
			if (fieldData!=null && fieldData.nArray()>0) { 
				n=fieldData.nArray();
			}
			T array = (T) Array.newInstance(cls.getComponentType(), n);

			for (int i=0;i<n;i++){
				Array.set(array, i, generate(cls.getComponentType(), ffield,true));
			}
			return array;
		}
		
		if (cls.isInterface() || Modifier.isAbstract(cls.getModifiers())) {
			FieldData fieldData = fieldDataStore.getData(ffield);
			if (fieldData!=null && fieldData.clasz()!=null){
				for (Class<?> clz: fieldData.clasz()){
					if (cls.isAssignableFrom(clz)){
						return (T) generate(clz, ffield,arrayOrColection);
					}
				}	
			}
			Class[] collection = new Class[]{ArrayList.class,HashSet.class,HashMap.class};
			for (Class<?> clz: collection){
				if (cls.isAssignableFrom(clz)){
					return (T) generate(clz, ffield,arrayOrColection);
				}
			}	
			System.err.println("[WARNING] Please provide implimentation class for " + cls.getCanonicalName() + " field '" + ffield + "'");
			return null;
		} 
		
		if (conv instanceof CollectionConverter){
			Collection collection = null;
			try {
				collection = (Collection) cls.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			
			Class element = getGenericTypeOrString(ffield,0);
			if (element!=null){
				for (int i=0;i<nArray;i++){
					collection.add(generate(element, ffield,true));
				}
			}
			return (T) collection;
		}
		
		if (conv instanceof MapConverter){
			Map map = null;
			try {
				map = (Map) cls.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			Class keyElement = getGenericTypeOrString(ffield, 0);
			Class valueElement = getGenericTypeOrString(ffield, 1);
			if (keyElement!=null && valueElement!=null){			
				map.put(generate(keyElement,ffield,false), generate(valueElement,ffield,false));
			}
			return (T) map;
		}
		
		if (conv instanceof EnumConverter){
			return cls.getEnumConstants()[Integer.parseInt(generateValueForField(cls,ffield))];
		}
		
		if (conv instanceof EnumSetConverter){
			Type type = ffield.getGenericType();
			if (type!=null && type instanceof sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl){
				Class genType = (Class) ((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)type).getActualTypeArguments()[0];
				Enum e = (Enum) genType.getEnumConstants()[Integer.parseInt(generateValueForField(genType,ffield))];
				return (T) EnumSet.of(e);
			}
		}
		
		if (conv instanceof ReflectionConverter){
			if (ffield!=null && !arrayOrColection){
		    	String key = ffield.toString();
		    	Integer i = classes.get(key);
		    	if (i==null){
		    		i=0;
		    	} else {
		    		if (i==recursionLevel) {
		    			return null;
		    		}
		    		i++;
		    	}
		    	classes.put(key, i);
		    }
			
			Object obj = xstream.getReflectionProvider().newInstance(cls);
			Class superCls = cls;
			do {	
				for (Field field : superCls.getDeclaredFields()){
					if (isInnerClass(field.getType())){
						System.err.println("          Field '" + field.getName() +"' was skipped by generator." );
						continue;
					}
					field.setAccessible(true);
					FieldData fieldData = fieldDataStore.getData(field);
					if (fieldData!=null && fieldData.skip()){ 
						continue;
					}
					if (field.isAnnotationPresent(XStreamOmitField.class)){
						continue;
					}
					if (Modifier.isStatic(field.getModifiers())){
						continue;
					}
					
					Object value = generate(field.getType(),field,false);
					try {
						field.set(obj, value);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}  catch (NullPointerException e){
						throw new RuntimeException(e);
					}
				}
				superCls = superCls.getSuperclass();
			} while (superCls!=null);
			return (T) obj;
		}
		
		if (conv instanceof DataAliasesConverter) {
			//No data should be generated for DataAliasesConverter
			return null;
		}
		
		System.err.println("[WARNING] Converter type: " + conv.getClass().getName() + ". Can't generate data for field " + ffield);
		return null;
	}
	
}
