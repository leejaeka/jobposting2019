package Project.Model;

import Project.GUI.HRCoordinatorGUI;
import Project.Main;

import java.io.Serializable;
import java.util.List;

public class HRCoordinator extends Employee implements Serializable {

//    final static HashMap<Posting, ArrayList<Applicant>> RECOMMENDED = new HashMap<>();

    public HRCoordinator(String username, String pw, Company company) {
        super(username, pw, company);
    }

    public void matchInterviewer(Applicant applicant, Interviewer interviewer, Posting posting) {
        interviewer.addInterviewingApplicant(applicant, posting);
    }

    @Override
    public HRCoordinatorGUI getTUI() {
        HRCoordinatorGUI gui = new HRCoordinatorGUI(this);

        // Observable pattern
        List<Applicant> applicants = Main.users.getUsers("APPLICANTS");
        for (Applicant applicant : applicants) {
            gui.addObserver(applicant);
        }

        return gui;
    }

}
