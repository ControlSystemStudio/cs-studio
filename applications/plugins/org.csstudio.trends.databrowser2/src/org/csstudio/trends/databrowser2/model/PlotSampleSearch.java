/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import org.csstudio.data.values.ITimestamp;

/** Search for samples in a haystack.
 *  Using direct PlotSample array access since PlotSampleMerger
 *  has arrays and uses System.arraycopy, which unfortunately
 *  prevents its use with the more generic PlotSamples interface.
 *  @author Kay Kasemir
 */
public class PlotSampleSearch
{
    private int cmp;
    private int mid;

    /** Perform binary search for given value.
     *  @return Returns <code>true</code> if exact match was found,
     *          otherwise <code>cmp</code> and <code>mid</code> are left accordingly:
     *          <code>mid</code> will be close to the desired sample,
     *          with cmp &lt; 0 if the sample at index 'mid' is smaller than 'goal',
     *          or cmp &gt; 0 if the sample at index 'mid' is greater than 'goal'.
     */
    private boolean search(final PlotSample samples[], final ITimestamp goal)
    {
        int low = 0;
        int high = samples.length-1;
        cmp = 0;
        mid = -1;
        while (low <= high)
        {
            mid = (low + high) / 2;
            // Compare 'mid' sample to goal
            final ITimestamp time = samples[mid].getTime();
            if (time.isGreaterThan(goal))
            {   // 'mid' too big, search lower half
                cmp = 1;
                high = mid - 1;
            }
            else if (time.isLessThan(goal))
            {   // 'mid' too small, search upper half
                cmp = -1;
                low = mid + 1;
            }
            else
            {
                cmp = 0;
                return true; // key found
            }
        }
        return false;
    }

    /** Find a sample that's smaller or equal to given value
     *  @param goal The time to look for.
     *  @return Returns index of sample smaller-or-equal to given goal, or -1.
     */
    static public int findSampleLessOrEqual(final PlotSample samples[], final ITimestamp goal)
    {
        final PlotSampleSearch binary = new PlotSampleSearch();
        if (binary.search(samples, goal))
            return binary.mid;
        // Didn't find exact match.
        if (binary.cmp < 0) // 'mid' sample is smaller than x, so it's OK
            return binary.mid;
        // cmp > 0, 'mid' sample is greater than x.
        // If there is a sample before, use that
        if (binary.mid > 0)
            return binary.mid-1;
        return -1;
    }

    /** Find a sample that's bigger or equal to given value
     *  @param goal The time to look for.
     *  @return Returns index of sample bigger-or-equal to given goal, or -1.
     */
    static public int findSampleGreaterOrEqual(final PlotSample samples[], final ITimestamp goal)
    {
        final PlotSampleSearch binary = new PlotSampleSearch();
        if (binary.search(samples, goal))
            return binary.mid;
        // Didn't find exact match.
        if (binary.cmp > 0) // 'mid' sample is bigger than x, so it's OK
            return binary.mid;
        // cmp < 0, 'mid' sample is smaller than x.
        // If there is a sample beyond, use that
        if (binary.mid < samples.length-2)
            return binary.mid+1;
        return -1;
    }

    /** Find the last sample that's smaller than the given value,
     *  i.e. the 'next' sample would be equal-or-greater than goal.
     *  @param goal The time to look for.
     *  @return Returns index of sample smaller than given goal, or -1.
     */
    static public int findSampleLessThan(final PlotSample samples[], final ITimestamp goal)
    {
        final PlotSampleSearch binary = new PlotSampleSearch();
        binary.search(samples, goal);
        int i = binary.mid;
        // Found 'mid' sample smaller than x right away?
        if (binary.cmp < 0) // 'mid' sample is smaller than x, so it's OK
            return i;
        // Look for sample < x
        while (i > 0)
        {
            --i;
            if (samples[i].getTime().isLessThan(goal))
                return i;
        }
        return -1;
    }

    /** Find the last sample that's greater than the given value,
     *  i.e. the 'previous' sample would be equal-or-less than goal.
     *  @param goal The time to look for.
     *  @return Returns index of sample greater than given goal, or -1.
     */
    static public int findSampleGreaterThan(final PlotSample samples[], final ITimestamp goal)
    {
        final PlotSampleSearch binary = new PlotSampleSearch();
        binary.search(samples, goal);
        int i = binary.mid;
        // Found 'mid' sample bigger than x right away?
        if (binary.cmp > 0)
            return binary.mid;
        // Look for sample > x
        while (++i < samples.length)
        {
            if (samples[i].getTime().isGreaterThan(goal))
                return i;
        }
        return -1;
    }

    /** Find a sample that's bigger or equal to given value
     *  @param goal The time to look for.
     *  @return Returns index of sample bigger-or-equal to given goal, or -1.
     */
    static public int findClosestSample(final PlotSample samples[], final ITimestamp goal)
    {
        final PlotSampleSearch binary = new PlotSampleSearch();
        if (binary.search(samples, goal))
            return binary.mid;
        // Didn't find exact match.
        if (binary.cmp > 0) // 'mid' sample is bigger than x
        {   // [mid-1]  ... x ... [mid]
            if (binary.mid > 0 &&
                (goal.toDouble() - samples[binary.mid-1].getTime().toDouble()
                   <  samples[binary.mid].getTime().toDouble() - goal.toDouble()))
                return binary.mid-1;
            return binary.mid;
        }
        // cmp < 0, 'mid' sample is smaller than x.
        // [mid] ... x ... [mid+1]
        if (binary.mid+1 < samples.length &&
            (samples[binary.mid+1].getTime().toDouble() - goal.toDouble()
               <  goal.toDouble() - samples[binary.mid].getTime().toDouble()))
                    return binary.mid+1;
        return binary.mid;
    }
}
