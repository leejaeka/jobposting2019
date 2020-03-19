package Project.Model;

/**
 * Class will be used in the future, for access to the system, where the class should not be a regular user
 */
public abstract class Employee extends User {
    private Company company;

    Employee(String username, String pw, Company company) {
        super(username, pw);
        this.company = company;

        company.addNewEmployee(this);
    }

    /**
     * returns whether or not this employee is a coworker of another
     */
    public boolean isCoworker(Employee otherEmployee) {
        return this.getCompany().isSameCompany(otherEmployee.getCompany());
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

}
