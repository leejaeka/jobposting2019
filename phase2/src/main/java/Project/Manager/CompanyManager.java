package Project.Manager;

import Project.Model.Company;
import Project.Model.Employee;
import Project.Model.HRCoordinator;
import Project.Model.Interviewer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// a class that manage all the company
public class CompanyManager implements Manager<Company>, Serializable {
    private final List<Company> companies;

    public CompanyManager() {
        this.companies = new ArrayList<>();
    }

    @Override
    public List<Company> get() {
        return this.companies;
    }

    @Override
    public void add(Company company) {
        this.companies.add(company);
    }

    public List<Company> getSameCompany(Company company) {
        List<Company> sameCompanies = new ArrayList<>();
        for (Company c : this.companies) {
            if (c.isSameCompany(company)) {
                sameCompanies.add(c);
            }
        }
        return sameCompanies;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("Companies = [");

        for (Company c : companies) {
            List<Employee> employees = c.getEmployees();
            List<Interviewer> interviewer = new ArrayList<>();
            List<HRCoordinator> hrCoordinator = new ArrayList<>();

            for (Employee e : employees) {
                if (e instanceof Interviewer) {
                    interviewer.add((Interviewer) e);
                } else {
                    hrCoordinator.add((HRCoordinator) e);
                }
            }

            output.append("\t{").append(c).append(", interviewer = ").append(interviewer).
                    append(", coordinator = ").append(hrCoordinator).append("}\n\t\t\t");
        }

        return output.append("]").toString();
    }
}

