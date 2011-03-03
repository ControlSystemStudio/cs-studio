/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart;


/** Search for samples in a haystack. 
 *  @author Kay Kasemir
 */
public class ChartSampleSearch
{
    private int cmp;
    private int mid;

    /** Perform binary search for given value.
     *  @return Returns <code>true</code> if excact match was found,
     *          otherwise <code>cmp</code> and <code>mid</code> are left accordingly.
     */
    private boolean search(ChartSampleSequence samples, double x)
    {
        int low = 0;
        int high = samples.size()-1;
        cmp = 0;
        mid = -1;
        while (low <= high)
        {
            mid = (low + high) / 2;  
            // Compare 'mid' sample with goal
            double diff = samples.get(mid).getX() - x;
            if (diff > 0.0)
            	cmp = 1;
            else if (diff < 0.0)
            	cmp = -1;
            else
            	cmp = 0;
            // See where to look next
            if (cmp == 0)
                return true; // key found
            if (cmp > 0) // 'mid' too big, search lower half
                high = mid - 1;
            else // 'mid' too small, search upper half
                low = mid + 1;
        }
        return false;
    }

    /** Find a sample that's smaller or equal to given value
     *  @param x The value to look for.
     *  @return Returns index of sample smaller-or-equal to given x, or -1.
     */
    static public int findSampleLessOrEqual(ChartSampleSequence samples, double x)
    {
        synchronized (samples)
        {
            ChartSampleSearch binary = new ChartSampleSearch();
            if (binary.search(samples, x))
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
    }

    /** Find a sample that's bigger or equal to given value
     *  @param x The value to look for.
     *  @return Returns index of sample bigger-or-equal to given x, or -1.
     */
    static public int findSampleGreaterOrEqual(ChartSampleSequence samples, double x)
    {
        synchronized (samples)
        {
            ChartSampleSearch binary = new ChartSampleSearch();
            if (binary.search(samples, x))
                return binary.mid;
            // Didn't find exact match.
            if (binary.cmp > 0) // 'mid' sample is bigger than x, so it's OK
                return binary.mid;
            // cmp < 0, 'mid' sample is smaller than x.
            // If there is a sample beyond, use that
            if (binary.mid < samples.size()-2)
                return binary.mid+1;
            return -1;
        }
    }

    /** Find a sample that's bigger or equal to given value
     *  @param x The value to look for.
     *  @return Returns index of sample bigger-or-equal to given x, or -1.
     */
    static public int findClosestSample(ChartSampleSequence samples, double x)
    {
        synchronized (samples)
        {
            ChartSampleSearch binary = new ChartSampleSearch();
            if (binary.search(samples, x))
                return binary.mid;
            // Didn't find exact match.
            if (binary.cmp > 0) // 'mid' sample is bigger than x
            {   // [mid-1]  ... x ... [mid]
                if (binary.mid > 0 &&
                    (x - samples.get(binary.mid-1).getX()
                       <  samples.get(binary.mid).getX() - x))
                    return binary.mid-1;
                return binary.mid;
            }
            // cmp < 0, 'mid' sample is smaller than x.
            // [mid] ... x ... [mid+1]
            if (binary.mid+1 < samples.size() &&
                (samples.get(binary.mid+1).getX() - x
                   <  x - samples.get(binary.mid).getX()))
                        return binary.mid+1;
            return binary.mid;
        }
    }

}
