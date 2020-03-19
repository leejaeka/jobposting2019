package Project.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

// HR coordinator can create need to create a company before it can create posting)
public class Company implements Serializable, WithdrawalObserver {
    private ArrayList<Employee> employees;
    private String name;
    private String location;
    private HashMap<Posting, ArrayList<Applicant>> recommended;

    public Company(String name, String location) {
        this.name = name;
        this.location = location;
        this.employees = new ArrayList<>();
        this.recommended = new HashMap<>();
    }

    @Override
    public String toString() {
        return name + "@" + location;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public Boolean isSameCompany(Company company) {
        return this.name.equalsIgnoreCase(company.name);
    }

    Boolean isSameLocation(Company company) {
        return this.location.equalsIgnoreCase(company.location);
    }

    public HashMap<Posting, ArrayList<Applicant>> getRecommended() {
        return this.recommended;
    }

    void addNewEmployee(Employee employee) {
        this.employees.add(employee);
    }

    /**
     * adds a job posting to the company
     */
    private void addPosting(Posting posting) {
        if (!recommended.containsKey(posting)) {
            recommended.put(posting, new ArrayList<>());
        }
    }

    /**
     * adds a recommended applicant
     */
    private void addRecommendedApplicant(Applicant applicant, Posting posting) {
        if (!recommended.get(posting).contains(applicant)) {
            recommended.get(posting).add(applicant);
            applicant.addObserver(this);
        }
    }

    /**
     * adds ALL recommended applicants
     */
    public void addAllRecommendedApplicants(HashMap<Posting, ArrayList<Applicant>> suggested) {
        for (Posting posting : suggested.keySet()) {
            addPosting(posting);
            System.out.println(posting + "has been added to company recommended list");
            for (Applicant app : suggested.get(posting)) {
                addRecommendedApplicant(app, posting);
                System.out.println(app.getUsername() + "has been added to the reccommended list of posting:" +
                        posting);
            }
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(employees, name, location);
    }

//    public HashMap<Project.Model.Posting, ArrayList<Project.Model.Applicant>> getRecommended() {
//        return recommended;
//    }
//    void addRecommendedApplicants(HashMap<Project.Model.Posting, ArrayList<Project.Model.Applicant>> applicants) {
//        for (Project.Model.Posting posting : applicants.keySet()) {
//            if (!recommended.containsKey(posting)) {
//                recommended.put(posting, new ArrayList<>());
//            }
//
//            for (Project.Model.Applicant applicant : applicants.get(posting)) {
//                if (!recommended.get(posting).contains(applicant)) { // if the applicant had already recommended, skip
//                    recommended.get(posting).add(applicant); // otherwise, add the applicant into the list
//                }
//            }
//        }
//    }

    /**
     * withdraws applicant from the posting
     */
    @Override
    public void withdrawApplicant(Applicant app, Posting post) {
        if (recommended.containsKey(post)) {
            recommended.get(post).remove(app);
        }
    }

    /**
     * Companies are considered the same when case ignored and the name and location still match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return name.equalsIgnoreCase(company.name) &&
                location.equalsIgnoreCase(company.location);
    }
}

