package Project.GUI;

import Project.Main;
import Project.Model.*;
import Project.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the Employee TUI (text-based user interface)
 */
abstract class EmployeeGUI<T extends Employee> extends MenuGUI<T> {

    EmployeeGUI(T user) {
        this.user = user;
    }

    /**
     * Select posting from a company
     */
    Posting selectPosting(Company company) {
        List<Posting> postingFromCompany = Main.postings.get(company, null);
        return selectPosting(postingFromCompany);
    }

    /**
     * Select posting from a list
     */
    Posting selectPosting(List<Posting> postings) {
        System.out.println();

        if (postings != null && !postings.isEmpty()) {
            int count = 1;

            for (Posting p : postings) {
                System.out.println("(" + count + ") " + p.getPostingInfo().getName());
                count++;
            }
            System.out.print("Select a posting: ");
            int postingResponse = Util.promptNumbers(postings.size());
            Posting post = postings.get(postingResponse - 1);
            post.sort();
            return post;

        } else {
            System.out.print("No posting is available");
            return null;
        }
    }

    /**
     * Select an applicant and view their documents
     */
    public void viewApplicants(Posting selectedPosting) {
        if (selectedPosting != null) {
            selectedPosting.sort();
            ArrayList<Applicant> applicantPool = selectedPosting.getApplicantPool();

            if (applicantPool != null && !applicantPool.isEmpty()) {
                Applicant a = selectUser(applicantPool);

                System.out.println();

                ArrayList<String> listOfDocuments = a.getListOfDocumentNames();
                for (String documentName : listOfDocuments) {
                    viewDocument(a, documentName);
                    System.out.println();
                }

            } else {
                System.out.println("No applicant available");
            }

        }
    }

    <T extends User> T selectUser(List<T> listOfUser) {
        System.out.println();
        // For use when printing user type literal
        String userType = "user";

        if (listOfUser != null && !listOfUser.isEmpty()) {
            // Check which type of user List<T extends User> listOfUser contains
            if (listOfUser.get(0) instanceof Applicant) {
                userType = "applicant";
            } else if (listOfUser.get(0) instanceof Interviewer) {
                userType = "interviewer";
            } else if (listOfUser.get(0) instanceof HRCoordinator) {
                userType = "HR coordinator";
            }

            int count = 1;

            for (T user : listOfUser) {
                System.out.println("(" + count + ") " + user.getUsername());
                count++;
            }

            System.out.print("Select a " + userType + ": ");
            int response = Util.promptNumbers(listOfUser.size());
            return response <= listOfUser.size() ? listOfUser.get(response - 1) : null;

        } else {
            System.err.println("There is currently no " + userType + "s available");
            return null;
        }
    }

    private void viewDocument(Applicant selectedApplicant, String documentName) {
        String document = selectedApplicant.getDocument(documentName);

        if (document == null) {
            System.out.println("The applicant does not have a " + documentName);

        } else {
            System.out.print(documentName.substring(0, 1).toUpperCase() + documentName.substring(1) + ": " + document);
        }
    }

    public Applicant selectApplicant() {
        return selectUser(Main.users.getUsers("APPLICANTS"));
    }

    Interviewer selectInterviewer() {
        return selectUser(Main.users.getUsers("INTERVIEWERS", user.getCompany()));
    }

    HRCoordinator selectHRCoordinator() {
        return selectUser(Main.users.getUsers("HR_COORDINATORS", user.getCompany()));
    }

}
