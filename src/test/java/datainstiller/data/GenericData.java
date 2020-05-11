package datainstiller.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import javax.swing.text.html.parser.Entity;
import java.util.List;

@XStreamAlias("generic-data")
public class GenericData extends DataPersistence {
    List<Entity> entities;

    @XStreamAlias("entity")
    private static class Entity {
        String name;
        Integer age;
        String skill;
    }

}
