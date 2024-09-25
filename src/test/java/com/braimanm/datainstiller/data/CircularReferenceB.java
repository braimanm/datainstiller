package com.braimanm.datainstiller.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

@SuppressWarnings({"NewClassNamingConvention", "unused"})
@XStreamAlias("CLASS-B")
public class CircularReferenceB extends DataPersistence {
    String someOtherValue;
    CircularReferenceA classA;
    CircularReferenceA[] arrayOfClassA;
    List<CircularReferenceA> listOfClassA;
    CircularReferenceA classA2;
}
