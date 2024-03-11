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

import java.util.List;

public class HumanNameGenerator extends File2ListReader implements GeneratorInterface{
	private List<String> femaleNames = null;
	private List<String> maleNames = null;
	private List<String> sureNames = null;
	
	
	private void init() {
		if (sureNames == null && femaleNames == null && maleNames == null) {
			sureNames = populate("/sure_names");
			femaleNames = populate("/female_names");
			maleNames = populate("/male_names");
		}
	}
	
	public String getFemaleFirstName() {
		init();
		int index = (int) (Math.random() * femaleNames.size());
		return femaleNames.get(index);
	}
	
	public String getMaleFirstName() {
		init();
		int index = (int) (Math.random() * maleNames.size());
		return maleNames.get(index);
	}
	
	public String getAnyFirstName() {
		int sex = (int) (Math.random() * 2);
		if (sex == 0) {
			return getFemaleFirstName();
		} else {
			return getMaleFirstName();
		}
	}
	
	public String getSureName() {
		init();
		int index = (int) (Math.random() * sureNames.size());
		return sureNames.get(index);
	}
	
	/*Format syntax:
	 *{F} - Female Name 	
	 *{M} - Male Name
	 *{A} - Any Sex Name
	 *{S} - Sure Name
	*/
	public String getFullName(String format) {
		String fullName = format;
		if (format.contains("{F}")) {
			fullName = fullName.replace("{F}", getFemaleFirstName());
		}
		if (format.contains("{M}")) {
			fullName = fullName.replace("{M}", getMaleFirstName());
		}
		if (format.contains("{A}")){
			fullName = fullName.replace("{A}", getAnyFirstName());
		}
		if (format.contains("{S}")){
			fullName = fullName.replace("{S}", getSureName());
		}
		return fullName;
	}

	@Override
	public String generate(String pattern, String value) {
		return getFullName(pattern);
	}
	
//	@Test
//	public void test(){
//		HumanNameGenerator generator=new HumanNameGenerator();
//		System.out.println("Female First Name: " + generator.getFemaleFirstName());
//		System.out.println("Male First Name: " + generator.getMaleFirstName());
//		System.out.println("Any Sex First Name: " + generator.getAnyFirstName());
//		System.out.println("Sure Name: " + generator.getSureName());
//		System.out.println("Full Female Name ('{F} {S}'): " + generator.getFullName("{F} {S}"));
//		System.out.println("Full Male Name ('{S} {M}'): " + generator.getFullName("{S} {M}"));
//		System.out.println("Male and Female Full Name ('{M} and {F} {S}'): " + generator.getFullName("{M} and {F} {S}"));
//		
//	}
	
}
