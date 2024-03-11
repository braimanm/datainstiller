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

package datainstiller.data;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Braiman braimanm@gmail.com
 * 			This class represents aliases store. The only reason to have this class is for serialization and deserialization
 * 			of aliases by special XStream converter {@link DataAliasesConverter}. This class implements Map interface.
 */
@SuppressWarnings("unused")
public class DataAliases implements Map<String, Object> {
	@XStreamOmitField
	Map<String, Object> map;

	public DataAliases() {
		map = new HashMap<>();
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return  map.entrySet();
	}

	@Override
	public Object get(Object key) {
		return map.get(key);
	}

	public String resolveData(String data) {
		String value = data;
		Pattern pat = Pattern.compile("\\$\\{([^}]+)}");
		Matcher mat = pat.matcher(data);
		while (mat.find()) {
			String key = mat.group(1);
			if (this.containsKey(key)) {
				String val = this.getAsString(key);
				if (val != null) {
					value = value.replace(mat.group(), val);
				}
			}
		}
		return value;
	}

	public String getAsString(String key) {
		Object value = get(key);
		if (value == null) {
			return null;
		}
		if (value instanceof String) {
			return (String) value;
		}
		return value.toString();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public Object put(String key, Object value) {
		return map.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ?> m) {
		map.putAll(m);
	}

	@Override
	public Object remove(Object key) {
		return map.remove(key);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<Object> values() {
		return map.values();
	}

}
