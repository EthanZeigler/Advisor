import components.Course;
import components.Event;
import components.Section;
import org.apache.commons.collections.MapUtils;

import java.io.*;
import java.util.*;
import java.time.DayOfWeek;

/**
 * Created by Ethan on 11/20/17.
 */
public class PawsParser {
    public static void main(String[] args) throws IOException {
        Thread memT = new Thread(() -> {
            while(true) {
                System.err.printf("%sM total, %sM free\n", Runtime.getRuntime().totalMemory()/(1000000) + "", Runtime.getRuntime().freeMemory()/(1000000) + "");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
            }
        });
        memT.setDaemon(true);
        memT.start();
        ScheduleMasterCLI.main(args);
    }

    private static void badDataTerminate(String line) {
        System.out.println("-> Error: Unexpected information found when analyzing shopping cart");
        System.out.println("Reason: " + line);
        System.out.println("[FATAL] Terminating");
        System.exit(1);
    }

    public static Collection<Course> parseData(String input) throws IOException {



        // isolate classes
        Iterator<String> lines = Arrays.asList(input.split("Select\\s+Class\\s+Days/Times\\s+Room" +
                "\\s+Instructor\\s+Units\\s+Available Seats\\s+Status")[1]
                .split("delete")[0].trim().split("\n")).iterator();

        HashMap<String, Course> courseMap = new HashMap<>();

        Course lastUsedCourse = null;
        Section lastUsedSection = null;

        courseDecoder:
        while (lines.hasNext()) {
            System.out.println("preprocess");
            String line;
            do {
                // reached the end of the file
                if (!lines.hasNext()) {
                    break courseDecoder;
                }
                line = lines.next().trim();
            } while (!line.matches("[A-Za-z]{2,4}\\s[0-9]+-[A-Za-z0-9]+"));

            //badDataTerminate("Expected a class ID, but I don't recognize this.\n" +
            //"Data: " + line);

            String courseName = line.split("-")[0];
            String sectionCode = line.split("-")[1];
            Course course = courseMap.get(courseName);
            String profName;
            String pawsCode;
            double unitCount;
            int seatsLeft = 99999;
            boolean isUninitialized = false;
            boolean isOpen = true;

            if (course == null) {
                course = new Course();
                courseMap.put(courseName, course);
                isUninitialized = true;
            }

            if (isUninitialized) {
                course.setName(courseName);
                course.setClassCode(courseName);
            }

            // go to paws code
            line = lines.next().trim();
            if (!line.matches("\\([0-9]+\\)")) {
                badDataTerminate("Expected a PAWS code, but I don't recognize this.\n" +
                        "Data: " + line);
            }
            pawsCode = line.substring(1, line.length() - 1);
            line = lines.next().trim();

            List<String> rawTimes = new ArrayList<>(5);
            List<String> locations = new ArrayList<>(5);
            while (line.matches("((Mo|Tu|We|Th|Fr)+) [0-9]+:[0-9]{2}(AM|PM) - [0-9]+:[0-9]{2}(AM|PM)")) {
                rawTimes.add(line);
                line = lines.next().trim();
            }
            for (int i = 0; i < rawTimes.size(); i++) {
                locations.add(line);
                line = lines.next().trim();
            }
            profName = line;
            if (profName.endsWith(",")) {
                profName = profName.concat(lines.next().trim());
            }
            line = lines.next().trim();

            if (!line.matches("([0-9]\\.[0-9]{2,})?")) {
                badDataTerminate("Expected a unit number, but I don't recognize this.\n" +
                        "Data: " + line);
            }

            boolean isDependent = false;
            if (line.isEmpty() && lastUsedCourse != null) {
                isDependent = true;
                unitCount = 0;
            } else if (line.isEmpty()) {
                System.out.println("-> Something is wrong here. I think I found a lab as the first entry," +
                        " but that shouldn't be possible. Go take a look at what you copy pasted. The third to last " +
                        "line in each class you pasted should be a number if it is a lecture, and empty " +
                        "if it's a lab. I can't understand what's happening if you start with a lab. For now, I'm" +
                        "going to skip this entry. Course Name = " + courseName + ", Section Code = " + pawsCode);
                continue;
            } else {
                unitCount = Double.parseDouble(line);
            }

            line = lines.next().trim();

            try {
                seatsLeft = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                badDataTerminate("Expected a count of how many seats are left, but I don't recognize this.\n" +
                        "Data: " + line);
            }

            line = lines.next().trim();

            if (line.equalsIgnoreCase("Closed")) {
                System.out.println("-> I found a closed class. It might not be helpful when making schedules.");
                isOpen = false;
            } else if (line.equalsIgnoreCase("Open")) {
                isOpen = true;
            } else {
                badDataTerminate("I was expecting the word 'open' or 'closed', but I didn't recognize this\n" +
                        "Data: " + line);
            }

            course.setClassCode(courseName);

            // calculate times.
            // when we have multiple days of the week at the start, we need to make multiple sessions.
            // I really, really don't feel like coding this rn. What am I doing with my friday?

            // create the section structure
            Section section = new Section();
            section.setParentCourse(course);
            section.setProfName(profName);
            section.setSectionCode(sectionCode);
            section.setPawsCode(pawsCode);
            section.setOpen(isOpen);
            section.setSeatsRemaining(seatsLeft);

            List<Event> events = new ArrayList<>();

            for (int i = 0; i < rawTimes.size(); i++) {
                // get event entry
                String timeLine = rawTimes.get(i);
                // split line into tokens where 0 is days, 1 is start time, and 3 is end time
                String[] timeTokens = timeLine.split("\\s");
                // get corresponding location
                String location = locations.get(i);

                // decide times
                int startHour, endHour, startMinute, endMinute;
                String[] tokenizedTimeToken = timeTokens[1].split(":");
                startHour = Integer.parseInt(tokenizedTimeToken[0]);
                startMinute = Integer.parseInt(tokenizedTimeToken[1].substring(0, 2));
                if (tokenizedTimeToken[1].substring(2).equalsIgnoreCase("PM") && startHour != 12) {
                    startHour += 12;
                }

                // and now the end
                tokenizedTimeToken = timeTokens[3].split(":");

                endHour = Integer.parseInt(tokenizedTimeToken[0]);
                endMinute = Integer.parseInt(tokenizedTimeToken[1].substring(0, 2));
                if (tokenizedTimeToken[1].substring(2).equalsIgnoreCase("PM") && endHour != 12) {
                    endHour += 12;
                }

                // break apart multiple days
                for (int j = 0; j < timeTokens[0].length() / 2; j++) {
                    Event event = new Event();
                    switch (timeTokens[0].substring(j * 2, j * 2 + 2).toUpperCase()) {
                        case "MO": event.setDayOfWeek(DayOfWeek.MONDAY);break;
                        case "TU": event.setDayOfWeek(DayOfWeek.TUESDAY);break;
                        case "WE": event.setDayOfWeek(DayOfWeek.WEDNESDAY);break;
                        case "TH": event.setDayOfWeek(DayOfWeek.THURSDAY);break;
                        case "FR": event.setDayOfWeek(DayOfWeek.FRIDAY);break;
                    }
                    // set event fields

                    // this is going to need a hella lot of refactoring, but I'm in a rush here right now.
                    event.setStartHour(startHour);
                    event.setStartMinute(startMinute);
                    event.setEndHour(endHour);
                    event.setEndMinute(endMinute);
                    event.setLocation(location);
                    event.setParentSection(section);

                    events.add(event);
                }
                section.addAllEvents(events);
            }
            if (isDependent) {
                lastUsedSection.addDependency(section);
            } else {
                course.addSection(section);
            }
            lastUsedCourse = course;
            lastUsedSection = section;
        }
        System.out.println("Parse Complete");


//        MapUtils.debugPrint(System.out, "myMap", courseMap);
        return courseMap.values();


        /* identifying
        line 0: Code CourseNum-section
        line 1: (section PAWS code)
        line 2x: MoTuWeThFr dd:dd(starttime)AM/PM, -, dd:dd(starttime)AM/PM
        line 2x: MoTuWeThFr dd:dd(starttime)AM/PM, -, dd:dd(starttime)AM/PM
        line 2x: MoTuWeThFr dd:dd(starttime)AM/PM, -, dd:dd(starttime)AM/PM
        line 2x: MoTuWeThFr dd:dd(starttime)AM/PM, -, dd:dd(starttime)AM/PM
        line 3x: [A-Za-z]+[0-9]
        line 3x: [A-Za-z]+ word[0-9]
        line 3x: [A-Za-z]+ word[0-9]
        line 3x: [A-Za-z]+ word[0-9]
        line 4: (Staff)|([A-Z]\.\\s[A-Za-z]+
        line 5:  [
         */
    }
}
