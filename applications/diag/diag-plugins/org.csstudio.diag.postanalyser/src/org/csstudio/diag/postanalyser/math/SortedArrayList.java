package org.csstudio.diag.postanalyser.math;

import java.util.ArrayList;

/** ArrayList for comparable elements, keeps them sorted
 *  as long as elements are inserted using <code>insert()</code>.
 *  @author Kay Kasemir
 *  @param <T> Array element type, must be <code>Comparable</code>
 */
public class SortedArrayList<T extends Comparable<T>> extends ArrayList<T>
{
    /** To avoid warnings... */
    private static final long serialVersionUID = 1L;

    /** Insert new element into sorted array */
    public void insert(final T new_element)
    {
        // Binary search for insertion point
        int low = 0;
        int high = size()-1;
        while (low <= high)
        {
            final int mid = (low + high)/2;
            final int cmp = get(mid).compareTo(new_element);

            if (cmp < 0)      // [mid] < new_element
                low = mid + 1;
            else if (cmp > 0) // [mid] > new_element
                high = mid - 1;
            else              // [mid] == new_element
            {
                this.add(mid+1, new_element);
                return;
            }
        }
        this.add(low, new_element);
    }
}
