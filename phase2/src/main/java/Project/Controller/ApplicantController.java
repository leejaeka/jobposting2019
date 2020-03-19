package Project.Controller;

import Project.GUI.ApplicantGUI;
import Project.Main;
import Project.Model.Applicant;
import Project.Model.Interview;
import Project.Model.Posting;
import Project.Model.Tag;
import Project.Util;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Project.Controller for Project.GUI.ApplicantGUI
 */
public class ApplicantController {
    private ApplicantGUI g;

    public ApplicantController(ApplicantGUI g) {
        this.g = g;
    }

    public void clearDocumentsHelper(GridPane grid, Text feedback) {
        for (Node n : grid.getChildren()) {
            if (n instanceof TextArea) {
                ((TextArea) n).clear();
                feedback.setText(null);
            }
        }
    }

    /**
     * helper function to submit documents
     */
    public void submitDocumentsHelper(GridPane grid, Text feedback) {
        int i = 0;

        for (Node n : grid.getChildren()) {
            if (n instanceof TextArea) {
                g.getUser().setDocument(g.getUser().getListOfDocumentNames().get(i), ((TextArea) n).getText());
                i++;
            }
        }

        feedback.setFill(Color.DARKGREEN);
        feedback.setText("Document saved");

    }

    private void submitRequirementsHelper(Applicant user, Posting posting, List<ToggleGroup> tgs, Text mainFeedback, Text dialogFeedback, Stage dialog) {

        boolean qualified = true;

        for (ToggleGroup tg : tgs) {
            if (tg.getSelectedToggle() == null) {
                dialogFeedback.setText("Please choose all the options   ");
                return;
            } else if (tg.getSelectedToggle().toString().contains("No")) {
                qualified = false;
            }
        }

        if (!qualified) {
            dialog.close();
            mainFeedback.setFill(Color.FIREBRICK);
            mainFeedback.setText("You do not meet the requirements   ");
            return;
        }

        qualifiedHelper(user, posting, mainFeedback, dialog);

    }

    private void qualifiedHelper(Applicant user, Posting posting, Text mainFeedback, Stage dialog) {
        dialog.close();
        applyJob(posting, user);
        mainFeedback.setFill(Color.DARKGREEN);
        mainFeedback.setText("You've applied for " + posting.getPostingInfo().getName() +
                ". Good luck on your application!");
    }

    private void applyJob(Posting posting, Applicant user) {
        posting.addApplicant(user);
        user.addPosting(posting);
    }

    /**
     * Button controller for 'help' pop up
     *
     * @param grid        this grid
     * @param postingName this posting
     * @return pop up cell
     */
    @SuppressWarnings("unchecked")
    public Callback<TableColumn<Posting, String>, TableCell<Posting, String>> viewButtonsController(GridPane grid, Text postingName) {
        return new Callback<TableColumn<Posting, String>, TableCell<Posting, String>>() {
            @Override
            public TableCell call(final TableColumn<Posting, String> param) {
                return new TableCell<Posting, String>() {

                    final Button btn = new Button("❓");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Posting posting = getTableView().getItems().get(getIndex());

                                postingName.setText(posting.getPostingInfo().getName());

                                Interview interview = (g.user).getCurrentApplications().get(posting);

                                g.clearGridHelper(grid, "stage");

                                int i = 1;
                                for (String stageName : interview.getStages().keySet()) {
                                    Label label = new Label(stageName + ": " + interview.getStageDate(stageName));
                                    label.setId("stage");

                                    grid.add(label, 0, i);
                                    i++;
                                }
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
            }
        };
    }

    @SuppressWarnings("unchecked")
    public Callback<TableColumn<Posting, String>, TableCell<Posting, String>> requirementButtonsController() {
        return new Callback<TableColumn<Posting, String>, TableCell<Posting, String>>() {
            @Override
            public TableCell call(final TableColumn<Posting, String> param) {
                return new TableCell<Posting, String>() {

                    final Button btn = new Button("🐳");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Posting posting = getTableView().getItems().get(getIndex());
                                g.getAlert(Main.stage, "Requirement", posting.getRequirement().toString()).showAndWait();

                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
            }
        };
    }

    @SuppressWarnings("unchecked")
    public Callback<TableColumn<Posting, String>, TableCell<Posting, String>> withdrawButtonsController(Text feedback, Collection<Posting> postings) {
        return new Callback<TableColumn<Posting, String>, TableCell<Posting, String>>() {
            @Override
            public TableCell call(final TableColumn<Posting, String> param) {
                return new TableCell<Posting, String>() {

                    final Button btn = new Button("❌");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> withdrawHelper());

                            setGraphic(btn);
                            setText(null);
                        }
                    }

                    private void withdrawHelper() {
                        // Confirmation dialog
                        Alert alert = g.getAlert(
                                Alert.AlertType.CONFIRMATION, Main.stage,
                                "Confirmation Dialog",
                                "Are you ok with this?",
                                "Look, application will have to start all over once it is withdrew");


                        Optional<ButtonType> result = alert.showAndWait();

                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            // ... user chose OK
                            Posting posting = getTableView().getItems().get(getIndex());

                            g.user.withdraw(posting);
                            posting.removeApplicant(g.user);
                            g.user.setDateClosed(LocalDate.now());
                            g.c.updateTable(getTableView(), postings);

                            feedback.setFill(Color.DARKGREEN);
                            feedback.setText("You have withdraw application process for: " +
                                    posting.getPostingInfo().getName());
                        }
                    }
                };
            }
        };
    }

    @SuppressWarnings("unchecked")
    public Callback<TableColumn<Posting, String>, TableCell<Posting, String>> applyButtonsController(Text feedback) {
        return new Callback<TableColumn<Posting, String>, TableCell<Posting, String>>() {
            @Override
            public TableCell call(final TableColumn<Posting, String> param) {
                return new TableCell<Posting, String>() {

                    final Button btn = new Button("✔");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Posting posting = getTableView().getItems().get(getIndex());
                                if (!g.user.hasUploadedAllDocuments()) {
                                    feedback.setFill(Color.FIREBRICK);
                                    feedback.setText("Please first upload all the required document");
                                } else if (g.user.getCurrentApplications().keySet().contains(posting)) {
                                    feedback.setFill(Color.FIREBRICK);
                                    feedback.setText("You've already applied for " + posting.getPostingInfo().getName() +
                                            ". Your application is currently under review");
                                } else {
                                    requirementCheck(posting, feedback);
                                }
                            });

                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
            }
        };
    }

    private void requirementCheck(Posting posting, Text mainFeedback) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(Main.stage);

        GridPane grid = g.createGridHelper();
        VBox vBox = g.crateVBoxWithBottomBar(grid, true, false);

        // Title
        Text sceneTitle = new Text("Do you meet the following requirement?");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
        grid.add(sceneTitle, 0, 0, 3, 1);

        List<String> requirements = posting.getRequirement().getRequirement();
        List<ToggleGroup> toggleGroups = new ArrayList<>();

        int i = 1;
        for (String s : requirements) {
            if (!(s.equalsIgnoreCase("cv") || s.equalsIgnoreCase("resume"))) {
                RadioButton yes = new RadioButton("Yes");
                RadioButton no = new RadioButton("No");
                ToggleGroup requirementMet = new ToggleGroup();
                yes.setToggleGroup(requirementMet);
                no.setToggleGroup(requirementMet);
                toggleGroups.add(requirementMet);

                grid.add(new Label(s), 0, i);
                grid.add(yes, 1, i);
                grid.add(no, 2, i);

                i++;
            }
        }

        Button submit = new Button("Submit");
        grid.add(submit, 1, i);

        Text dialogFeedback = g.getFeedbackTextHelper(vBox);
        // SUBMIT button event
        submit.setOnAction(e -> submitRequirementsHelper(g.user, posting, toggleGroups, mainFeedback, dialogFeedback, dialog));

        // Skip the dialog scene if there is no specific requirement (other than CV and resume)
        if (requirements.size() <= 2) {
            qualifiedHelper(g.user, posting, mainFeedback, dialog);

        } else {
            Scene dialogScene = new Scene(vBox, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
        }
    }

    public void updateTableAndTagStatus(TableView tableView, TextField filterField, Text tagStatus, ApplicantGUI applicantGUI) {
        ArrayList<Tag> interestedTags = Util.createTagList(filterField.getText());

        // Set tagStatus Text
        StringBuilder currentTags = new StringBuilder();

        if (!interestedTags.isEmpty()) {
            for (Tag tag : interestedTags) {
                currentTags.append("@").append(tag.getName()).append(" ");
            }
        } else {
            currentTags.append("n/a");
        }

        tagStatus.setText(currentTags.toString());

        List<Posting> qualifiedPosting = new ArrayList<>();

        for (Posting posting : Main.postings.get()) {
            if (posting.getTags().containsAll(interestedTags)) {
                qualifiedPosting.add(posting);
            }
        }

        applicantGUI.c.updateTable(tableView, qualifiedPosting);
    }

    @SuppressWarnings("unchecked")
    private void updateTable(TableView tableView, Collection c) {
        // Update table
        tableView.getItems().clear();
        tableView.getItems().addAll(c);
    }
}
