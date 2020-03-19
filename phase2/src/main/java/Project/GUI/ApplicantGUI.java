package Project.GUI;

import Project.Controller.ApplicantController;
import Project.Main;
import Project.Model.Applicant;
import Project.Model.Posting;
import Project.Model.Tag;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.*;

/**
 * Handles the Project.Model.Applicant TUI (text-based user interface)
 */
public class ApplicantGUI extends MenuGUI<Applicant> {

    public ApplicantController c = new ApplicantController(this);

    /**
     * Constructor
     *
     * @param applicant this applicant
     */
    public ApplicantGUI(Applicant applicant) {
        this.user = applicant;
    }

    /**
     * get menu Project.GUI.Project.GUI
     *
     * @return menu scene
     */
    @Override
    public Scene getMenu() {
        // Data and notification check
        user.checkClosed();
        user.checkLastApplication();
        checkNotification(user.getNotifications());

        // Adding options to menu
        addOptionsDisplay(Arrays.asList(
                "View and apply available job postings",
                "Manage my documents",
                "View the status of applied job"));

        getOption(0).setOnAction(event -> viewAndApplyJobs());
        getOption(1).setOnAction(event -> manageDocuments());
        getOption(2).setOnAction(event -> viewApplications());

        GridPane grid = createOptionsGrid();

        VBox vBox = crateVBoxWithBottomBar(grid, false, false);

        for (Node n : vBox.getChildren()) {
            if (n.getId() != null && n.getId().equals("bottomBar")) {
                Label label = new Label("God Mode: ");
                Button btn1 = new Button("Go 15 days into the future");
                Button btn2 = new Button("Initialize date closed");

                btn1.setOnAction(event -> fifteenDaysToFuture());
                btn2.setOnAction(event -> setDateclosedToToday());

                ((HBox) n).getChildren().add(label);
                ((HBox) n).getChildren().add(btn1);
                ((HBox) n).getChildren().add(btn2);
            }
        }

        return new Scene(vBox, width, height);
    }

    /**
     * make last date closed fifteen days in the past... just for testing functionality
     */
    private void fifteenDaysToFuture() {
        LocalDate dateClosed = this.user.dateClosed;

        if (dateClosed != null) {
            this.user.dateClosed = this.user.dateClosed.minusDays(15);
        }

        if (getDaySinceClosedHelper().equals("n/a")) {
            getAlert(Main.stage, "No application has ever been closed...",
                    "In order to change the date, at least one application has to be closed").showAndWait();
        } else {
            getAlert(Main.stage, "15 days have been passed...",
                    "It has been " + getDaySinceClosedHelper() + " days since the last application closed.").showAndWait();
        }

        // Update fields
        Main.stage.setScene(getMenu());
    }

    /**
     * make closed date to today's date... just for testing functionality
     */
    private void setDateclosedToToday() {
        this.user.dateClosed = LocalDate.now();
    }

    private void checkNotification(LinkedList<String> notifications) {
        for (String s : notifications) {
            getAlert(Main.stage, "You have a notification", s).showAndWait();
        }

        notifications.clear();
    }

    public GridPane createOptionsGrid() {
        GridPane grid = super.createOptionsGrid();

        // Create account info at the bottom of the grid
        Label infoTitle = new Label("Account Info:");
        infoTitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 15));

        // Date created
        Label dateCreated = new Label("Account created on:");
        Label dateCreatedValue = new Label(user.getDateCreated().toString());
        dateCreatedValue.setFont(Font.font("Tahoma", FontWeight.BOLD, 11));
        dateCreatedValue.setWrapText(true);

        // Jobs applied in the past
        Label pastJob = new Label("Job applied in the past:");
        Label pastJobValue = new Label(getApplicationsString(user.getPastApplications()));
        pastJobValue.setFont(Font.font("Tahoma", FontWeight.BOLD, 11));
        pastJobValue.setWrapText(true);

        // Jobs currently applying
        Label currentJobs = new Label("Job currently applying for:");
        Label currentJobsValue = new Label(getApplicationsString(user.getCurrentApplications().keySet()));
        currentJobsValue.setFont(Font.font("Tahoma", FontWeight.BOLD, 11));
        currentJobsValue.setWrapText(true);

        // How long since the last application closed
        Label daySinceClosed = new Label("Number of days since the last application closed:");

        String s = getDaySinceClosedHelper();
        Label daySinceClosedValue = new Label(s);
        daySinceClosedValue.setFont(Font.font("Tahoma", FontWeight.BOLD, 11));
        daySinceClosedValue.setWrapText(true);


        grid.add(infoTitle, 0, 10, 2, 1);

        grid.add(dateCreated, 0, 11);
        grid.add(dateCreatedValue, 1, 11);

        grid.add(pastJob, 0, 12);
        grid.add(pastJobValue, 1, 12);

        grid.add(currentJobs, 0, 13);
        grid.add(currentJobsValue, 1, 13);

        grid.add(daySinceClosed, 0, 14);
        grid.add(daySinceClosedValue, 1, 14);

        return grid;
    }

    private String getDaySinceClosedHelper() {
        int i;
        for (i = 0; i < 999; i++) {
            if (LocalDate.now().minusDays(i).equals(user.getDateClosed())) {
                break;
            }
        }
        return user.getDateClosed() == null ? "n/a" : String.valueOf(i);
    }

    /**
     * Displays all available job postings and ask applicant to select a job to view more in detail
     */
    @SuppressWarnings("unchecked")
    private void viewAndApplyJobs() {
        List<Posting> postings = Main.postings.get();
        if (postings.isEmpty()) {
            getAlert(Main.stage,
                    "Job posting not available",
                    "Sorry but there are no job postings at the moment").showAndWait();

        } else {
            // UpperBar
            HBox upperBar = new HBox(10);
            upperBar.setAlignment(Pos.CENTER_LEFT);

            Label filter = new Label(" Filter:");

            TextField filterField = new TextField();
            filterField.setPrefWidth(450.0);

            Button filterHelp = new Button("❓");

            upperBar.getChildren().add(filter);
            upperBar.getChildren().add(filterField);
            upperBar.getChildren().add(filterHelp);

            // Project.Main Table
            TableView tableView = new TableView<>();

            TableColumn<String, Posting> col1 = new TableColumn<>("Project.Model.Posting Name");
            col1.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<String, Posting> col2 = new TableColumn<>("Description");
            col2.setCellValueFactory(new PropertyValueFactory<>("description"));

            TableColumn<String, Posting> col3 = new TableColumn<>("Positions Available");
            col3.setCellValueFactory(new PropertyValueFactory<>("availablePositions"));

            TableColumn<String, Posting> col4 = new TableColumn<>("Post Date");
            col4.setCellValueFactory(new PropertyValueFactory<>("datePosted"));

            TableColumn<String, Posting> col5 = new TableColumn<>("End Date");
            col5.setCellValueFactory(new PropertyValueFactory<>("dateClosed"));

            TableColumn<String, Posting> col6 = new TableColumn<>("Tags");
            col6.setCellValueFactory(new PropertyValueFactory<>("tags"));

            TableColumn<Posting, String> col7 = new TableColumn<>("Requirements");
            TableColumn<Posting, String> col8 = new TableColumn<>("Apply");

            List<TableColumn> cols = Arrays.asList(col1, col2, col3, col4, col5, col6, col7, col8);
            tableView.getColumns().addAll(cols);

            tableView.getItems().addAll(postings);


            //Bottom Left Bar
            Label filterApplied = new Label(" Filter applied: ");
            final Text tagStatus = new Text("n/a");

            HBox bottomLeftBar = new HBox(10);
            bottomLeftBar.setAlignment(Pos.BASELINE_LEFT);

            bottomLeftBar.getChildren().add(filterApplied);
            bottomLeftBar.getChildren().add(tagStatus);

            List<Node> scenesList = Arrays.asList(upperBar, tableView, bottomLeftBar);
            VBox vBox = crateVBoxWithBottomBar(scenesList, true, true);

            // Add APPLY button to the table and create event
            Callback<TableColumn<Posting, String>, TableCell<Posting, String>> cellFactory =
                    c.applyButtonsController(getFeedbackTextHelper(vBox));
            col8.setCellFactory(cellFactory);

            // Add REQUIREMENT button to the table and create event
            Callback<TableColumn<Posting, String>, TableCell<Posting, String>> cellFactory1 =
                    c.requirementButtonsController();
            col7.setCellFactory(cellFactory1);

            // OFF FOCUS event
            filterField.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
                if (!newPropertyValue) {
                    c.updateTableAndTagStatus(tableView, filterField, tagStatus, this);
                }
            });

            // ENTER key event
            filterField.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER)) {
                    c.updateTableAndTagStatus(tableView, filterField, tagStatus, this);
                }
            });

            // Prompt Text for filterField
            StringBuilder promptText = new StringBuilder();

            for (Tag tag : Main.tags.get()) {
                promptText.append("@").append(tag.getName()).append(" ");
            }

            filterField.setPromptText("Tags, e.g. " + promptText.toString());

            // HELP button event
            filterHelp.setOnAction(event -> getAlert(Main.stage, "Filter",
                    "To create filter, type tags in the form of \"@name\", separated by space bar. " +
                            "Here are available tags:\n\n" + promptText.toString()).showAndWait());

            Main.stage.setScene(new Scene(vBox, width, height));
        }
    }

    /**
     * Manage documents (Cover letter, resume)
     */
    private void manageDocuments() {
        GridPane grid = createGridHelper();

        // Using for loop to create document label text area
        int index = 1;
        for (String s : user.getListOfDocumentNames()) {
            Label doc = new Label(s.substring(0, 1).toUpperCase() + s.substring(1) + ":");
            doc.setMinWidth(75);

            TextArea docTextArea = new TextArea(user.getDocument(s));

            grid.add(doc, 0, index);
            grid.add(docTextArea, 1, index, 6, 1);

            index++;
        }

        // Register and sign in Button
        Button clear = new Button("Clear");
        Button delete = new Button("Delete");
        Button submit = new Button("Save");

        grid.add(clear, 1, 4);
        grid.add(delete, 5, 4);
        grid.add(submit, 6, 4);

        VBox vBox = crateVBoxWithBottomBar(grid, true, true);
        Text feedback = getFeedbackTextHelper(vBox);

        // TextFields click event
        for (Node n : grid.getChildren()) {
            if (n instanceof TextArea) {
                n.setOnMouseClicked(e -> feedback.setText(null));
            }
        }

        // CLEAR button event
        clear.setOnAction(e -> c.clearDocumentsHelper(grid, feedback));

        // DELETE button event
        delete.setOnAction(e -> {
            c.clearDocumentsHelper(grid, feedback);
            c.submitDocumentsHelper(grid, feedback);
            feedback.setText("Documents deleted");
        });

        // SUBMIT button event
        submit.setOnAction(e -> c.submitDocumentsHelper(grid, feedback));

        // ENTER key event
        grid.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                c.submitDocumentsHelper(grid, feedback);
            }
        });

        Main.stage.setScene(new Scene(vBox, width, height));
    }

    /**
     * View all status of all applied job
     */
    @SuppressWarnings("unchecked")
    private void viewApplications() {
        Set<Posting> postings = user.getCurrentApplications().keySet();
        if (postings.isEmpty()) {
            getAlert(Main.stage, "No job applied",
                    "No applied job is currently under review").showAndWait();

        } else {
            // Left pane: Project.Main Table
            TableView tableView = new TableView<>();
            tableView.setMinWidth(500);

            TableColumn<String, Posting> col1 = new TableColumn<>("Project.Model.Posting Name");
            col1.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<String, Posting> col2 = new TableColumn<>("Description");
            col2.setCellValueFactory(new PropertyValueFactory<>("description"));

            TableColumn<String, Posting> col3 = new TableColumn<>("End Date");
            col3.setCellValueFactory(new PropertyValueFactory<>("dateClosed"));

            TableColumn<Posting, String> col4 = new TableColumn<>("View");
            TableColumn<Posting, String> col5 = new TableColumn<>("Withdraw");

            tableView.getColumns().addAll(Arrays.asList(col1, col2, col3, col4, col5));

            tableView.getItems().addAll(postings);

            // Right pane: interview details of selected posting
            GridPane grid = createGridHelper();

            // Project.Model.Posting name
            Text postingName = new Text("Select an application");
            postingName.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            grid.add(postingName, 0, 0);

            // Create SplitPane
            SplitPane splitPane = new SplitPane();
            splitPane.setPrefSize(width, height);
            splitPane.getItems().addAll(tableView, grid);

            VBox vBox = crateVBoxWithBottomBar(splitPane, true, true);

            // Add VIEW button to the table
            Callback<TableColumn<Posting, String>, TableCell<Posting, String>> cellFactory =
                    c.viewButtonsController(grid, postingName);
            col4.setCellFactory(cellFactory);

            // Add WITHDRAW button to the table
            Callback<TableColumn<Posting, String>, TableCell<Posting, String>> cellFactory1 =
                    c.withdrawButtonsController(getFeedbackTextHelper(vBox), postings);
            col5.setCellFactory(cellFactory1);

            Main.stage.setScene(new Scene(vBox, width, height));
        }
    }

    /**
     * Helper method: view a list of any type of posting, e.g. current, past...
     *
     * @param applications this type of application list, e.g. current, past...
     */
    private String getApplicationsString(Collection<Posting> applications) {
        StringBuilder output = new StringBuilder();

        if (applications.isEmpty()) {
            output.append("n/a");

        } else {
            for (Posting p : applications) {
                String s = "[" + p.getStatus() + "] " + p.getName() + ": " + p.getDescription() + ", expires on " + p.getDateClosed();
                output.append("\n").append(s);
            }
        }

        return output.toString();
    }


    public Text getFeedbackTextHelper(VBox vBox) {
        // Search for HBox bottomBar
        for (Node n : vBox.getChildren()) {
            if (n.getId() != null && n.getId().equals("bottomBar")) {

                // Search for Text feedback
                for (Node m : ((HBox) n).getChildren()) {
                    if (m.getId() != null && m.getId().equals("feedback")) {
                        return (Text) m;
                    }
                }
            }
        }
        throw new NullPointerException();
    }

}
