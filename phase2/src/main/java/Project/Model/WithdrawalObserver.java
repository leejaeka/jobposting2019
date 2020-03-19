package Project.Model;

interface WithdrawalObserver {
    /***
     * Lets the applicant class use the observer design pattern to remove itself from other objects when the
     * applicant withdraws the application
     */
    void withdrawApplicant(Applicant applicant, Posting posting);
}
