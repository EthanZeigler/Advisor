import components.Course;
import components.Event;
import components.Schedule;
import components.Section;

import javax.xml.stream.events.EndDocument;
import java.util.List;
import java.util.Map;
import java.time.DayOfWeek;

/**
 * Created by Ethan on 10/4/17.
 */
public class YAMLSerialization {
    private YAMLSerialization() {}

    public static void deserialize(List<Course> courses, Map<String, Object> data) {
        // check if contains course key
        if (!data.containsKey("courses")) {
            System.out.println("Can't load file. \"courses\" section is missing");
            ScheduleMasterCLI.quitMissingData();
        }

        Map<String, Object> courseData = (Map<String, Object>) data.get("courses");

        // check if there's actually courses in here...
        if (courseData.size() == 0) {
            System.out.println("Can't load file. \"courses\" section has no courses");
            ScheduleMasterCLI.quitMissingData();
        }

        for (Map.Entry<String, Object> entry : courseData.entrySet()) {
            deserializeCourse(entry.getKey(), courses, (Map<String, Object>) entry.getValue());
        }
    }

    private static void deserializeCourse(String courseName, List<Course> courses, Map<String, Object> data) {
        if (!data.containsKey("sections")) {
            System.out.printf("Course %s doesn't have a \'sections\' section%n", courseName);
            ScheduleMasterCLI.quitMissingData();
        }
        Course course = new Course();
        course.setName(courseName);
        course.setClassCode(courseName);

        if (data.containsKey("class_code")) {
            try {
                course.setClassCode((String) data.get("class_code"));
            } catch (ClassCastException e) {
                course.setClassCode(Integer.toString((Integer) data.get("class_code")));
            }
        }

        if (data.containsKey("dependencies")) {
            course.setDependencies((List<String>) data.get("dependencies"));
        }

        Map<String, Object> sectionData = (Map<String, Object>) data.get("sections");
        for (Map.Entry<String, Object> section : sectionData.entrySet()) {
            deserializeSection(section.getKey(), course, (Map<String, Object>) section.getValue());
        }
        courses.add(course);
    }

    private static void deserializeSection(String sectionName, Course course, Map<String, Object> data) {
        Section section = new Section();
        if (!data.containsKey("professor")) {
            System.out.printf("Course \'%s\' section \'%s\' does not have a listed \'professor\'%n", course.getName(), sectionName);
            ScheduleMasterCLI.quitMissingData();
        }
        section.setSectionCode(sectionName);
        section.setProfName((String) data.get("professor"));

        if (!data.containsKey("times")) {
            System.out.printf("Course \'%s\' section \'%s\' does not have a listed \'times\' section%n", course.getName(), sectionName);
            ScheduleMasterCLI.quitMissingData();
        }

        deserializeTimes(course, section, (List<Map<String, Object>>) data.get("times"));

        section.setParentCourse(course);
        course.addSection(section);

    }

    private static void deserializeTimes(Course course, Section section, List<Map<String, Object>> data) {
        Schedule schedule = new Schedule();

        for (Map<String, Object> entry : data) {
            if (!entry.containsKey("day")) {
                System.out.printf("Course \'%s\' section \'%s\' \'times\' section has a missing \'day\' field%n", course.getName(), section.getSectionCode());
                ScheduleMasterCLI.quitMissingData();
            }

            if (!entry.containsKey("start")) {
                System.out.printf("Course \'%s\' section \'%s\' \'times\' section has a missing \'start\' field%n", course.getName(), section.getSectionCode());
                ScheduleMasterCLI.quitMissingData();
            }

            if (!((Map<String, Object>)entry.get("start")).containsKey("hour")) {
                System.out.printf("Course \'%s\' section \'%s\' \'times\' section has a missing \'hour\' field%n", course.getName(), section.getSectionCode());
                ScheduleMasterCLI.quitMissingData();
            }

            if (!((Map<String, Object>)entry.get("start")).containsKey("min")) {
                System.out.printf("Course \'%s\' section \'%s\' \'times\' section has a missing \'min\' field%n", course.getName(), section.getSectionCode());
                ScheduleMasterCLI.quitMissingData();
            }

            if (!entry.containsKey("end")) {
                System.out.printf("Course \'%s\' section \'%s\' \'times\' section has a missing \'end\' field%n", course.getName(), section.getSectionCode());
                ScheduleMasterCLI.quitMissingData();
            }

            if (!((Map<String, Object>)entry.get("end")).containsKey("hour")) {
                System.out.printf("Course \'%s\' section \'%s\' \'times\' section has a missing \'hour\' field%n", course.getName(), section.getSectionCode());
                ScheduleMasterCLI.quitMissingData();
            }

            if (!((Map<String, Object>)entry.get("end")).containsKey("min")) {
                System.out.printf("Course \'%s\' section \'%s\' \'times\' section has a missing \'min\' field%n", course.getName(), section.getSectionCode());
                ScheduleMasterCLI.quitMissingData();
            }

            // all data should be okay now

            DayOfWeek day = DayOfWeek.valueOf(((String)entry.get("day")).toUpperCase());
            int startHour = (int) ((Map<String, Object>) entry.get("start")).get("hour");
            int startMin = (int) ((Map<String, Object>) entry.get("start")).get("min");
            int endHour = (int) ((Map<String, Object>) entry.get("end")).get("hour");
            int endMin = (int) ((Map<String, Object>) entry.get("end")).get("min");

            if ((((startHour * 60) + startMin) - ((endHour * 60) + endMin) >= 0)) {
                System.out.printf("Course \'%s\' section \'%s\' \'times\' section ends before it starts%n", course.getName(), section.getSectionCode());
                ScheduleMasterCLI.quitMissingData();
            }


            Event event = new Event(day, startHour, startMin, endHour, endMin, "na");
            event.setParentSection(section);
            section.addEvent(event);
        }
    }
}
