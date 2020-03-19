package Project.Model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class PostingInfo implements Serializable {
    private String name;
    private String description;
    private int amountHiring;
    private LocalDate datePosted;
    private LocalDate dateClosed;
    private Company company;

    public PostingInfo(String name, String description, int amountHiring, LocalDate datePosted, LocalDate dateClosed, Company company) {
        this.name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        this.description = description;
        this.amountHiring = amountHiring;
        this.datePosted = datePosted;
        this.dateClosed = dateClosed;
        this.company = company;
    }

    LocalDate getCloseDate() {
        return dateClosed;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    public LocalDate getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(LocalDate datePosted) {
        this.datePosted = datePosted;
    }

    int getAmountHiring() {
        return this.amountHiring;
    }

    public void setAmountHiring(int newAmount) {
        this.amountHiring = newAmount;
    }

    LocalDate getDateClosed() {
        return this.dateClosed;
    }

    public void setDateClosed(LocalDate newDate) {
        this.dateClosed = newDate;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company newCompany) {
        this.company = newCompany;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostingInfo that = (PostingInfo) o;
        return amountHiring == that.amountHiring &&
                name.equals(that.name) &&
                description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, amountHiring);
    }

    @Override
    public String toString() {
        return getName() + ": " + this.description + "; " +
                "posted on " + datePosted + "; " +
                "ends on " + dateClosed + "; ";
    }
}
