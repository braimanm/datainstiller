package com.braimanm.datainstiller.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@SuppressWarnings({"NewClassNamingConvention", "unused"})
@XStreamAlias("CLASS-A")
public class CircularReferenceA extends DataPersistence {
    String someValue;
    CircularReferenceB classB;
}
