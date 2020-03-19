package Project.Model;

import Project.GUI.MenuGUI;

import java.io.Serializable;
import java.util.Objects;

public abstract class User implements Serializable {

    private final String username;
    private String pw;

    User(String username, String pw) {
        this.username = username;
        this.pw = pw;
    }

    public abstract MenuGUI getTUI();

    public String getUsername() {
        return username;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, pw);
    }

    /**
     * Users are considered the same when case ignored and username and password still match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equalsIgnoreCase(user.username) &&
                pw.equalsIgnoreCase(user.pw);
    }

    @Override
    public String toString() {
        return username + "@" + pw;
    }
}
