package datainstiller.data;

import com.thoughtworks.xstream.converters.Converter;

import java.lang.reflect.Field;

public interface DataValueConverter extends Converter {
	public <T> T fromString(String str, Class<T> cls, Field field);
}
