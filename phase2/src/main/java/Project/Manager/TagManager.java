package Project.Manager;

import Project.Model.Tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// a class that manage all the company
public class TagManager implements Manager<Tag>, Serializable {
    private final List<Tag> tags;

    public TagManager() {
        this.tags = new ArrayList<>();
    }

    @Override
    public List<Tag> get() {
        return this.tags;
    }

    public Tag get(String s) {
        for (Tag t : tags) {
            if (t.getName().equalsIgnoreCase(s)) {
                return t;
            }
        }

        return null;
    }

    @Override
    public void add(Tag tag) {
        this.tags.add(tag);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("Tags = [");

        for (Tag t : tags) {
            output.append("@").append(t.getName()).append(" ");
        }
        return output.append("]").toString();
    }
}

