package com.braimanm.datainstiller.data;

import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamConverter(MoodConverter.class)
public interface MoodInterface {
	public String getMood();
	public String getValue();
	public void init(String mood,String value);
}
