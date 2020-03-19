package Project;

import Project.Model.Applicant;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DocumentRater implements Comparator<Applicant> {
    /***
     * Gives a rating to the applicants documents to mke it easier for coordinators to choose who to hire/interview
     */
    private String[] keywords;

    public DocumentRater(String[] keywords) {
        this.keywords = keywords;
    }

    /***
     * Checks if the reume has certain keywords
     * @param resume
     * @return score
     */
    private int rateResume(String resume) {
        int i = 0;
        for (String word : keywords) {
            if (resume.toLowerCase().contains(word.toLowerCase())) {
                i++;
            }
        }
        return Math.round(i * 100 / keywords.length);
    }

    /***
     * Checks if he document is formatted well
     * @param coverLetter
     * @return score
     */
    private int rateCoverLetter(String coverLetter) {
        int i = 0;
        if (coverLetter.matches("^Dear.*")) {
            i++;
        }
        if (coverLetter.matches(".*\n(\\w),\n.*")) {
            i++;
        }
        return i * 30;
    }

    private int rateDocuments(Applicant app) {
        return rateCoverLetter(app.getCoverLetter()) + rateResume(app.getResume());
    }

    @Override
    public int compare(Applicant a, Applicant b) {
        return rateDocuments(a) - rateDocuments(b);
    }

    /***
     * sorts the list according to score in descending order (highest score first)
     * @param apps
     */
    public void sort(List<Applicant> apps) {
        apps.sort(this);
        Collections.reverse(apps);
    }


}
