package Project.Manager;

import Project.Model.Company;
import Project.Model.Posting;
import Project.Model.Tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that manage all the postings in the system
 */
public class PostingManager implements Manager<Posting>, Serializable {

    private final List<Posting> postings;

    public PostingManager() {
        this.postings = new ArrayList<>();
    }

    /**
     * find the posting object with name postingName
     */
    public Posting find(String postingName) {
        for (Posting posting : postings) {
            if (posting.getPostingInfo().getName().equalsIgnoreCase(postingName)) {
                return posting;
            }
        }
        return null;
    }

    /**
     * return list of Postings from a specific Project.Model.Company
     * with a filter option
     */
    public List<Posting> get(Company c, ArrayList<Tag> tags) {
        List<Posting> postingFromCompany = new ArrayList<>();
        for (Posting p : this.postings) {
            if (p.getPostingInfo().getCompany().isSameCompany(c)) {
                postingFromCompany.add(p);
            }
        }
        return filteredPostings(postingFromCompany, tags);
    }

    private List<Posting> filteredPostings(List<Posting> postings, ArrayList<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return postings;
        } else {
            for (Posting p : postings) {
                if (p.getTags() != null && !tags.containsAll(p.getTags())) {
                    postings.remove(p);
                }
            }
            return postings;
        }
    }

    @Override
    public List<Posting> get() {
        return this.postings;
    }

    @Override
    public void add(Posting posting) {
        this.postings.add(posting);
    }

    @Override
    public String toString() {
        return "Postings = " + postings;
    }

}
