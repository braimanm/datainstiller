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
import com.braimanm.datainstiller.generators.GeneratorInterface;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Michael Braiman braimanm@gmail.com
 *          This is{@link XStream} Converter implementation for marshaling and unmarshaling {@link DataAliases} map.
 *          During unmarshaling, if alias value is data generator expression then this expression is resolved to data using specific generator. 
 */
@SuppressWarnings("unused")
public class DataAliasesConverter implements Converter {
	@Override
	public boolean canConvert(Class type) {
		return (type.equals(DataAliases.class));
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		if (source != null) {
			DataAliases aliases=(DataAliases)source;
			for (String key : aliases.keySet()) {
				writer.startNode(key);
				writer.setValue(aliases.getAsString(key));
				writer.endNode();
			}
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		DataAliases aliases = new DataAliases();
		GlobalAliases globalAliases = DataContext.getGlobalAliases();
		String nodeName;
		boolean global;
		String value;
        Object objValue = null;
        while (reader.hasMoreChildren()) {
			reader.moveDown();
			nodeName = reader.getNodeName();
			value = reader.getValue();
			//Data Generator Handling
            if (value.matches("\\$\\[.+]")) {
                Pattern pattern = Pattern.compile("\\$\\[(.+)\\(\\s*'\\s*(.*)\\s*'\\s*,\\s*'\\s*(.*)\\s*'\\s*\\)");
                Matcher matcher = pattern.matcher(value);
				if (!matcher.find()) {
					throw new PatternUnmarshalException(value + " - invalid data generation expression!");
				}	
				GeneratorInterface genType = new DataGenerator(new XStream()).getGenerator(matcher.group(1).trim());
				String init = matcher.group(2);
				String val = matcher.group(3);
				value = genType.generate(init, val);
            } else {
				objValue = globalAliases.evaluateExpression(value);
            }
            if (objValue != null) {
				aliases.put(nodeName, objValue);
				globalAliases.put(nodeName, objValue);
            } else {
				aliases.put(nodeName, value);
                globalAliases.put(nodeName, value);
            }
            objValue = null;
            reader.moveUp();
		}
		return aliases;
	}
}
