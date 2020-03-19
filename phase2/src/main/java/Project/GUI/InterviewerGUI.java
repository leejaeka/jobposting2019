package Project.GUI;

import Project.Main;
import Project.Model.*;
import Project.Util;
import javafx.scene.Scene;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles the Interviewer TUI (text-based user interface)
 */
public class InterviewerGUI extends EmployeeGUI<Interviewer> {


    public InterviewerGUI(Interviewer interviewer) {
        super(interviewer);
    }

    @Override
    public Scene getMenu() {
        addOptionsDisplay(Arrays.asList(
                "Check my recommendation",
                "Add an applicant to my recommendation",
                "Send my recommendation to a HR coordinator",
                "View interviews scheduled for me",
                "Interview"));

        getOption(0).setOnAction(event -> printRecommendedApplicants());
        getOption(1).setOnAction(event -> {
            Main.stage.close();
            addApplicantsToRecommendation();
            Main.stage.show();

        });
        getOption(2).setOnAction(event -> {
            Main.stage.close();
            sendRecommendingList(selectHRCoordinator());
            Main.stage.show();

        });
        getOption(3).setOnAction(event -> {
            Main.stage.close();
            //viewApplicants(selectPosting(new ArrayList<>(user.applicantsInterviewing.keySet())));
            viewInterviews();
            Main.stage.show();

        });
        getOption(4).setOnAction(event -> {
            Main.stage.close();
            interviewApplicants();
            Main.stage.show();
        });

        return new Scene(createOptionsGrid(), width, height);
    }

    private void viewInterviews() {
        if (!this.user.applicantsInterviewing.keySet().isEmpty()) {
            for (Posting posting : this.user.applicantsInterviewing.keySet()) {
                System.out.println("For job postings " + posting.getName() + ": ");
                if (this.user.applicantsInterviewing.get(posting) != null && !this.user.applicantsInterviewing.get(posting).isEmpty()) {
                    for (Applicant applicant : this.user.applicantsInterviewing.get(posting)) {
                        System.out.println("-------------------------------------------------");
                        System.out.println("You have interview with " + applicant.getUsername());
                        System.out.println("This applicant's cover letter: " + applicant.getCoverLetter());
                        System.out.println("This applicant's resume: " + applicant.getResume());
                    }
                } else {
                    System.out.println("no interviews scheduled");
                }
            }
        }
    }

    private void printRecommendedApplicants() {
        if (!user.recommendation.keySet().isEmpty()) {
            Main.stage.close();

            for (Posting posting : user.recommendation.keySet()) {
                System.out.print(posting.getPostingInfo().getName() + ": ");

                for (Applicant applicant : user.recommendation.get(posting)) {
                    System.out.print(applicant.getUsername() + " \n");
                }
            }
            Main.stage.show();

        } else {
            getAlert(Main.stage, "No recommendation", "You have not recommended anyone yet").show();
        }
    }

    private void addApplicantsToRecommendation() {
        List<Posting> postingCandidates = new ArrayList<>(user.applicantsInterviewing.keySet());

        if (!postingCandidates.isEmpty()) {
            Posting jobPosting = selectPosting(postingCandidates);
            List<Applicant> applicantCandidates = user.applicantsInterviewing.get(jobPosting);

            if (applicantCandidates != null && !applicantCandidates.isEmpty()) {
                jobPosting.sort(applicantCandidates);
                Applicant applicantAdding = selectUser(applicantCandidates);
                user.addRecommendedApplicant(applicantAdding, jobPosting);
                System.out.println("The applicant is added :)");

            } else {
                System.out.println("There is no available applicant :(");
            }

        } else {
            System.out.println("You cannot recommend an applicant while there isn't one interviewed by you :(");
        }
    }

    private void sendRecommendingList(HRCoordinator hr) {
        hr.getCompany().addAllRecommendedApplicants(user.recommendation);
        // DO NOT change it, because addRecommendedApplicants() should not be static.
        // TODO: ^ it should not be static but it is static now. Someone follow this up please
        System.out.println("The list has been sent to HR Coordinator" + hr.getUsername());

    }

    private void interviewApplicants() {
        if (!user.applicantsInterviewing.isEmpty()) {
            List<Posting> postings = new ArrayList<>(user.applicantsInterviewing.keySet());
            Posting posting = selectPosting(postings);

            List<Applicant> applicants = user.applicantsInterviewing.get(posting);

            if (applicants != null && !applicants.isEmpty()) {
                Applicant applicant = selectUser(applicants);
                if (applicant != null) {
                    interviewApplicant(posting, applicant);
                    System.out.println(applicant.getUsername() +
                            " has been interviewed for " + posting.getPostingInfo().getName());
                }
            } else {
                System.out.println("There is no applicants available at the moment");
            }

        } else {
            System.out.println("Sorry, there are no interviews at the moment");
        }
    }

    private void addApplicantToRecommendationList(Posting jobPosting, Applicant applicantAdding) {
        if (!user.recommendation.containsKey(jobPosting)) {
            user.recommendation.put(jobPosting, new ArrayList<>());
        }

        user.recommendation.get(jobPosting).add(applicantAdding);
    }

    private void interviewApplicant(Posting posting, Applicant applicant) {
        Interview interview = applicant.getCurrentApplications().get(posting);

        boolean passed = Util.booleanMenu("Did this person pass the interview?");
        if (!passed) {
//            posting.removeApplicant(applicant);
////            this.user.withdrawApplicant(applicant, posting);
            applicant.withdraw(posting);

            // Observable pattern
            setChanged();
            List args = Arrays.asList(applicant, "We regret to inform you that your application of " +
                    posting.getName() + " has been rejected");
            notifyObservers(args);

        } else {
            int i = applicant.getCurrentApplications().get(posting).getCurrentStageNum() + 1;
            if (i < applicant.getCurrentApplications().get(posting).getStages().size()) {
                applicant.setInterviewStageDate(posting, i - 1, LocalDate.now());
                applicant.getCurrentApplications().get(posting).setCurrentStageNum(i);
                int stagesLeft = applicant.getCurrentApplications().get(posting).getStages().size() - i;
                System.out.println("There are " + stagesLeft + " interview stages left");
            } else {
                user.addRecommendedApplicant(applicant, posting);
                applicant.setInterviewStageDate(posting, i - 1, LocalDate.now());
                applicant.getCurrentApplications().get(posting).setCurrentStageNum(i);
                System.out.println(applicant.getUsername() + " has completed the final interview round");
            }
            // Observable pattern
            setChanged();
            List args = Arrays.asList(applicant, "One step closer to your dream job! You've passed the " +
                    interview.getStage(interview.getCurrentStageNum() - 1) + ". See you in the next interview! ");
            notifyObservers(args);
        }
    }

}