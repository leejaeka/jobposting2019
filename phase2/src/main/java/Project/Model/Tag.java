package Project.Model;

import java.io.Serializable;
import java.util.Objects;

public class Tag implements Serializable {
    private String name;

    public Tag(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return tag.name.toLowerCase().equals(this.name.toLowerCase());
    }

    public String getName() {
        return name;
    }

    //    ArrayList<String> filter(String keyword){
//        return
//    }
}
