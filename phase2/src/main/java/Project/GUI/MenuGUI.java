package Project.GUI;

import Project.Main;
import Project.Model.User;
import Project.SaveManager;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;


public abstract class MenuGUI<T extends User> extends GUI {
    private final LinkedHashMap<String, Button> options;
    public T user;

    MenuGUI() {
        this.options = new LinkedHashMap<>();

    }

    public T getUser() {
        return this.user;
    }

    void addOptionsDisplay(List<String> optionText) {
        List<String> completeOptionText = new ArrayList<>(optionText);
        completeOptionText.add("Change password");
        completeOptionText.add("Logout");

        for (String s : completeOptionText) {
            Button btn = new Button(s);

            if (s.equalsIgnoreCase("Change password")) {
                btn.setOnAction(e -> changePasswordScreen());
            } else if (s.equalsIgnoreCase("Logout")) {
                btn.setOnAction(event -> logoutHandler());
            }

            options.put(s, btn);
        }
    }

    private void changePasswordScreen() {
        GridPane grid = createGridHelper();
        addUIControlsToPasswordScreen(grid);
        Scene scene = new Scene(grid, width, height);
        Main.stage.setScene(scene);
    }

    private void addUIControlsToPasswordScreen(GridPane grid) {
        Label currentPw = new Label("Current Password:");
        grid.add(currentPw, 0, 0);
        PasswordField currentPwField = new PasswordField();
        currentPwField.setPromptText("current password");
        grid.add(currentPwField, 1, 0);

        Label newPass = new Label("New Password:");
        grid.add(newPass, 0, 1);
        PasswordField pwBox = new PasswordField();
        pwBox.setPromptText("new password");
        grid.add(pwBox, 1, 1);

        Button save = new Button("Save");
        Button cancel = new Button("Cancel");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(cancel);
        hbBtn.getChildren().add(save);
        grid.add(hbBtn, 1, 3);

        final Text actionTarget = new Text();
        grid.add(actionTarget, 1, 5);

        save.setOnAction(event -> {
            actionTarget.setFill(Color.FIREBRICK);
            String realOldPass = user.getPw();
            String currentPassTyped = currentPwField.getText();
            String newPass1 = pwBox.getText();

            if (currentPassTyped.isEmpty()) {
                actionTarget.setText("Please enter your current password");
            }
            if (newPass1.isEmpty()) {
                actionTarget.setText("Please enter the new password");

            } else if (!realOldPass.equalsIgnoreCase(currentPassTyped)) {
                actionTarget.setText("Current password is incorrect");
            } else {
                user.setPw(newPass1);
                setPasswordHandler();
            }
        });

        cancel.setOnAction(e -> Main.stage.setScene(getMenu()));
    }

    private void setPasswordHandler() {
        getAlert(Main.stage,
                "Password changed",
                "Your password has been changed").showAndWait();
        Main.stage.setScene(getMenu());
    }

    public abstract Scene getMenu();

    /**
     * When user selects 'logout'
     */
    private void logoutHandler() {
        SaveManager.writeAll(Main.users, Main.postings, Main.companies, Main.tags);
        Main.stage.setScene(Main.loginGUI.loginScene(Main.stage));
    }

    /**
     * A pop-up alert window
     *
     * @param alertType e.g. INFORMATION, ERROR
     * @param stage     window
     * @param title     title of window
     * @param message   alert message
     */
    public Alert getAlert(Alert.AlertType alertType, Window stage, String title, String message, String header) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.initOwner(stage);
        alert.initStyle(StageStyle.UTILITY);

        return alert;
    }

    public Alert getAlert(Window stage, String title, String message) {
        return getAlert(Alert.AlertType.INFORMATION, stage, title, message, null);
    }

    Button getOption(int i) {
        List<String> optionText = new ArrayList<>(options.keySet());

        return options.get(optionText.get(i));
    }

    /**
     * Combine layout and controls to form Options Screen
     */
    GridPane createOptionsGrid() {
        GridPane grid = createGridHelper();
        addOptionsToLayout(grid);
        addMessageToOptionsScreen(grid);
        grid.setMinHeight(height - 30);
        return grid;
    }

    /**
     * Populates Options Screen with all the options
     *
     * @param layout gridPane
     */
    private void addOptionsToLayout(GridPane layout) {
        List<String> optionText = new ArrayList<>(options.keySet());
        for (int i = 1; i <= options.size(); i++) {
            if ((i - 1) % 2 == 0) {
                layout.add(options.get(optionText.get(i - 1)), 0, i);

            } else {
                layout.add(options.get(optionText.get(i - 1)), 1, i - 1);
            }
        }
    }

    private void addMessageToOptionsScreen(GridPane gridPane) {
        Text message = new Text("Welcome " + user.getUsername());
        message.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        gridPane.add(message, 0, 0, 2, 1);
    }

    private Button getGoBackButton() {
        // Go back button
        Button goBack = new Button("Go Back");
        goBack.setOnAction(event -> Main.stage.setScene(getMenu()));
        return goBack;
    }

    /**
     * Template vBox. It creates a vBox of list of Nodes and bottom bar, which contain feedback text and Go Back button.
     * To access feedback Text, use getFeedbackTextHelper()
     */
    VBox crateVBoxWithBottomBar(List<Node> nodes, boolean containsFeedBack, boolean containsButton) {
        VBox vbox = new VBox();

        for (Node n : nodes) {
            vbox.getChildren().add(n);
        }

        // Bottom Bar
        HBox bottomBar = new HBox(10);
        bottomBar.setId("bottomBar");
        bottomBar.setAlignment(Pos.BOTTOM_RIGHT);

        // Feedback text
        if (containsFeedBack) {
            final Text feedback = new Text();
            feedback.setId("feedback");
            feedback.setFill(Color.FIREBRICK);

            bottomBar.getChildren().add(feedback);
        }

        if (containsButton) {
            bottomBar.getChildren().add(getGoBackButton());
        }

        vbox.getChildren().add(bottomBar);

        return vbox;
    }

    public VBox crateVBoxWithBottomBar(Node node, boolean containsFeedBack, boolean containsButton) {
        return crateVBoxWithBottomBar(Collections.singletonList(node), containsFeedBack, containsButton);
    }
}
