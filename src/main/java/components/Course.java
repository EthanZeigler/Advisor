package components;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ethan on 10/4/17.
 */
public class Course {
    private String name;
    private String classCode;
    private List<Section> sections;
    private List<String> dependencies;

    /**
     * Represents a course
     * @param name name of the course
     * @param classCode class code
     * @param sections list of sections within the course
     * @param dependencies list of other courses that must be with this course in a schedule
     */
    public Course(String name, String classCode, List<Section> sections, List<String> dependencies) {
        this.name = name;
        this.classCode = classCode;
        this.sections = sections;
        this.dependencies = dependencies;
    }

    /**
     * Represents a course
     * @param name name of the course
     * @param classCode class code
     */
    public Course(String name, String classCode) {
        this(name, classCode, new ArrayList<Section>(), new ArrayList<String>());
    }

    /**
     * Represents a course
     */
    public Course() {
        this("", "");
    }

    /**
     * Gets the course name
     * @return the course name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the course name
     * @param name course name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the class code
     * @return class code
     */
    public String getClassCode() {
        return classCode;
    }

    /**
     * Sets the class code
     * @param classCode class code
     */
    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    /**
     * Gets the course's sections
     * @return the course's sections
     */
    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    /**
     * Sets the course's sections
     * @param sections course's sections
     */
    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    /**
     * Gets the course's dependencies
     * @return course's dependencies
     */
    public List<String> getDependencies() {
        return dependencies;
    }

    /**
     * Sets the course's dependencies
     * @param dependencies course's dependencies
     */
    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public void addDependency(String dependency) {
        dependencies.add(dependency);
    }

    @Override
    public String toString() {
        return "Course{" +
                "name='" + name + '\'' +
                ", classCode='" + classCode + '\'' +
                ", sections=" + sections +
                ", dependencies=" + dependencies +
                '}';
    }
}
