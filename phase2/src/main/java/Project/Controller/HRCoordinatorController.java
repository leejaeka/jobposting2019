package Project.Controller;

import Project.Main;
import Project.Model.Applicant;
import Project.Model.Company;
import Project.Model.Interviewer;
import Project.Model.Posting;

public class HRCoordinatorController {
    /**
     * Project.Controller for Project.GUI.HRCoordinatorGUI
     */

    public HRCoordinatorController() {
    }

    public void hire(Applicant applicant, Posting posting) {
        posting.fillPosting(applicant);
    }

    /**
     * matches an interviewer with an applicant in this posting
     */
    public void matchInterviewer(Interviewer interviewer, Applicant applicant, Posting posting) {
        interviewer.addInterviewingApplicant(applicant, posting);
    }

    /**
     * return whether or not postings exist in this company
     */
    public boolean postingsExist(Company company) {
        return !Main.postings.get(company, null).isEmpty();
    }

}
