package Project;

import Project.Model.Tag;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ValueRange;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class that stores all the helper methods. Our helper methods help preventing code smell,
 * avoid duplication of code and functionality, and to display interaction format in a group setting project
 */
public class Util {

    private static final Scanner reader = new Scanner(System.in);

    /**
     * Combine the use of promptNumbers and promptNumbers
     */
    public static int numericalMenu(String question, List options, boolean extendedQuestion, boolean allowZero) {
        int numOfOptions = Util.promptNumbers(question, options, extendedQuestion, allowZero);

        return Util.promptNumbers(numOfOptions, allowZero);
    }

    public static int numericalMenu(String question, List options) {
        return numericalMenu(question, options, false, false);
    }

    /**
     * This helper method print the question with options listed.
     *
     * @return the total number of options
     */
    public static int promptNumbers(String question, List options, boolean extendedQuestion, boolean allowZero) {
        StringBuilder prompt = new StringBuilder("\n");

        if (extendedQuestion) {
            for (int i = 0; i < options.size(); i++) {
                prompt.append("(").append(i + 1).append(") ").append(options.get(i)).append("\n");
            }

            if (allowZero) {
                prompt.append("(").append(0).append(") ").append("Exit").append("\n");
            }

            prompt.append(question);

        } else {
            prompt.append(question);

            for (int i = 0; i < options.size(); i++) {
                prompt.append(" (").append(i + 1).append(") ").append(options.get(i));
            }

            if (allowZero) {
                prompt.append(" (").append(0).append(") ").append("Exit");
            }
        }

        prompt.append(": ");

        System.out.print(prompt);
        return options.size();
    }

    /**
     * This helper method handles the numerical user interaction in TUI (text-base interface)
     *
     * @param numOfOptions total number of options
     * @return the selected number within the range (1 to numOfOptions)
     */
    private static int promptNumbers(int numOfOptions, boolean allowZero) {
        ValueRange range = ValueRange.of(1, 1);

        try {
            range = allowZero ? ValueRange.of(0, numOfOptions) : ValueRange.of(1, numOfOptions);

        } catch (IllegalArgumentException ex) {
            System.err.println("Ouch! Empty list is passed in");
        }

        int responseInInt = -1;
        String responseInString = null;

        while (!range.isValidIntValue(responseInInt)) {
            try {
                responseInString = next();
                responseInInt = Integer.parseInt(responseInString);

                // Check if response is valid
                if (!range.isValidIntValue(responseInInt)) {
                    throw new NumberFormatException();
                }

            } catch (InputMismatchException | NumberFormatException ex) {
                checkExit(Objects.requireNonNull(responseInString));
                responseInInt = -1;
                System.out.print("Invalid. Please select option (" + range + "): ");
            }
        }

        return responseInInt;
    }

    /**
     * Basically a custom new Scanner(System.in).next() but it checks the keyword 'exit' and doesn't allow empty input
     */
    public static String next() {
        String input = reader.nextLine();

        while (input.equals("")) {
            System.out.print("Empty input is not allowed! Try again: ");
            input = reader.nextLine();
        }

        checkExit(input);
        return input.trim();
    }

    /**
     * check for input 'exit'
     * @param s this
     */
    private static void checkExit(String s) {
        if (s.trim().equalsIgnoreCase("exit")) {
            exit();
        }
    }

    /**
     * save and quit
     */
    private static void exit() {
        System.out.println("You have exited the program. " +
                "Thank you for using CSC207 Job Network!\n");
        SaveManager.writeAll(Main.users, Main.postings, Main.companies, Main.tags);
    }

    /**
     * Combine the use of a println() and promptNumbers
     */
    public static boolean booleanMenu(String question) {
        System.out.print(question + " (Y/N)? ");
        return Util.promptBoolean();
    }

    public static ArrayList<Tag> createTagList(String string) {
        ArrayList<Tag> tagList = new ArrayList<>();

        List<String> allMatches = new ArrayList<>();
        Matcher m = Pattern.compile("@[a-zA-Z_0-9]+").matcher(string);
        while (m.find()) {
            allMatches.add(m.group());
        }

        for (String s : allMatches) {
            Tag tag = Main.tags.get(s.substring(1));

            if (tag != null) {
                boolean duplicated = false;
                for (Tag filter : tagList) {
                    if (filter.getName().equalsIgnoreCase(s.substring(1))) {
                        duplicated = true;
                    }
                }
                // check if duplicate filter
                if (!duplicated) {
                    tagList.add(tag);
                }
            }
        }

        return tagList;
    }

    /**
     * This helper method handles the boolean user interaction in TUI (text-base interface)
     *
     * @return the boolean
     */
    private static boolean promptBoolean() {
        String response = next();
        checkExit(response);

        while (!response.equalsIgnoreCase("Y") && !response.equalsIgnoreCase("N")) {
            System.out.print("Invalid. Please select option (Y/N): ");
            response = next();
        }

        return response.equalsIgnoreCase("Y");
    }

    /**
     * Returns String[] of user inputs separated by ','
     *
     * @param target type of inputs we want
     * @return Project.Model.User inputs
     */
    public static String[] multipleString(String target) {
        System.out.print("Enter the the names of " + target + " " + "separated by \',\'\n" +
                "(for example: abc, def, ghi): ");
        String targetString = Util.next();
        return targetString.split(",");
    }

    // Overloaded method
    public static int promptNumbers(int numOfOptions) {
        return promptNumbers(numOfOptions, false);
    }

    /**
     * Ask user for Date
     *
     * @return date
     */
    public static LocalDate promptDates() {
        return promptDates("Enter the date");
    }

    /**
     * Ask for date
     * @param s question
     * @return generated date
     */
    public static LocalDate promptDates(String s) {
        System.out.print("s" + " (yyyy MM dd): ");

        while (true) {
            String inputDate = next();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            DateTimeFormatter format2 = DateTimeFormatter.ofPattern("yyyy MM dd");

            try {
                return LocalDate.parse(inputDate, format);

            } catch (DateTimeParseException ex) {
                try {
                    return LocalDate.parse(inputDate, format2);

                } catch (DateTimeParseException ex1) {
                    System.out.print("Invalid input. Please try again (yyyy MM dd): ");
                }
            }
        }
    }
}