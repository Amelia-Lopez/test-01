package amelialopez.test;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class WorkoutServiceTest {

    @Test
    public void verifyNoErrors() {
        Map<String, String> workoutDetails = new HashMap<>();
        workoutDetails.put("id", "1234");
        workoutDetails.put("exercise type", "running");
        workoutDetails.put("start time", "2016/08/09 16:42");
        workoutDetails.put("distance", "6");
        workoutDetails.put("minutes", "45");
        workoutDetails.put("heart rate", "132");
        workoutDetails.put("calories", "172");

        new WorkoutService().logSingleWerkout(workoutDetails);
    }
}
