import com.ethanzeigler.utils.KeyboardReader;
import components.Course;
import components.Schedule;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Ethan on 10/4/17.
 */
public class ScheduleMasterCLI {

    public static String main(String[] args) throws IOException {
        List<Course> courses;

        KeyboardReader cli = new KeyboardReader();
//        Yaml yaml = new Yaml();
//        System.out.println("Opening file...");
//
//        // check if given arg
//        if (args.length == 0) {
//            quitBadFile();
//        }
//
//        // check if file exits
//        File file = new File(args[0]);
//        if (!file.exists()) {
//            quitBadFile();
//        }
//
//        Map<String, Object> yamlData = null;
//        // check if file is valid yaml
//        try {
//            yamlData = (Map<String, Object>) yaml.load(new FileInputStream(file));
//        } catch (Exception e) {
//            e.printStackTrace();
//            quitBadFile();
//        }
//
//        System.out.println("Loading file... ");
//
//        // load file. If there are any semantic issues, will quit.
//
//        YAMLSerialization.deserialize(courses, yamlData);

        // loaded information successfully
        // begin generation
        System.out.println("Loaded file successfully... ");
        if (args.length > 0) {
            System.out.println("Parsing from server source: " + args[0]);
            courses = new ArrayList<>(PawsParser.parseData(args[0]));
        } else {
            courses = new ArrayList<>(PawsParser.parseData(null));
        }
        //int kPower = cli.readInt("Enter the number of courses you are taking", 1, 10);
        int kPower = 4;
        Set<Schedule> schedules = ScheduleGenerator.generateSchedules(courses, kPower);
        System.out.println("Building output");

        StringBuilder builder = new StringBuilder("Here are your possible schedules:\n\n");
        //System.out.println("Here are your possible schedules:\n\n");

        Iterator<Schedule> iterator = schedules.iterator();

        AtomicInteger counter = new AtomicInteger(1);
        iterator.forEachRemaining(schedule -> {
            builder.append("Schedule ").append(counter).append(":").append("\n");
            builder.append(schedule.prettyPrint());
            counter.getAndIncrement();
        });
        return builder.toString();
    }

    public static void quitBadFile() {
        System.out.println("No file given, no existing file, or corrupt file.");
        System.exit(0);
    }

    public static void quitMissingData() {
        System.out.println("Something went wrong when loading the file");
        System.exit(0);
    }
}
