package datainstiller.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("retain")
public class RetainData extends DataPersistence {
    byte b;
    short sh;
    int i;
    long l;
    float f;
    double d;
    char c;
    boolean bo;
    String s;
    byte bb = 1;
    short shsh = 2;
    int ii = 3;
    long ll = 4L;
    float ff = 0.1f;
    double dd = 0.2d;
    char cc = 'A';
    boolean bobo = true;
    String ss = "String";

    @Data(skip = true)
    byte bskip = 45;
    static byte bstat = 55;
    static final byte bstatfinal = 65;

}
