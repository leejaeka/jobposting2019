package Project.Model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Requirements implements Serializable {

    /**
     * If it exists in this list, it's required
     */
    private ArrayList<String> requirements = new ArrayList<>();
    private LocalDate deadline;

    /**
     * CV and Resume are required
     */
    Requirements(PostingInfo posting) {
        this.deadline = posting.getDateClosed();
        requirements.add("CV");
        requirements.add("Resume");
    }

    /**
     * Write out the requirement for application
     *
     * @return requirement in text form
     */
    @Override
    public String toString() {
        StringBuilder requirements = new StringBuilder();
        requirements.append("• Application close date: ").append(deadline).append("\n");
        if (this.requirements != null && !this.requirements.isEmpty()) {
            int i = 1;
            for (String requirement : this.requirements) {
                requirements.append("• Requirement ").append(i).append(": ").append(requirement).append("\n");
                i++;
            }
        }
        return requirements.toString();
    }

    /**
     * Return requirements
     *
     * @return requirements
     */
    public ArrayList<String> getRequirement() {
        return requirements;
    }

    /**
     * Ask user for requirements
     */
    public void addRequirements() {
        System.out.println("Note that CV and resumes are required for every applications");
        Scanner sc = new Scanner(System.in);
        String stringTyped = "";
        int i = 0;
        while (true) {
            System.out.println("type additional requirement (for ex. 'experience in java')(enter 'quit' to stop adding): ");
            stringTyped = sc.nextLine();
            if (!stringTyped.equals("quit")) {
                if (requirements.contains(stringTyped)) {
                    System.out.println("This requirement already existed.");
                } else {
                    requirements.add(stringTyped);
                    i++;
                }
            } else {
                break;
            }
        }
        System.out.println("Successfully added " + i + " new requirements");
    }
}
