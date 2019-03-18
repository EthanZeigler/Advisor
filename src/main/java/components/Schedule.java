package components;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Ethan on 10/4/17.
 */
public class Schedule implements Cloneable {

    static int counter = 0;
    private Set<Event> events;

    /**
     * Represents a section's schedule
     * @param events section's events
     */
    public Schedule(Set<Event> events) {
        this.events = events;
    }

    /**
     * Represents a schedule
     */
    public Schedule() {
        this (new HashSet<>());
    }

    /**
     * Returns the events in the schedule
     * @return the events in the schedule
     */
    public Set<Event> getEvents() {
        return events;
    }

    /**
     * Adds an event to the schedule if it does not create a collision
     * @param event event to add
     */
    public boolean addEvent(Event event) {
        if (!willCreateCollision(event)) {
            return events.add(event);
        }
        return false;
    }

    /**
     * Checks if adding the given event will cause a collision.
     * @param newEvent the event to add
     * @return true if the event creates a collision
     */
    public boolean willCreateCollision(Event newEvent) {
        for (Event event : events) {
            if (event.collidesWith(newEvent)) {
                return true;
            }
        }
        return false;
    }

    public boolean merge(Schedule schedule) {
        Set<Event> savedEvents = new HashSet<>(events);
        for (Event event : schedule.getEvents()) {
            if (!addEvent(event)) {
                events = savedEvents;
                return false;
            }
        }
        return true;
    }

    public Set<Course> collectCourses() {
        return collectSections().stream().map(Section::getParentCourse).distinct().collect(Collectors.toSet());
    }

    public Set<Section> collectSections() {
        return events.stream().map(Event::getParentSection).collect(Collectors.toSet());
    }

    public String prettyPrint() {
        StringBuilder builder = new StringBuilder();
        // Courses:
        //  CSC230 ->
        //    02:prof_name, A-01
        //
        //
        //
        //
        builder.append("Courses:\n");
        collectCourses().forEach(course -> {
            builder.append("\t")
                    .append(course.getName())
                    .append(" ->\n");
            course.getSections().forEach(section -> builder.append("\t\t")
                    .append(section.getSectionCode().trim())
                    .append(section.getDependencyPrettyPrint())
                    .append(":")
                    .append(section.getProfName().trim())
                    .append(" ")
                    .append("\n"));
        });
        AsciiTable table = new AsciiTable();
        table.setTextAlignment(TextAlignment.CENTER);
        table.addRule();


        @SuppressWarnings("unchecked")
        ImmutablePair<String, DayOfWeek>[] days = new ImmutablePair[]{
                new ImmutablePair<>("Monday", DayOfWeek.MONDAY),
                new ImmutablePair<>("Tuesday", DayOfWeek.TUESDAY),
                new ImmutablePair<>("Wednesday", DayOfWeek.WEDNESDAY),
                new ImmutablePair<>("Thursday", DayOfWeek.THURSDAY),
                new ImmutablePair<>("Friday", DayOfWeek.FRIDAY)};
//                new ImmutablePair<>("Saturday", DayOfWeek.SATURDAY),
//                new ImmutablePair<>("Sunday", DayOfWeek.SUNDAY)};
        table.addRow("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        String[] tableContent = new String[5];
        Arrays.fill(tableContent, "");
        for (Event event: events) {
            tableContent[event.getDayOfWeek().getValue() - 1] =
                    (tableContent[event.getDayOfWeek().getValue() - 1] + event.prettyPrint().replace("\n", "[BR]") + "<br><br><=====>");
        }
        table.addRule();
        table.addRow(Arrays.asList(tableContent));
        table.addRule();

        builder.append(table.render(120)).append("\n\n");
        //builder.append(table.render());
        return builder.toString();
    }

    /**
     * Creates and returns a copy of this object.  The precise meaning
     * of "copy" may depend on the class of the object. The general
     * intent is that, for any object {@code x}, the expression:
     * <blockquote>
     * <pre>
     * x.clone() != x</pre></blockquote>
     * will be true, and that the expression:
     * <blockquote>
     * <pre>
     * x.clone().getClass() == x.getClass()</pre></blockquote>
     * will be {@code true}, but these are not absolute requirements.
     * While it is typically the case that:
     * <blockquote>
     * <pre>
     * x.clone().equals(x)</pre></blockquote>
     * will be {@code true}, this is not an absolute requirement.
     *
     * By convention, the returned object should be obtained by calling
     * {@code super.clone}.  If a class and all of its superclasses (except
     * {@code Object}) obey this convention, it will be the case that
     * {@code x.clone().getClass() == x.getClass()}.
     *
     * By convention, the object returned by this method should be independent
     * of this object (which is being cloned).  To achieve this independence,
     * it may be necessary to modify one or more fields of the object returned
     * by {@code super.clone} before returning it.  Typically, this means
     * copying any mutable objects that comprise the internal "deep structure"
     * of the object being cloned and replacing the references to these
     * objects with references to the copies.  If a class contains only
     * primitive fields or references to immutable objects, then it is usually
     * the case that no fields in the object returned by {@code super.clone}
     * need to be modified.
     *
     * The method {@code clone} for class {@code Object} performs a
     * specific cloning operation. First, if the class of this object does
     * not implement the interface {@code Cloneable}, then a
     * {@code CloneNotSupportedException} is thrown. Note that all arrays
     * are considered to implement the interface {@code Cloneable} and that
     * the return type of the {@code clone} method of an array type {@code T[]}
     * is {@code T[]} where T is any reference or primitive type.
     * Otherwise, this method creates a new instance of the class of this
     * object and initializes all its fields with exactly the contents of
     * the corresponding fields of this object, as if by assignment; the
     * contents of the fields are not themselves cloned. Thus, this method
     * performs a "shallow copy" of this object, not a "deep copy" operation.
     *
     * The class {@code Object} does not itself implement the interface
     * {@code Cloneable}, so calling the {@code clone} method on an object
     * whose class is {@code Object} will result in throwing an
     * exception at run time.
     *
     * @return a clone of this instance.
     * @throws CloneNotSupportedException if the object's class does not
     *                                    support the {@code Cloneable} interface. Subclasses
     *                                    that override the {@code clone} method can also
     *                                    throw this exception to indicate that an instance cannot
     *                                    be cloned.
     * @see Cloneable
     */
    @Override
    protected Schedule clone() throws CloneNotSupportedException {
        Schedule schedule = (Schedule) super.clone();

        schedule.events = new HashSet<>(schedule.events);
        return schedule;
    }
}
