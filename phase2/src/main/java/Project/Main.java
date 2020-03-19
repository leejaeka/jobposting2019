package Project;

import Project.GUI.LoginGUI;
import Project.Manager.CompanyManager;
import Project.Manager.PostingManager;
import Project.Manager.TagManager;
import Project.Manager.UserManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main class of the program where Project.Main is located
 */
public class Main extends Application {
    public static UserManager users = new UserManager();
    public static PostingManager postings = new PostingManager();
    public static CompanyManager companies = new CompanyManager();
    public static TagManager tags = new TagManager();

    // Login Project.GUI.Project.GUI
    public static LoginGUI loginGUI = new LoginGUI();

    // Project.GUI.Project.GUI stage
    public static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage s) {
        stage = s;

        // Deserialization
        users = SaveManager.readManager(UserManager.class, SaveManager.usersFilename);
        postings = SaveManager.readManager(PostingManager.class, SaveManager.postingsFilename);
        companies = SaveManager.readManager(CompanyManager.class, SaveManager.companiesFilename);
        tags = SaveManager.readManager(TagManager.class, SaveManager.tagsFilename);

        // Add observers here


        // Print serialization info
        System.out.println("\n" + users + "\n" + postings + "\n" + companies + "\n" + tags +
                "\n\n--------------------------------------\n" +
                "Please proceed to the login windows");

        // Set and show stage
        s.setTitle("CSC207 Job Network");
        s.setScene(loginGUI.loginScene(s));
        s.show();
    }


}
