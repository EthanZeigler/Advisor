package components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ethan on 10/4/17.
 */
public class Section {
    private String sectionCode;
    private String pawsCode;
    private String profName;
    private Schedule schedule;
    private List<Section> dependencies;
    private int seatsRemaining;
    private boolean isOpen;

    private Course parent;

    public Section(String sectionCode, String profName, Schedule schedule) {
        this.sectionCode = sectionCode;
        this.profName = profName;
        this.schedule = schedule;
        this.dependencies = new ArrayList<>();
    }

    public Section(String sectionCode, String profName, String room) {
        this(sectionCode, profName, new Schedule());
    }

    public Section() {
        this ("", "", "");
    }

    /**
     * Gets the Section code. This is a unique identifier and must be a duplicate of another.
     * @return Section code
     */
    public String getSectionCode() {
        return sectionCode;
    }

    public String getDependencyPrettyPrint() {
        StringBuilder builder = new StringBuilder();
        dependencies.forEach(section -> builder.append(", ").append(section.getSectionCode()));
        return builder.toString();
    }

    /**
     * Sets the section code. This is a unique identifier and must not be a duplicate of another.
     * @param sectionCode section code
     */
    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    /**
     * Gets the professor's name
     * @return professor's name
     */
    public String getProfName() {
        return profName;
    }

    /**
     * Sets the professor's name
     * @param profName professor's name
     */
    public void setProfName(String profName) {
        this.profName = profName;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public int getSeatsRemaining() {
        return seatsRemaining;
    }

    public void setSeatsRemaining(int seatsRemaining) {
        this.seatsRemaining = seatsRemaining;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public Course getParent() {
        return parent;
    }

    public void setParent(Course parent) {
        this.parent = parent;
    }

    /**
     * Adds an event to the section
     * @param event event to add
     * @return if the event was added, and therefore doesn't conflict
     */
    public boolean addEvent(Event event) {
        return schedule.addEvent(event);
    }

    public void addAllEvents(Collection<Event> events) {
        for (Event event : events) {
            schedule.addEvent(event);
        }
    }

    public boolean addDependency(Section section) {
        if (this.schedule.merge(section.getSchedule())) {
            dependencies.add(section);
            return true;
        }
        return false;
    }

    public boolean removeDependency(Section section) {
        return dependencies.remove(section);
        // fixme remove dependent events
    }

    public void setParentCourse(Course course) {
        this.parent = course;
    }

    public Course getParentCourse() {
        return this.parent;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public String getPawsCode() {
        return pawsCode;
    }

    public void setPawsCode(String pawsCode) {
        this.pawsCode = pawsCode;
    }
}
