package Project.Manager;

import java.util.List;

public interface Manager<T> {
    List<T> get();

    void add(T obj);
}
