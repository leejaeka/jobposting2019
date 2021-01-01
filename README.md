# CSC207 Job Network App

## NOTE
- For two extra features required for the project, we chose to do partial GUI + notification system
+ handling documents and rating them
- Note that our program have partial GUI meaning will have to switch from GUI to text and text to GUI
- frequently through out the program

## SETUP
Everything will be automatically setup if you imported Maven module during the cloning process (watch **How_to_setup.mp4**); if you did not, follow the steps below:
- Go to `Project Structure` (Ctrl+Alt+Shift+S)

    - Go to `Project`, set:
        - `Project JDK` to **1.8**
        - `Project language level` to **8 - Lambdas**
        - `Project compiler output` to anywhere
    - Go to `Modules`
        - Select the existing module, press **-** (Delete) to remove default module from project. 
        - press **+** (Alt+Ins), then select `Import Module`, select `phase2` folder, import from `Maven`. Keep pressing **next**, until you press **finish**.


## BASIC
- To start the program, simply run `Project.Main.main()`
- To interact with the program, enter the corresponding option (usually _numeral_ or _(Y/N)_)
- To exit the program, type `exit` at any time


## DESIGN
- Options are based on user type, either Project.Model.Applicant or Project.Model.Employee (Project.Model.Interviewer or Project.Model.HRCoordinator).

## SERIALIZATION
- `Project.Manager.Project.Manager.UserManager` and `Project.Manager.Project.Manager.PostingManager` will be deserialize when program is initially started; 
- Serialized when a user logout/exit.
- Serialization filepath is set on Line 8-9 in `Project.SaveManager` class
- ```java 8
  private static String USERS_FILEPATH = "resource/users.ser";
  private static String POSTINGS_FILEPATH = "resource/postings.ser";
  ```
- Make sure to **delete** the serialization files after modifying any classes due to update of _serialVersionUID_.

## LOGIN
- Creating new account
    - enter a new username and password and press register
    - for next step choose an account type and press complete button to create
- Logging into existing account
    - Saves once logged in accounts so when someone tries to login again after logout
    - it would show a small scroll down menu with username
    - Incorrect password will not be able to log in. Note that there are no recovering lost
    - passwords

## APPLICANT
- Completely GUI based
- View and apply jobs
    - Can view every postings each posting in row
    - Columns have name, description, position available, post date, end date , tags, requirements, apply
    - Note that sometimes you have to click button twice for action to happen
    - Filter bar is at the top you can click the '?' button for more explanation
    - Also currently applied filter tags are displayed at the bottom
- Manage documents
    - easy to read, use layout
- View status
    - Lists all applied jobs, can click buttons to view more in detail or withdraw
- Go 15 days into future & Initialize date closed
    - purely for the TA's to help marking. Date related functionaries
    - 15days into future sets date closed minus 15 days so it's safe to use
- change password
    - regular change password system
- Account info
    - account created on
    - past job application (displays all not just first few)
    - current application (displays all not just first few)
    - number of days since last closed : days left till documents get automatically removed from system

## INTERVIEWER
- Mostly text based (not GUI)
    - check recommendation list
    - add applicant to recommendation list
    - view interviews scheduled for me
    - interview
        - will have to choose for which posting first and then choose applicant and then say whether they can
        - go to the next interview stage or not (pass or not)
        - Also notifies applicant that they passed (the next time they log in)

## HRCOORDINATOR
- Mostly text based (not GUI)
    - note that once interviewer interviews HRCoordinator has to match another interviewer
    - for the interviewed applicant's next interview stage if there is one
