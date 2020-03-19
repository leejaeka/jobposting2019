package Project;

import Project.Manager.*;
import Project.Model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;


/**
 * Project.SaveManager class is where handles the serialization of -Project.Manager.Project.Manager classes.
 */
public class SaveManager {

    static String usersFilename = "src/main/resources/users.ser";
    static String postingsFilename = "src/main/resources/postings.ser";
    static String companiesFilename = "src/main/resources/companies.ser";
    static String tagsFilename = "src/main/resources/tags.ser";

    private static Company amazon = new Company("Amazon", "Toronto");
    private static Company tesla = new Company("Tesla", "Toronto");
    private static Company google = new Company("Google", "Toronto");
    private static Company riot = new Company("Facebook", "Silicon Valley");


    /**
     * Deserialize
     *
     * @param c        for this class
     * @param filepath for this filepath
     * @param <T>      any
     * @return deserialize
     */
    static <T extends Manager> T readManager(Class c, String filepath) {
        return deserialize(c, filepath);
    }

    /**
     * write all the demos
     *
     * @param userManager    this
     * @param postingManager this
     * @param companyManager this
     * @param tagManager     this
     */
    public static void writeAll(UserManager userManager, PostingManager postingManager, CompanyManager companyManager,
                                TagManager tagManager) {
        writeUserManager(userManager);
        writePostings(postingManager);
        writeCompanies(companyManager);
        writeTagPool(tagManager);
        System.out.println();
    }

    /**
     * serialize userManager
     * @param userManager this
     */
    private static void writeUserManager(UserManager userManager) {
        serialize(userManager, usersFilename);
    }

    /**
     * serialize tagPool
     * @param tagPool this
     */
    private static void writeTagPool(TagManager tagPool) {
        serialize(tagPool, tagsFilename);
    }

    /**
     * serialize postingManager
     * @param postingManager this
     */
    private static void writePostings(PostingManager postingManager) {
        serialize(postingManager, postingsFilename);
    }

    /**
     * serialize companyManager
     * @param companyManager this
     */
    private static void writeCompanies(CompanyManager companyManager) {
        serialize(companyManager, companiesFilename);
    }

    /**
     * serialize given any object and filepath
     * @param object this object
     * @param filepath this filepath
     */
    private static void serialize(Object object, String filepath) {
        try {
            //Saving of object in a file
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            // Method of serializing of an object
            out.writeObject(object);

            out.close();
            fileOut.close();

            System.err.println("Serialization complete! " + object.getClass().getName() + " is saved in " + filepath);

        } catch (IOException ex) {
//            ex.printStackTrace();
            if (usersFilename.contains("phase2")) {
                usersFilename = usersFilename.substring(7);
                postingsFilename = postingsFilename.substring(7);
                companiesFilename = companiesFilename.substring(7);
                tagsFilename = tagsFilename.substring(7);

                serialize(object, filepath.substring(7));

            } else {
                ex.printStackTrace();
            }
        }
    }

    /**
     * deserialize
     * @param clazz this class
     * @param filepath this filepath
     * @param <T> this object
     * @return deserialization
     */
    @SuppressWarnings({"unchecked", "SpellCheckingInspection"})
    private static <T extends Manager> T deserialize(Class clazz, String filepath) {
        try {
            // Reading the object from the file
            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            // Method for deserializing of an object
            T obj = (T) in.readObject();

            in.close();
            fileIn.close();

            System.err.print(clazz.getName() + " deserialization complete! ");

            return obj;

        } catch (FileNotFoundException ex) {

            if (!usersFilename.contains("phase2")) {
                usersFilename = "phase2/" + usersFilename;
                postingsFilename = "phase2/" + postingsFilename;
                companiesFilename = "phase2/" + companiesFilename;
                tagsFilename = "phase2/" + tagsFilename;
                return deserialize(clazz, "phase2/" + filepath);
            } else {
                return createDemoData(clazz);
            }


        } catch (IOException | ClassNotFoundException ex) {
            return createDemoData(clazz);
        }
    }


    /**
     * If the any of the following groups of objects is empty or deleted, then create a demo and save it.
     * THIS METHOD IS ONLY FOR TESTING PURPOSE
     */
    @SuppressWarnings({"SpellCheckingInspection", "unchecked"})
    private static <T extends Manager> T createDemoData(Class clazz) {
        System.err.println(clazz + " serialization file not found. A demo data will now be instantiated");

        try {
            Object obj = clazz.newInstance();

            Interviewer i = new Interviewer("2", "2", riot);
            HRCoordinator h = new HRCoordinator("3", "3", riot);


            Applicant tony = new Applicant("1", "1");
            tony.setDocument("cover letter", "abc");
            tony.setDocument("resume", "abc");
            // ADD RIOT
            PostingInfo postingInfo = new PostingInfo("Riot", "Assistance", 1,
                    LocalDate.now(), LocalDate.now().plusDays(30), riot);
            ArrayList<Tag> lol = new ArrayList<>();
            lol.add(new Tag("LEAGUE"));
            lol.add(new Tag("LEGEND"));
            String[] interviews = new String[2];
            interviews[0] = "In person interview";
            interviews[1] = "In person interview 2";
            Posting faceBook = new Posting(postingInfo, interviews, lol);
            faceBook.getRequirement().getRequirement().add("Speak Korean");
            faceBook.getRequirement().getRequirement().add("Challenger in league");
            tony.applyJob(faceBook);
            h.matchInterviewer(tony, i, faceBook);
            if (clazz.equals(UserManager.class)) {
                ((UserManager) obj).add(tony);
                ((UserManager) obj).add(i);
                ((UserManager) obj).add(h);
                ((UserManager) obj).add(new Applicant("truboy", "1234"));
                ((UserManager) obj).add(new Applicant("tolo", "1234"));
                ((UserManager) obj).add(new HRCoordinator("leon", "1234", amazon));
                ((UserManager) obj).add(new HRCoordinator("arjun", "1234", tesla));
                ((UserManager) obj).add(new HRCoordinator("tony", "1234", google));
                ((UserManager) obj).add(new Interviewer("ekko", "1234", amazon));
                ((UserManager) obj).add(new Interviewer("jason", "1234", tesla));
                ((UserManager) obj).add(new Interviewer("ashu", "1234", google));

            } else if (clazz.equals(CompanyManager.class)) {
                ((CompanyManager) obj).add(riot);
                ((CompanyManager) obj).add(amazon);
                ((CompanyManager) obj).add(tesla);
                ((CompanyManager) obj).add(google);
            } else if (clazz.equals(TagManager.class)) {
                ((TagManager) obj).add(lol.get(0));
                ((TagManager) obj).add(lol.get(1));
                ((TagManager) obj).add(new Tag("Internship"));
                ((TagManager) obj).add(new Tag("FullTime"));
                ((TagManager) obj).add(new Tag("PartTime"));
                ((TagManager) obj).add(new Tag("$15-20"));
                ((TagManager) obj).add(new Tag("$20-25"));
                ((TagManager) obj).add(new Tag("$25+"));
                ((TagManager) obj).add(new Tag("Summer"));

            } else if (clazz.equals(PostingManager.class)) {
                String[] interviewStageTemplate = new String[]{
                        "Phone interview",
                        "First in-person interview",
                        "Second in-person interview",
                };
                ArrayList<Tag> summerInternship = new ArrayList<>();
                summerInternship.add(new Tag("Internship"));
                summerInternship.add(new Tag("Summer"));
                ArrayList<Tag> fullTime15 = new ArrayList<>();
                fullTime15.add(new Tag("fulltime"));
                fullTime15.add(new Tag("$15-20"));
                ArrayList<Tag> parttime25 = new ArrayList<>();
                parttime25.add(new Tag("$25+"));
                parttime25.add(new Tag("parttime"));
                // ADD AMAZON
                PostingInfo postingInfo1 = new PostingInfo("Amazon", "Backend", 1,
                        LocalDate.now(), LocalDate.now().plusDays(30), amazon);
                Posting posting1 = new Posting(postingInfo1, interviewStageTemplate, summerInternship);
                posting1.getRequirement().getRequirement().add("Speak English");
                posting1.getRequirement().getRequirement().add("American Citizenship");
                ((PostingManager) obj).add(posting1);
                // ADD TESLA
                PostingInfo postingInfo2 = new PostingInfo("Tesla", "Auto Mobile", 2,
                        LocalDate.now(), LocalDate.now().plusDays(30), tesla);
                Posting posting2 = new Posting(postingInfo2, interviewStageTemplate, fullTime15);
                posting2.getRequirement().getRequirement().add("Driver License");
                posting2.getRequirement().getRequirement().add("At least 2 years of experience");
                ((PostingManager) obj).add(posting2);
                // ADD GOOGLE
                PostingInfo postingInfo3 = new PostingInfo("Google", "Artificial Intelligence", 3,
                        LocalDate.now(), LocalDate.now().plusDays(30), google);
                ((PostingManager) obj).add(new Posting(postingInfo3, interviewStageTemplate, parttime25));
                // ADD FACEBOOK
                ((PostingManager) obj).add(faceBook);
            }
            return (T) obj;

        } catch (IllegalAccessException | InstantiationException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

