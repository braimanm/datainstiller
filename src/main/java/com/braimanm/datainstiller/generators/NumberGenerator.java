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

package com.braimanm.datainstiller.generators;

import java.text.DecimalFormat;

@SuppressWarnings("unused")
public class NumberGenerator implements GeneratorInterface{
	
	long minNum;
	long maxNum;
	String format;
	
	public NumberGenerator() {
	}

	public NumberGenerator(String min, String max, String format) {
		minNum = Long.parseLong(min);
		maxNum = Long.parseLong(max);
		this.format = format;
	}
	
	public String getNum(){
		double num = minNum + Math.random() * (maxNum - minNum);
		DecimalFormat dec = new DecimalFormat(format);
		return dec.format(num);
	}

	@Override
	public String generate(String pattern, String value) {
		String[] limits = value.split(",");
		minNum = Long.parseLong(limits[0]);
		maxNum = Long.parseLong(limits[1]);
		format = pattern;
		return getNum();
	}
	
//	@Test
//	public static void test(){
//		NumberGenerator gen=new NumberGenerator("0", "1000","$###.##");
//		System.out.println(gen.getNum());
//		System.out.println(gen.generate("0,100", "##.000"));
//	}
	
}
