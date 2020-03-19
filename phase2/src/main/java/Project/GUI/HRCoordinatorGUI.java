package Project.GUI;

import Project.Controller.HRCoordinatorController;
import Project.DocumentRater;
import Project.Main;
import Project.Model.*;
import Project.Util;
import javafx.scene.Scene;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;


/**
 * Handles the HR Coordinator TUI (text-based user interface)
 */
public class HRCoordinatorGUI extends EmployeeGUI<HRCoordinator> {

    private HRCoordinatorController c = new HRCoordinatorController();

    public HRCoordinatorGUI(HRCoordinator hrCoordinator) {
        super(hrCoordinator);
    }

    /**
     * display menu
     *
     * @return menu scene
     */
    @Override
    public Scene getMenu() {
        addOptionsDisplay(Arrays.asList(
                "View/edit posting(s)",
                "Create job posting(s)",
                "Match interviewer"));

        getOption(0).setOnAction(event -> {
            Main.stage.close();
            currentPostings();
            Main.stage.show();

        });
        getOption(1).setOnAction(event -> {
            Main.stage.close();
            createPostings();
            Main.stage.show();

        });
        getOption(2).setOnAction(event -> {
            Main.stage.close();
            matchInterviewerGUI();
            Main.stage.show();

        });
        return new Scene(createOptionsGrid(), width, height);
    }

    /**
     * display all postings
     */
    private void currentPostings() {

        if (Main.postings.get(user.getCompany(), null).isEmpty()) {
            System.out.println("There are currently no postings");

        } else {
            System.out.println();
            Posting selectedPosting = selectPosting(user.getCompany());

            int response = Util.numericalMenu("Select an option", Arrays.asList(
                    "View applicants",
                    "View Recommendations",
                    "Edit Project.Model.Posting"));

            switch (response) {
                case 1:
                    viewApplicants(selectedPosting);
                    break;
                case 2:
                    viewRecommendations(selectedPosting);
                    break;
                case 3:
                    editPosting(selectedPosting);
            }
        }
    }

    /**
     * create bot that rates document
     *
     * @return
     */
    private DocumentRater createDocumentRater() {
        if (Util.booleanMenu("Would you like to add a Document Rater to this posting? (Applicants will be " +
                " automatically prioritized and listed in order of descending priority)")) {
            System.out.println("Please type in keywords for the posting, each one separated by a comma (,)");
            String[] keywords = Util.next().split(",");
            return new DocumentRater(keywords);
        }
        return null;
    }

    /**
     * Create new posting
     */
    private void createPostings() {
        System.out.print("Enter the name of the job : ");
        String name = Util.next();

        System.out.print("Enter the description: ");
        String description = Util.next();
        ArrayList<Tag> tags = new ArrayList<>();
        if (Util.booleanMenu("Would you like to add tags to this posting? (Can add some later)")) {
            if (!Main.tags.get().isEmpty()) {
                System.out.println("Here are some already existing tags: ");
                for (Tag tag : Main.tags.get()) {
                    System.out.print("@" + tag.getName() + " ");
                }
                System.out.println("\n");
            }
            if (Util.booleanMenu("Don't see what you're looking for? Would you like to create new tag(s)?")) {
                tags.addAll(createTags());
            } else {
                System.out.print("Please type the tag in form '@name' to add or type '@!' to quit adding: ");
                Scanner reader = new Scanner(System.in);
                while (true) {
                    String readerString = reader.nextLine();
                    if (!Pattern.matches("@!", readerString)) {
                        if (Pattern.matches("@[a-zA-Z_0-9]+", readerString)) {
                            for (Tag tag : Main.tags.get()) {
                                if (tag.getName().equals(readerString.substring(1))) {
                                    tags.add(tag);
                                }
                            }
                            System.out.println("Successfully added tag: @" + readerString.substring(1));
                            if (!Util.booleanMenu("Would you like to add more? (Can add more later)")) {
                                break;
                            } else {
                                System.out.print("Please type tag in form '@name' or type '@!' to quit: ");
                            }
                        } else {
                            System.out.print("Invalid input please try again");
                            System.out.println("Remember to leave no spacings between characters!");
                            System.out.print("Please type tag in form '@name' or type '@!' to quit: ");
                        }
                    }
                }
            }
        }

        System.out.print("Enter how many people are required for the job: ");
        int amountHiring = Util.promptNumbers(999);

        LocalDate postDate = LocalDate.now();
        LocalDate closeDate = Util.promptDates("Enter the close date of the post");

        String[] interviewStages = Util.multipleString("Interview Stages");
        PostingInfo postingInfo = new PostingInfo(name, description, amountHiring, postDate, closeDate, this.user.getCompany());
        Posting newPosting = new Posting(postingInfo, interviewStages);
        if (interviewStages.length != 0) {
            System.out.println("Successfully added " + interviewStages.length + " stages");
        }
        DocumentRater rater = createDocumentRater();
        if (rater != null) {
            newPosting.setRater(rater);
        }
        if (!tags.isEmpty()) {
            newPosting.addTags(tags);
        }
        Main.postings.get().add(newPosting);
        this.user.getCompany().getRecommended().put(newPosting, new ArrayList<>());// all job will be added to the recommended map when created

        //Each posting have different requirements
        newPosting.updateRequirements();
        System.out.println("You have added a new posting\n");
    }

    /**
     * match interviewer with an applicant
     */
    private void matchInterviewerGUI() {
        System.out.println();

        Posting selectedPosting = selectPosting(user.getCompany());
        Interviewer selectedInterviewer = selectInterviewer();
        Applicant selectedApplicant = selectUser(selectedPosting.getApplicantPool());

        if (selectedInterviewer != null && selectedApplicant != null) {
            if (selectedApplicant.getCurrentApplications().get(selectedPosting).getCurrentStageNum()
            == selectedApplicant.getCurrentApplications().get(selectedPosting).getStages().size()) {
                selectedPosting.getApplicantPool().remove(selectedApplicant);
            }
            c.matchInterviewer(selectedInterviewer, selectedApplicant, selectedPosting);
            System.out.println("\n" + selectedApplicant.getUsername() + " has been matched with " +
                    selectedInterviewer.getUsername() + " for the job " + selectedPosting.getPostingInfo().getName());

        } else {
            System.out.println("Matching failed");
        }
    }

    /**
     * Create new tags
     *
     * @return arraylist of tags
     */
    private ArrayList<Tag> createTags() {
        ArrayList<Tag> tags = new ArrayList<>();
        boolean flag = false;
        System.out.print("Please type new tag in form '@name' or type '@!' to quit: ");
        Scanner reader = new Scanner(System.in);
        while (true) {
            String readerString = reader.nextLine();
            if (!Pattern.matches("@!", readerString)) {
                if (Pattern.matches("@[a-zA-Z_0-9]+", readerString)) {
                    for (Tag tag : Main.tags.get()) {
                        if (tag.getName().toLowerCase().equals(readerString.toLowerCase())) {
                            System.out.println("Such tag already existed. Please try a different tag");
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        Tag newTag = new Tag(reader.toString().substring(1));
                        tags.add(newTag);
                        Main.tags.get().add(newTag);
                        System.out.println("Successfully added new tag: " + readerString.substring(1));
                        if (!Util.booleanMenu("Would you like to add more? (Can add more later)")) {
                            break;
                        }
                        System.out.print("Please type new tag in form '@name' or type '@!' to quit: ");
                    }
                } else {
                    System.out.println("Invalid input please try again");
                    System.out.print("Please type new tag in form '@name' or type '@!' to quit: ");
                }
            }
        }
        System.out.println("You have added " + tags.size() + " tags in total!");
        return tags;
    }

    /**
     * view applicants for selected posting
     *
     * @param selectedPosting this posting
     */
    public void viewApplicants(Posting selectedPosting) {
        if (!selectedPosting.getApplicantPool().isEmpty()) {
            int count = 1;

            for (Applicant applicant : selectedPosting.getApplicantPool()) {
                System.out.println("(" + count + ") " + applicant.getUsername());
                count++;
            }
            System.out.print("\nSelect the applicant you'd like to view: ");
            int response = Util.promptNumbers(selectedPosting.getApplicantPool().size());
            Applicant selectedApplicant = selectedPosting.getApplicantPool().get(response - 1);

            ArrayList<String> listOfDocuments = selectedApplicant.getListOfDocumentNames();
            for (String documentName : listOfDocuments) {
                viewDocument(selectedApplicant, documentName);
                System.out.println();
            }

        } else {
            System.out.println("No applicant has applied for this job");
            this.currentPostings();
        }
    }

    /**
     * view applicants in my recommendation list
     *
     * @param selectedPosting this posting
     */
    private void viewRecommendations(Posting selectedPosting) {
        if (this.user.getCompany().getRecommended().get(selectedPosting) == null) {
            System.out.println("There is no applicant in the recommended list.");

        } else {
            System.out.println("The following applicants are recommended: ");
            int count = 0;
            for (Applicant applicant : this.user.getCompany().getRecommended().get(selectedPosting)) {
                System.out.println("(" + count + ") " + applicant.getUsername() + "\n");
            }
        }
    }

    /**
     * edit existing posting
     *
     * @param selectedPosting this posting
     */
    private void editPosting(Posting selectedPosting) {
        int response = Util.numericalMenu("Select an option", Arrays.asList(
                "Fill posting",
                "Close posting",
                "Edit posting"), false, true);
        switch (response) {
            case 1:
                hireApplicant(selectedPosting);
                break;
            case 2:
                selectedPosting.closePosting();
                System.out.println("The posting is now closed");
                break;
            case 3:
                int responseField = Util.numericalMenu("Which field would you like to update", Arrays.asList(
                        "Name",
                        "Description",
                        "AmountHiring",
                        "CloseDate",
                        "Company",
                        "Tag",
                        "Requirement"));
                updateField(responseField, selectedPosting);
        }
    }

    /**
     * Hire applicant
     *
     * @param selectedPosting this applicant
     */
    private void hireApplicant(Posting selectedPosting) {
        System.out.println("Who would you like to fill the posting with");
        Applicant selectedApplicant = getRecommendedApplicant(selectedPosting);
        if (selectedApplicant == null) {
            System.out.println("There is currently no one qualified for the job.");
        } else {
            selectedPosting.fillPosting(selectedApplicant);
            System.out.println(selectedApplicant.getUsername() + " has been hired");
            System.out.println("There are " + selectedPosting.getAvailablePositions() + " positions left");

            // Observable pattern
            setChanged();
            List args = Arrays.asList(selectedApplicant, "Congratulation!  You've been hired for " +
                    selectedPosting + "!");
            notifyObservers(args);
        }
    }

    /**
     * Helper method for editPost
     * updates field given selection
     *
     * @param i       selection
     * @param posting this posting
     */
    private void updateField(int i, Posting posting) {
        if (i == 1) {
            System.out.println("Enter new name: ");
            String newName = Util.next();
            posting.getPostingInfo().setName(newName);
        } else if (i == 2) {
            String newDescription = Util.next();
            posting.getPostingInfo().setDescription(newDescription);
        } else if (i == 3) {
            //Hardcoded maximum amount of hiring here
            int newAmount = Util.promptNumbers(10);
            posting.getPostingInfo().setAmountHiring(newAmount);
        } else if (i == 4) {
            LocalDate newCloseDate = Util.promptDates();
            posting.getPostingInfo().setDateClosed(newCloseDate);
        } else if (i == 5) {
            int j = 0;
            for (Company company : Main.companies.getSameCompany(this.user.getCompany())) {
                // Show all listings
                System.out.println("(" + (j + 1) + ") " + company.getName() + " at " + company.getLocation());
                j++;
            }
            System.out.print("Select new branch to assign the posting: ");
            int response = Util.promptNumbers(j);
            posting.getPostingInfo().setCompany(Main.companies.getSameCompany(this.user.getCompany()).get(response - 1));
        } else if (i == 6) {
            updateTags(posting);
        } else if (i == 7) {
            updateRequirement(posting);
        }
    }

    /**
     * Add/remove requirements
     *
     * @param posting this posting
     */
    private void updateRequirement(Posting posting) {
        System.out.println("Displaying current requirements for " + posting.getName() + ": ");
        System.out.println(posting.getRequirement());
        if (Util.booleanMenu("Would you like to add more requirements?")) {
            posting.getRequirement().addRequirements();
        } else {
            if (Util.booleanMenu("Would you like to remove any requirements?")) {
                String[] requirementsToRemove = Util.multipleString("requirements to remove");
                ArrayList<String> removed = new ArrayList<>();
                for (String required : posting.getRequirement().getRequirement()) {
                    for (String toRemove : requirementsToRemove) {
                        if (required.equalsIgnoreCase(toRemove)) {
                            removed.add(required);
                            posting.getRequirement().getRequirement().remove(required);
                        }
                    }
                }
                if (!removed.isEmpty()) {
                    System.out.println("Successfully removed following requirements" + removed);
                }
            }
        }
    }

    /**
     * add/remove tags
     *
     * @param posting this posting
     */
    private void updateTags(Posting posting) {
        System.out.println("Here are all the current tags for " + posting.getPostingInfo().getName() + ": ");
        System.out.println(posting.getTags());
        if (posting.getTags().isEmpty()) {
            if (Util.booleanMenu("This posting have no tag at the moment. Would you like to add some?")) {
                createTags();
            }
        } else {
            System.out.println("To select tag(s) to be removed, type name of tag(s) in the form of \"@name\", separated by space bar.");
            ArrayList<Tag> interestedTags = Util.createTagList(Util.next());
            ArrayList<String> removedTags = new ArrayList<>();
            for (int i = 0; i < posting.getTags().size(); i++) {
                for (Tag tag1 : interestedTags) {
                    if (posting.getTags().get(i).equals(tag1)) {
                        removedTags.add(posting.getTags().get(i).getName());
                        posting.getTags().remove(i);
                    }
                }
            }
            System.out.print("Successfully removed tag(s): " + removedTags);
        }
    }

    /**
     * view document of this applicant
     *
     * @param selectedApplicant this applicant
     * @param documentName      this type of document
     */
    private void viewDocument(Applicant selectedApplicant, String documentName) {
        String document = selectedApplicant.getDocument(documentName);

        if (document == null) {
            System.out.println("The applicant does not have a " + documentName);

        } else {
            System.out.print(documentName.substring(0, 1).toUpperCase() + documentName.substring(1) + ": " + document);
        }
    }

    /**
     * get a specific applicant from recommended list
     *
     * @param posting this posting
     * @return applicant of interest
     */
    private Applicant getRecommendedApplicant(Posting posting) {
        ArrayList<String> recommended = new ArrayList<>();
        if (!this.user.getCompany().getRecommended().containsKey(posting)) {
            System.out.println("This posting is not in the recommended list.");
            return null;
        } else {
            posting.sort(user.getCompany().getRecommended().get(posting));
            for (Applicant applicant : this.user.getCompany().getRecommended().get(posting)) {
                recommended.add(applicant.getUsername());
            }
            if (recommended.isEmpty()) {
                return null;
            } else {
                int response = Util.numericalMenu("Choose an applicant", recommended, true,
                        false);
                if (response == 0) {
                    return null;
                } else if (response <= user.getCompany().getRecommended().get(posting).size()) {
                    return this.user.getCompany().getRecommended().get(posting).get(response - 1);
                } else {
                    return null;
                }
            }
        }
    }
}