import components.Course;
import components.Schedule;
import components.Section;

import java.util.*;

/**
 * Created by Ethan on 10/8/17.
 */
public class ScheduleGenerator {
    public static Set<Schedule> generateSchedules(List<Course> courses, int kPower) {
        Set<Schedule> validSchedules = new HashSet<>();
        Set<Set<Course>> powerSets = computeKPowerSet(new HashSet<>(courses), kPower);
        for (Set<Course> powerSet : powerSets) {
            validSchedules.addAll(processCoursePossibilities(powerSet));
        }

        // FIXME eval dependencies
        return validSchedules;
    }

    /**
     * Processes a set of Courses and returns all possible schedules possible with the set of courses
     * @param courses
     * @return
     */
    private static Set<Schedule> processCoursePossibilities(Set<Course> courses) {
        Set<Schedule> validSchedules = new HashSet<>();
        List<List<Section>> allSections = new ArrayList<>();
        for (Course course : courses) {
            allSections.add(course.getSections());
        }
        MultiListIterator<Section> iter = new MultiListIterator<>(allSections);

        while (iter.hasNext()) {
            Set<Section> selection = iter.next();

            Schedule schedule = processSectionPossibility(selection);

            if (schedule != null) {
                validSchedules.add(schedule);
            }
        }
        return validSchedules;

    }

    /**
     * Processes a set of sections and returns all the schedule with the given sections.
     * If such a schedule cannot exist, null is returned.
     * @return the generated schedule or if impossible, null
     */
    private static Schedule processSectionPossibility(Set<Section> sections) {
        Schedule returnSchedule = new Schedule();
        Iterator<Section> iter = sections.iterator();
        boolean hasFailed = false;
        do {
            hasFailed = !returnSchedule.merge(iter.next().getSchedule());
        } while (!hasFailed && iter.hasNext());

        if (hasFailed) {
            return null;
        } else {
            return returnSchedule;
        }
    }

    /**
     * Shamelessly copied from stack overflow, because I couldn't figure it out.
     * @param source
     * @param k
     * @param <E>
     * @return
     */
    private static <E> Set<Set<E>> computeKPowerSet(final Set<E> source, final int k)
    {
        if (k==0 || source.size() < k) {
            Set<Set<E>> set = new HashSet<Set<E>>();
            set.add(Collections.EMPTY_SET);
            return set;
        }

        if (source.size() == k) {
            Set<Set<E>> set = new HashSet<Set<E>>();
            set.add(source);
            return set;
        }

        Set<Set<E>> toReturn = new HashSet<Set<E>>();

        // distinguish an element
        for(E element : source) {
            // compute source - element
            Set<E> relativeComplement = new HashSet<E>(source);
            relativeComplement.remove(element);

            // add the powerset of the complement
            Set<Set<E>> completementPowerSet = computeKPowerSet(relativeComplement,k-1);
            toReturn.addAll(withElement(completementPowerSet,element));
        }

        return toReturn;
    }

    /** Given a set of sets S_i and element k, return the set of sets {S_i U {k}} */
    static private <E> Set<Set<E>> withElement(final Set<Set<E>> source, E element)
    {

        Set<Set<E>> toReturn = new HashSet<Set<E>>();
        for (Set<E> setElement : source) {
            Set<E> withElementSet = new HashSet<E>(setElement);
            withElementSet.add(element);
            toReturn.add(withElementSet);
        }

        return toReturn;
    }
}
