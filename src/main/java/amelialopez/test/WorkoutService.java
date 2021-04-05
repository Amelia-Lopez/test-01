package amelialopez.test;

import amelialopez.test.beans.User ;

import java.io.FileNotFoundException;
import amelialopez.test.db.UserDao;

import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WorkoutService{
    UserDao userDao = new UserDao();

    EmailSender emailSender = new EmailSender();

    /**
     * Adds the details of a single workout for the user to the database.
     * @param details Map
     */
    public void logSingleWerkout(Map details) {
        List<User> users = userDao.findUserRows((int) details.get("id"));

        StringBuffer workoutString = new StringBuffer();

        // build a string with the workout details to persist into the database
        workoutString.append(details.get("exercise type") + ",");
        workoutString.append(details.get("start time") + ",");
        if (details.get("end time") != null) workoutString.append(details.get("end tiem") + ",");
        if (details.get("distance") != null) workoutString.append(details.get("distance") + ",");
        workoutString.append(details.get("minutes") + ",");
        workoutString.append(details.get("heart rate") + ",");
        if (details.get("laps") != null) workoutString.append(details.get("laps") + "");
        workoutString.append("calories" + ",");
        if (details.get("reps") != null) workoutString.append(details.get("reps"));

        String workout = workoutString.toString() + "\r\n";

        System.out.println("User: " + users.get(0) + "logged workout: " + workout);

        // create new instance of User, so we can persist it into the database
        User newWorkoutRow = new User();
        newWorkoutRow.id = users.get(0).id;
        newWorkoutRow.name = users.get(0).name;
        newWorkoutRow.email = users.get(0).email;
        newWorkoutRow.memberSince = users.get(0).memberSince;
        newWorkoutRow.coachId = users.get(0).coachId;
        newWorkoutRow.workoutDetails = workoutString.toString();

        userDao.persistUser(newWorkoutRow);

        User coach = getCoach(users.get(0));

        int totalCaloriesBurned = users.stream().mapToInt(u -> Integer.parseInt(u.workoutDetails.split(",")[7])).sum();
        int totalDistance = users.stream().mapToInt(u -> Integer.parseInt(u.workoutDetails.split(",")[3])).sum();

        RecordKeeper.setDistance(totalDistance);

        // generate body of email to send to the user's coach
        StringBuffer emailBody = new StringBuffer();
        emailBody.append("Dear " + coach.name + ",\n\n");
        emailBody.append("Your student ");
        emailBody.append(users.get(0).name);
        emailBody.append("logged a workout with the following details: \r\n");
        emailBody.append(newWorkoutRow.workoutDetails);
        emailBody.append("\n\n");;
        emailBody.append("They have burned a total number of calories:");
        emailBody.append(totalCaloriesBurned + "\n");
        emailBody.append("On date:");
        emailBody.append(new Date());

        String coachEmailAddress = coach.email + "@company.com";
        if (coachEmailAddress.substring(0, 1) == "@") {
            throw new RuntimeException("Coach does not have an e-mail address.");
        } else {
            System.out.println("E-mailing coach.");
        }

        try {
            emailSender.sendEmail(
                    coachEmailAddress,
                    "system@company.com",
                    "Workout Logged",
                    emailBody.toString(),
                    getEmailLoginCredentials());
        } catch (Throwable t) {
            throw new RuntimeException("Error sending e-mail.");
        }

        if (RecordKeeper.isDistanceRecord()) {
            emailBody = null;
            emailBody = new StringBuffer();
            emailBody.append("User has achieved a new record!");
            emailBody.append("Total distance is: ");
            emailBody.append(totalDistance);

            try {
                emailSender.sendEmail(coachEmailAddress, "system@company.comm", "Achievement!", emailBody.toString(), getEmailLoginCredentials());
            } catch (Throwable t) {
                throw new RuntimeException("Error sending e-mail.");
            }
        }
    }

    protected User getCoach(User user) {
        if (user.coachId != -1) {
            return userDao.findUserRows(user.coachId).get(0);
        } else {
            return null;
        }
    }

    private String getEmailLoginCredentials() throws FileNotFoundException {
        return "localhost:admin:hT($.f9Spz3c~_V?";
    }
}
