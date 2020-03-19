package Project.Model;

import Project.DocumentRater;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Posting implements Serializable, WithdrawalObserver {

    private final static List<String> ALL_STATUSES = Arrays.asList("OPEN", "FILLED", "CLOSED");
    private final String[] interviewStages;
    private PostingInfo postingInfo;
    private ArrayList<Applicant> applicantPool = new ArrayList<>();
    private ArrayList<Applicant> hiredApplicants = new ArrayList<>();
    private String status = ALL_STATUSES.get(0);
    private ArrayList<Tag> tags = new ArrayList<>();
    private Requirements requirement;
    private DocumentRater rater;

    public Posting(PostingInfo postingInfo, String[] interviewStages) {
        this.postingInfo = postingInfo;
        this.interviewStages = interviewStages;
        this.requirement = new Requirements(this.postingInfo);
    }

    public Posting(PostingInfo postingInfo, String[] interviewStages,
                   ArrayList<Tag> tags) {
        this.postingInfo = postingInfo;
        this.interviewStages = interviewStages;
        this.applicantPool = new ArrayList<>();
        this.tags = tags;
        this.requirement = new Requirements(this.postingInfo);
    }

    public String getStatus() {
        return status;
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    // DO NOT DELETE!!!
    public int getAvailablePositions() {
        return this.getAmountHiring() - this.hiredApplicants.size();
    }

    public void addTags(ArrayList<Tag> listOfTags) {
        this.tags.addAll(listOfTags);
    }

    public void setRater(DocumentRater rater) {
        this.rater = rater;
    }

    private boolean raterExists() {
        return this.rater != null;
    }

    public void sort() {
        if (raterExists()) {
            rater.sort(this.applicantPool);
        }
    }

    public void sort(List<Applicant> apps) {
        if (raterExists()) {
            rater.sort(apps);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public ArrayList<Tag> getTags() {
        return this.tags;
    }

    // DO NOT DELETE!!!
    public String getName() {
        return postingInfo.getName();
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    // DO NOT DELETE!!!
    public String getDescription() {
        return postingInfo.getDescription();
    }

    private int getAmountHiring() {
        return postingInfo.getAmountHiring();
    }

    @SuppressWarnings("unused")
    // DO NOT DELETE!!!
    public LocalDate getDatePosted() {
        return postingInfo.getDatePosted();
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    // DO NOT DELETE!!!
    public LocalDate getDateClosed() {
        return postingInfo.getDateClosed();
    }

    String[] getInterviewStages() {
        return interviewStages.clone();
    }

    /**
     * add an applicant to the posting
     */
    public void addApplicant(Applicant applicant) {
        if (status.equalsIgnoreCase("OPEN")) {
            if (!(applicantPool.contains(applicant))) {
                applicantPool.add(applicant);
            }
        }
    }

    public void removeApplicant(Applicant applicant) {
        applicantPool.remove(applicant);
    }

    public ArrayList<Applicant> getApplicantPool() {
        return this.applicantPool;
    }

    public PostingInfo getPostingInfo() {
        return this.postingInfo;
    }

    public Requirements getRequirement() {
        return this.requirement;
    }

//    void removeTag(Project.Model.Tag tag){
//        tags.remove(tag);
//    }

    /**
     * return a list of the names of the applicants
     */
    List<String> getApplicantNames() {
        List<String> applicantNames = new ArrayList<>();
        for (Applicant applicant : applicantPool) {
            applicantNames.add(applicant.getUsername());
        }
        return applicantNames;
    }

    /**
     * change the status of the posting to CLOSED
     */
    public void closePosting() {
        this.status = ALL_STATUSES.get(2);
        this.postingInfo.setDateClosed(LocalDate.now());
    }

    /**
     * hire as many current applicants as possible
     */
    public void fillPosting(Applicant app) {
        if (this.hiredApplicants.size() != this.postingInfo.getAmountHiring()) {
            if (this.applicantPool.contains(app) && !this.hiredApplicants.contains(app)) {
                this.hiredApplicants.add(app);
                // the bottom line removes the applicant from the recommending list and the applicant's current
                // applications list
                app.withdraw(this);
            }
            if (this.hiredApplicants.size() == this.postingInfo.getAmountHiring()) {
                this.status = ALL_STATUSES.get(1);
            }
        } else {
            System.out.println("There are enough applicants have already been hired for this job.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Posting posting = (Posting) o;
        return Arrays.equals(interviewStages, posting.interviewStages) &&
                postingInfo.equals(posting.postingInfo) &&
                status.equals(posting.status);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(postingInfo, status);
        result = 31 * result + Arrays.hashCode(interviewStages);
        return result;
    }

    @Override
    public String toString() {
        return ("\t[" + this.status + "] " +
                this.postingInfo.toString() +
                "hired: " + getHiredApplicantNames()) + "There are " + getAvailablePositions() + " left"
                + "\n\t\t\t";
    }

    /**
     * get the names of the hired applicants
     */
    private String getHiredApplicantNames() {
        if (this.hiredApplicants.size() == 0) {
            return "None";
        } else {
            return this.hiredApplicants.toString();
        }
    }

    /**
     * update the instructions for application
     */
    public void updateRequirements() {
        this.getRequirement().addRequirements();
        System.out.println("Current requirement for application: ");
        System.out.println(this.getRequirement());
    }

    ArrayList getHiredApplicants() {
        return this.hiredApplicants;
    }

    /**
     * withdraw the applicant from the posting
     */
    @Override
    public void withdrawApplicant(Applicant app, Posting post) {
        if (this.equals(post)) {
            this.removeApplicant(app);
        }
    }
}