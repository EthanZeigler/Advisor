import components.Section;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Ethan on 10/8/17.
 */
public class MultiListIterator <T> {
    private List<List<T>> sectionData;
    private int[] counters;
    private boolean hasReachedEnd = false;

    public MultiListIterator(List<List<T>> sectionData) {
        this.sectionData = sectionData;
        counters = new int[sectionData.size()];
    }

    public boolean hasNext() {
        return !hasReachedEnd;
    }

    public Set<T> next() {
        if (hasNext()) {
            Set<T> sections = new HashSet<>();
            for (int i = 0; i < counters.length; i++) {
                sections.add(sectionData.get(i).get(counters[i]));
            }
            increment();
            return sections;
        } else {
            throw new IndexOutOfBoundsException("No more elements");
        }
    }

    private void increment(int i) {
        if (i >= counters.length) {
            hasReachedEnd = true;
        } else if (counters[i] >= (sectionData.get(i).size() - 1)) {
            counters[i] = 0;
            increment(i + 1);
        } else {
            counters[i]++;
        }
    }

    private void increment() {
        increment(0);
    }
}
