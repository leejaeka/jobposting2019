package Project.Manager;

import Project.Model.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that manage different types of user in the system
 */
public class UserManager implements Manager<User>, Serializable {
    private final List<User> users = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <T extends User> List<T> getUsers(String userType, Company company) {
        List<T> userGroup = new ArrayList<>();

        for (User u : this.users) {
            if (userType.equalsIgnoreCase("APPLICANTS")) {
                if (u instanceof Applicant) {
                    userGroup.add((T) u);
                }

            } else if (userType.equalsIgnoreCase("INTERVIEWERS")) {
                if (u instanceof Interviewer && ((Interviewer) u).getCompany().isSameCompany(company)) {
                    userGroup.add((T) u);
                }

            } else if (userType.equalsIgnoreCase("HR_COORDINATORS")) {
                if (u instanceof HRCoordinator && ((HRCoordinator) u).getCompany().isSameCompany(company)) {
                    userGroup.add((T) u);
                }
            } else {
                throw new IllegalArgumentException("Wrong user type");
            }
        }

        return userGroup;
    }

    public boolean authenticate(String username, String pw) {
        User u = get(username);
        return u != null && u.getPw().equalsIgnoreCase(pw);
    }

    @Override
    public List<User> get() {
        return users;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public List<String> getUsernames() {
        List<String> usernames = new ArrayList<>();

        for (User u : users) {
            usernames.add(u.getUsername());
        }

        return usernames;
    }

    @Override
    public void add(User user) {
        users.add(user);
    }

    @Override
    public String toString() {
        return "Applicants = " + getUsers("APPLICANTS");
    }

    @SuppressWarnings("unchecked")
    public <T extends User> List<T> getUsers(String userType) {
        List<T> userGroup = new ArrayList<>();

        for (User u : this.users) {
            if (userType.equalsIgnoreCase("APPLICANTS")) {
                if (u instanceof Applicant) {
                    userGroup.add((T) u);
                }

            } else if (userType.equalsIgnoreCase("INTERVIEWERS")) {
                throw new IllegalArgumentException("Missing Project.Model.Company argument");


            } else if (userType.equalsIgnoreCase("HR_COORDINATORS")) {
                throw new IllegalArgumentException("Missing Project.Model.Company argument");
            } else {
                throw new IllegalArgumentException("Wrong user type");
            }
        }

        return userGroup;
    }

    @SuppressWarnings("unchecked")
    public <T extends User> T get(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return (T) u;
            }
        }

        return null;
    }

}
