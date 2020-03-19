package Project.Model;

import Project.GUI.ApplicantGUI;
import Project.Main;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;


/**
 * Project.Model.Applicant user
 */
public class Applicant extends User implements Serializable, Observer {

    private final static String RESUME = "resume";
    private final static String COVER_LETTER = "cover letter";
    private final HashMap<String, String> documents;
    // stores the current Application mapped with its Project.Model.Interview object
    private final HashMap<Posting, Interview> currentApplications;
    private final ArrayList<Posting> pastApplications;
    private final LocalDate dateCreated;
    public LocalDate dateClosed;
    private ArrayList<WithdrawalObserver> withdrawalObservers = new ArrayList<>();
    private LinkedList<String> notifications;

    /**
     * Constructor for applicant user
     *
     * @param username username
     * @param pw       password
     */
    public Applicant(String username, String pw) {
        super(username, pw);

        // To add a new document type to the list, create a final static variable then put it to the documents HashMap.
        documents = new HashMap<String, String>() {{
            put(COVER_LETTER, null);
            put(RESUME, null);
        }};

        pastApplications = new ArrayList<>();
        dateCreated = LocalDate.now();
        currentApplications = new HashMap<>();
        notifications = new LinkedList<>();
    }


    @Override
    public void update(Observable o, Object arg) {
        if (((List) arg).get(0).equals(this)) {
            notifications.add((String) ((List) arg).get(1));
        }
    }

    public LinkedList<String> getNotifications() {
        return notifications;
    }

    /**
     * Get all applications in progress
     *
     * @return current applications
     */
    public HashMap<Posting, Interview> getCurrentApplications() {
        return currentApplications;
    }

    public String getResume() {
        return documents.get(RESUME);
    }

    public String getCoverLetter() {
        return documents.get(COVER_LETTER);
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public LocalDate getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(LocalDate dateClosed) {
        this.dateClosed = dateClosed;
    }

    public ArrayList<Posting> getPastApplications() {
        return pastApplications;
    }

    @Override
    public ApplicantGUI getTUI() {
        return new ApplicantGUI(this);
    }

    /**
     * Withdraw from an application process
     */
    public void withdraw(Posting posting) {
        boolean flag = true;
        for (Posting posting1 : pastApplications) {
            if (posting.getName().equalsIgnoreCase(posting1.getName())) {
                flag = false;
                break;
            }
        }
        if (flag) {
            pastApplications.add(posting);
        }
        currentApplications.remove(posting);
        withdrawFromObservers(posting);
    }

    /**
     * Add observers (Project.Model.Company, Project.Model.Interviewer, Project.Model.Posting)
     *
     * @param observer observer
     */
    void addObserver(WithdrawalObserver observer) {
        withdrawalObservers.add(observer);
    }

    /**
     * withdraw observers (Project.Model.Company, Project.Model.Interviewer, Project.Model.Posting)
     *
     * @param posting this posting
     */
    private void withdrawFromObservers(Posting posting) {
        for (WithdrawalObserver observer : withdrawalObservers) {
            observer.withdrawApplicant(this, posting);
        }
    }

    /**
     * Return the date interview completed. The second parameter indicates which interview stage is interested
     *
     * @param stageNum interview stage number
     * @return date of indicated interview
     */
    LocalDate getInterviewStageDate(Posting posting, int stageNum) {
        return currentApplications.get(posting).getStageDate(stageNum);
    }

    /**
     * set interviewed date
     *
     * @param posting  for this posting
     * @param stageNum for this stage of interview
     * @param newDate  to this date
     */
    public void setInterviewStageDate(Posting posting, int stageNum, LocalDate newDate) {
        currentApplications.get(posting).setStageDate(stageNum, newDate);
    }

    /**
     * Get list of all the document names
     *
     * @return document names
     */
    public ArrayList<String> getListOfDocumentNames() {
        return new ArrayList<>(this.documents.keySet());
    }

    /**
     * Return whether the applicant has uploaded ALL the documents
     */
    public boolean hasUploadedAllDocuments() {
        for (String document : documents.values()) {
            if (document == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the specific document
     *
     * @param documentName given this name
     * @return document
     */
    public String getDocument(String documentName) {
        return this.documents.get(documentName.toLowerCase());
    }

    /**
     * If last job application closed after 30 days, applicants CV and
     * cover letter should automatically be removed from the system
     */
    public void checkLastApplication() {
        if (exceedsThirtyDays()) {
            removeDocuments();
        }
    }

    /**
     * @return true if 30+ days, otherwise false
     */
    private boolean exceedsThirtyDays() {
        if (!this.currentApplications.isEmpty()) {
            return false;

        } else {
            LocalDate current = LocalDate.now();

            if (dateClosed != null) {
                return dateClosed.isBefore(current.minusDays(30));
            }

            return false;
        }
    }

    /**
     * remove all documents
     */
    private void removeDocuments() {
        removeDocument(Applicant.COVER_LETTER);
        removeDocument(Applicant.RESUME);
    }

    /**
     * remove specific document
     *
     * @param documentName this document
     */
    private void removeDocument(String documentName) {
        setDocument(documentName, null);
    }

    public void setDocument(String documentName, String doc) {
        this.documents.put(documentName.toLowerCase(), doc.trim());
    }

    /**
     * Automatically check every posting if it expired. Add/remove accordingly
     */
    public void checkClosed() {
        for (Posting posting : currentApplications.keySet()) {
            if (posting.getPostingInfo().getDateClosed() != null) {

                // If posting closed already
                if (LocalDate.now().isAfter(posting.getPostingInfo().getDateClosed())) {
                    boolean flag = true;
                    for (Posting posting1 : pastApplications) {
                        if (posting.getName().equalsIgnoreCase(posting1.getName())) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        pastApplications.add(posting);
                    }
                    currentApplications.remove(posting);

                    // If dateClosed was before last dateClosed saved, make dateClosed latest date closed
                    if (dateClosed.isBefore(posting.getPostingInfo().getDateClosed())) {
                        dateClosed = posting.getPostingInfo().getDateClosed();
                    }
                }
            }
        }
    }

    /**
     * Add a posting to current Job list
     *
     * @param posting this posting
     */
    public void addPosting(Posting posting) {
        addObserver(posting);
        currentApplications.put(posting, new Interview(posting.getInterviewStages()));
    }


    /**
     * Prints out status of this applicant in posting postingName
     */
    void getStatus(String postingName) {
        //Precondition: postingName is the name of a posting that this applicant has already applied for.
        Posting posting = Main.postings.find(postingName);
        if (posting != null) {
            if (pastApplications.contains(posting))
                System.out.println("Sorry you have not been accepted into this job! Better luck next time!");
            if (currentApplications.keySet().contains((posting))) {
                if (posting.getApplicantPool().contains(this)) {
                    if (posting.getHiredApplicants().contains(this)) {
                        System.out.println("Congratulations you have been hired!");
                    } else {
                        System.out.println("You have passed the previous interview stage!");
                    }
                } else {
                    System.out.println("Sorry, you have not passed the interview process and have" +
                            " been declined a job offer at this company!");
                }
            }
        }
    }

    /**
     * Apply for job
     *
     * @param posting for this
     */
    public void applyJob(Posting posting) {
        posting.addApplicant(this);
        this.addPosting(posting);
    }
}
