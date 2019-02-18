package components;

import java.time.DayOfWeek;

/**
 * Created by Ethan on 10/4/17.
 */
public class Event implements Comparable {


    private DayOfWeek dayOfWeek;
    private String location;
    private int startHour;
    private int startMinute;

    private int endHour;
    private int endMinute;

    private Section parent;

    /**
     * Represents a calendar event using 24 hour time.
     * @param startHour the start hour
     * @param startMinute the start minute
     * @param endHour the end hour
     * @param endMinute the end minute
     */
    public Event(DayOfWeek dayOfWeek, int startHour, int startMinute, int endHour, int endMinute, String location) {
        this.dayOfWeek = dayOfWeek;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.location = location;
    }

    /**
     * Represents a calendar event using 24 hour time using default start end both of
     * 12:00 AM on sunday
     */
    public Event() {
        this(DayOfWeek.SUNDAY, 0,0, 0,0, "Unspecified");
    }

    /**
     * Gets the start hour
     * @return start hour
     */
    public int getStartHour() {
        return startHour;
    }

    /**
     * Sets the start hour
     * @param startHour start hour
     */
    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    /**
     * Gets the start minute
     * @return start minute
     */
    public int getStartMinute() {
        return startMinute;
    }

    /**
     * Sets the start minute
     * @param startMinute start minute
     */
    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    /**
     * Gets the end hour
     * @return the end hour
     */
    public int getEndHour() {
        return endHour;
    }

    /**
     * Sets the end hour
     * @param endHour end hour
     */
    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    /**
     * Gets the end minute
     * @return end minute
     */
    public int getEndMinute() {
        return endMinute;
    }

    /**
     * Sets the end minute
     * @param endMinute end minute
     */
    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public void setParentSection(Section section) {
        this.parent = section;
    }

    public Section getParentSection() {
        return this.parent;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Section getParent() {
        return parent;
    }

    public void setParent(Section parent) {
        this.parent = parent;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns whether or not the event collides with the given event
     * @param event event to check for collision
     * @return whether or not the event collides with the given event
     */
    public boolean collidesWith(Event event) {
        int eventOneStart = (event.getStartHour() * 60) + event.getStartMinute();
        int eventOneEnd = (event.getEndHour() * 60) + event.getEndMinute();
        int eventTwoStart = (startHour * 60) + startMinute;
        int eventTwoEnd = (endHour * 60) + endMinute;

        if (event.dayOfWeek == dayOfWeek) {
            return (eventOneStart > eventTwoStart && eventOneStart < eventTwoEnd) ||
                    (eventOneEnd > eventTwoStart && eventOneEnd < eventTwoEnd);
        } else {
            return false;
        }
    }

    public String prettyPrint() {
        StringBuilder builder = new StringBuilder();
        writePrettyTime(builder, getStartHour(), getStartMinute());
        builder.append(" - ");
        writePrettyTime(builder, getEndHour(), getEndMinute());
        builder.append(" ").append(getParentSection().getParentCourse().getName()).append("\n")
            .append("  ").append(getLocation()).append(" w/ ").append(getParentSection().getProfName()).append("\n");

        return builder.toString();
    }

    private static void writePrettyTime(StringBuilder builder, int hour, int min) {
        if (hour == 0) {
            builder.append("12:");
        } else {
            builder.append(hour).append(":");
        }

        if (min + 1 < 10) {
            builder.append(0);
        }
        builder.append(min + 1);

        if (hour < 12) {
            builder.append("AM");
        } else {
            builder.append("PM");
        }
    }

    @Override
    public int compareTo(Event e) {
        if (dayOfWeek.getValue() != e.getDayOfWeek().getValue()) {
            return dayOfWeek.getValue() - e.getDayOfWeek().getValue();
        } else {
            return getDayOfWeek()
        }
    }
}
