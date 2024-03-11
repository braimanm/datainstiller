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

package datainstiller.generators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class File2ListReader {

	public List<String> populate(String fileName) {
		List<String> listToPopulate = new ArrayList<>();
		InputStream inStream = this.getClass().getResourceAsStream(fileName);
		if (inStream == null)
			throw new RuntimeException("Resource file: " + fileName + " was not found!");
		BufferedReader reader = new BufferedReader(new InputStreamReader(inStream), 100000);
		String line;
		try {
			while ((line = reader.readLine()) != null) listToPopulate.add(line);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return listToPopulate;
	}
}
