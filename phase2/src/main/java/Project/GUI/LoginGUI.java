package Project.GUI;

import Project.Main;
import Project.Model.*;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.util.Arrays;


/**
 * Login Project.GUI.Project.GUI. It builds the login scene graph as well as handles mouse and keyboard events during the scene
 */
public class LoginGUI extends GUI {

    @SuppressWarnings("SpellCheckingInspection")
    public Scene loginScene(Stage s) {
        GridPane grid = createGridHelper();

        // Welcome
        Text sceneTitle = new Text("Welcome");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 3, 1);

        // Username
        Label username = new Label("Username:");
        TextField usernameField = new TextField();
        // Auto completetion for username
        TextFields.bindAutoCompletion(usernameField, Main.users.getUsernames());
        grid.add(username, 0, 1);
        grid.add(usernameField, 1, 1);

        // Password
        Label pw = new Label("Password:");
        PasswordField pwField = new PasswordField();
        grid.add(pw, 0, 2);
        grid.add(pwField, 1, 2);

        // Register and sign in Button
        Button register = new Button("Register");
        Button signIn = new Button("Sign in");
        grid.add(register, 0, 4);
        grid.add(signIn, 1, 4);

        // Message
        final Text feedback = new Text();
        feedback.setFill(Color.FIREBRICK);
        grid.add(feedback, 0, 6, 2, 1);

        register.setOnAction(e -> register(usernameField, pwField, feedback, s));
        signIn.setOnAction(e -> signIn(usernameField, pwField, feedback, s));

        // ENTER key event
        grid.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                signIn(usernameField, pwField, feedback, s);
            }
        });

        return new Scene(grid, width, height);
    }

    @SuppressWarnings("SpellCheckingInspection")
    private Scene registerScene(String username, String pw, Stage s) {
        GridPane grid = createGridHelper();

        // Title
        Text sceneTitle = new Text("Continuing signing up...");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 3, 1);

        // Select user type
        Label selectUserType = new Label("Select a user type: ");
        grid.add(selectUserType, 0, 1, 2, 1);

        ToggleGroup userTypeGroup = new ToggleGroup();

        RadioButton applicant = new RadioButton("Applicant");
        RadioButton interviewer = new RadioButton("Interviewer");
        RadioButton hrCoordinator = new RadioButton("HR Coordinator");

        applicant.setToggleGroup(userTypeGroup);
        interviewer.setToggleGroup(userTypeGroup);
        hrCoordinator.setToggleGroup(userTypeGroup);

        grid.add(applicant, 2, 1);
        grid.add(interviewer, 3, 1);
        grid.add(hrCoordinator, 4, 1);

        // Register a new company?
        Label createNewCompany = new Label("Register a new company?");

        ToggleGroup registerNewCompany = new ToggleGroup();
        RadioButton yes = new RadioButton("Yes");
        RadioButton no = new RadioButton("No");
        yes.setToggleGroup(registerNewCompany);
        no.setToggleGroup(registerNewCompany);

        // Select selected company
        Label selectCompany = new Label("Select your company:");
        final ComboBox<? extends Company> companyComboBox = new ComboBox<>(
                FXCollections.observableList(Main.companies.get()));
        companyComboBox.getSelectionModel().selectFirst();

        // New company info
        Label name = new Label("Company name: ");
        Label location = new Label("Location: ");
        TextField nameField = new TextField();
        TextField locationField = new TextField();

        // Message
        final Text feedback = new Text();
        feedback.setFill(Color.FIREBRICK);
        grid.add(feedback, 0, 7, 3, 1);

        // Progress Bar
        ProgressBar pb = new ProgressBar(0.6);
        Label progress = new Label("50% Completed");
        grid.add(pb, 0, 8, 2, 1);
        grid.add(progress, 2, 8, 2, 1);

        // Register Button
        Button back = new Button("Back");
        HBox hbBack = new HBox(10);
        hbBack.setAlignment(Pos.BOTTOM_RIGHT);
        hbBack.getChildren().add(back);
        grid.add(hbBack, 0, 6);

        // Sign in Button
        Button completeRegistration = new Button("Complete registration");
        HBox hbRegister = new HBox(10);
        hbRegister.setAlignment(Pos.BOTTOM_RIGHT);
        hbRegister.getChildren().add(completeRegistration);
        grid.add(hbRegister, 1, 6, 2, 1);

        userTypeListener(grid, userTypeGroup, applicant, createNewCompany, registerNewCompany,
                yes, no, selectCompany, companyComboBox, name, location, nameField, locationField, feedback);

        registerNewCompanyListener(grid, registerNewCompany, yes, no, selectCompany,
                companyComboBox, name, location, nameField, locationField, feedback);

        back.setOnAction(e -> s.setScene(loginScene(s)));
        completeRegistration.setOnAction(e -> completeRegistration(username, pw, userTypeGroup,
                registerNewCompany, companyComboBox, nameField, locationField, feedback, s));

        // ENTER key event
        grid.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                completeRegistration(username, pw, userTypeGroup,
                        registerNewCompany, companyComboBox, nameField, locationField, feedback, s);
            }
        });

        return new Scene(grid, width, height);
    }

    private void register(TextField u, PasswordField p, Text feedback, Stage s) {
        String username = u.getText();
        String pw = p.getText();

        if (username.isEmpty()) {
            feedback.setText("Please enter a username");

        } else if (pw.isEmpty()) {
            feedback.setText("Please enter a password");

        } else if (Main.users.get(username) != null) {
            feedback.setText("Username already existed");

        } else {
            feedback.setText(null);
            s.setScene(registerScene(username, pw, s));
        }
    }

    private void signIn(TextField u, PasswordField p, Text feedback, Stage s) {
        String username = u.getText();
        String pw = p.getText();

        if (username.isEmpty()) {
            feedback.setText("Please enter your username");

        } else if (pw.isEmpty()) {
            feedback.setText("Please enter your password");

        } else if (!Main.users.authenticate(username, pw)) {
            feedback.setText("Wrong username/password");

        } else {
            feedback.setText(null);
            User user = Main.users.get(username);
            System.out.println("Tip: At any point in time if you want to logout or exit the program, type \"exit\"");
            s.setScene(user.getTUI().getMenu());
        }
    }

    private void completeRegistration(String username, String pw, ToggleGroup u, ToggleGroup r,
                                      ComboBox<? extends Company> c,
                                      TextField n, TextField l, Text feedback, Stage s) {
        Toggle userType = u.getSelectedToggle();
        Toggle registerNewCompany = r.getSelectedToggle();
        Company company = c.getValue();
        String name = n.getText();
        String location = l.getText();

        if (userType == null) {
            feedback.setText("Please select a user type");

        } else if (userType.toString().contains("Project.Model.Applicant")) {
            createUserHelper(userType, username, pw, company, s);

        } else {
            if (registerNewCompany == null) {
                feedback.setText("Please select an option");

            } else {

                if (registerNewCompany.toString().contains("Yes")) {
                    // Create a new company
                    if (name.isEmpty()) {
                        feedback.setText("Please enter the company name");

                    } else if (location.isEmpty()) {
                        feedback.setText("Please enter the location");

                    } else {
                        company = new Company(name, location);

                        if (Main.companies.get().contains(company)) {
                            feedback.setText("Company already existed");
                        } else {
                            Main.companies.add(company);
                            createUserHelper(userType, username, pw, company, s);
                        }
                    }

                } else {
                    createUserHelper(userType, username, pw, company, s);
                }
            }

        }

    }

    private void createUserHelper(Toggle userType, String username, String pw, Company c, Stage s) {
        User createdUser;

        if (userType.toString().contains("Project.Model.Applicant")) {
            createdUser = new Applicant(username, pw);

        } else if (userType.toString().contains("Interviewer")) {
            createdUser = new Interviewer(username, pw, c);

        } else if (userType.toString().contains("Coordinator")) {
            createdUser = new HRCoordinator(username, pw, c);

        } else {
            throw new IllegalArgumentException("User type not found!");
        }

        Main.users.add(createdUser);
        System.out.println("Tip: At any point in time if you want to logout or exit the program, type \"exit\"");
        s.setScene(createdUser.getTUI().getMenu());
    }

    private void userTypeListener(GridPane grid, ToggleGroup userTypeGroup, RadioButton applicant, Label createNewCompany,
                                  ToggleGroup registerNewCompany, RadioButton yes, RadioButton no, Label selectCompany,
                                  ComboBox<? extends Company> companyComboBox, Label name, Label location,
                                  TextField nameField, TextField locationField, Text feedback) {
        userTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton rb = (RadioButton) userTypeGroup.getSelectedToggle();
            feedback.setText(null);

            if (rb.equals(applicant)) {
                clearGridHelper(grid, Arrays.asList(createNewCompany, yes, no, selectCompany, companyComboBox,
                        name, nameField, location, locationField));

                if (registerNewCompany.getSelectedToggle() != null) {
                    registerNewCompany.getSelectedToggle().setSelected(false);
                }
            } else if (!grid.getChildren().contains(createNewCompany)) {
                grid.add(createNewCompany, 0, 2, 2, 1);
                grid.add(yes, 2, 2);
                grid.add(no, 3, 2);
            }
        });
    }

    private void registerNewCompanyListener(GridPane grid, ToggleGroup registerNewCompany, RadioButton yes, RadioButton no,
                                            Label selectCompany, ComboBox<? extends Company> companyComboBox, Label name,
                                            Label location, TextField nameField, TextField locationField, Text feedback) {
        registerNewCompany.selectedToggleProperty().addListener(((observable1, oldValue1, newValue1) -> {
            RadioButton rb1 = (RadioButton) registerNewCompany.getSelectedToggle();
            feedback.setText(null);

            if (rb1 != null) {
                if (rb1.equals(yes)) {
                    clearGridHelper(grid, Arrays.asList(selectCompany, companyComboBox));

                    grid.add(name, 0, 3, 2, 1);
                    grid.add(nameField, 2, 3, 3, 1);
                    grid.add(location, 0, 4, 2, 1);
                    grid.add(locationField, 2, 4, 3, 1);

                } else if (rb1.equals(no)) {
                    clearGridHelper(grid, Arrays.asList(name, nameField, location, locationField));

                    // Select company
                    grid.add(selectCompany, 0, 3, 2, 1);
                    grid.add(companyComboBox, 2, 3, 3, 1);
                }
            }
        }));
    }

}
