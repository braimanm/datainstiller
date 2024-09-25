package com.braimanm.datainstiller.data;

@SuppressWarnings({"NewClassNamingConvention", "unused"})
public class Pers1 extends DataPersistence {
    @Data(alias = "string1")
    String s1;
    String s2;
    Pers2 pers2;
}
