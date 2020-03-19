package Project.Model;

import Project.GUI.InterviewerGUI;
import Project.Main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Interviewer extends Employee implements Serializable, WithdrawalObserver {

    public final HashMap<Posting, ArrayList<Applicant>> recommendation = new HashMap<>();
    public final HashMap<Posting, ArrayList<Applicant>> applicantsInterviewing = new HashMap<>();

    public Interviewer(String username, String pw, Company company) {
        super(username, pw, company);
    }

    /**
     * add interview applicant
     */
    public void addInterviewingApplicant(Applicant applicant, Posting posting) {
        if (!this.applicantsInterviewing.containsKey(posting)) {
            addInterviewPosting(posting);
        }
        if (!applicantsInterviewing.get(posting).contains(applicant)) {
            applicantsInterviewing.get(posting).add(applicant);
            applicant.addObserver(this);
        }
    }

    /**
     * add recommended applicant
     */
    public void addRecommendedApplicant(Applicant app, Posting post) {
        addRecommendedPosting(post);
        if (!recommendation.get(post).contains(app)) {
            recommendation.get(post).add(app);
            applicantsInterviewing.get(post).remove(app);
            System.out.println(app.getUsername() + "has been added to the recommending list and removed from" +
                    "the interviewing list");
        }
    }

    /**
     * add interview posting
     */
    private void addInterviewPosting(Posting posting) {
        if (!applicantsInterviewing.containsKey(posting)) {
            applicantsInterviewing.put(posting, new ArrayList<>());
        }
    }

    /**
     * add recommended posting
     */
    private void addRecommendedPosting(Posting posting) {
        if (!recommendation.containsKey(posting)) {
            recommendation.put(posting, new ArrayList<>());
        }
    }

    /**
     * withdraw an applicant from the posting
     */
    @Override
    public void withdrawApplicant(Applicant app, Posting post) {
        if (recommendation.containsKey(post)) {
            recommendation.get(post).remove(app);
        }
        if (applicantsInterviewing.containsKey(post)) {
            applicantsInterviewing.get(post).remove(app);
        }
    }


    @Override
    public InterviewerGUI getTUI() {
        InterviewerGUI gui = new InterviewerGUI(this);

        // Observable pattern
        List<Applicant> applicants = Main.users.getUsers("APPLICANTS");
        for (Applicant applicant : applicants) {
            gui.addObserver(applicant);
        }

        return gui;
    }

}
