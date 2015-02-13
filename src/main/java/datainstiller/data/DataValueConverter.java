package datainstiller.data;

import com.thoughtworks.xstream.converters.Converter;

public interface DataValueConverter extends Converter {
	public <T> T fromString(String str, Class<T> cls );
}
