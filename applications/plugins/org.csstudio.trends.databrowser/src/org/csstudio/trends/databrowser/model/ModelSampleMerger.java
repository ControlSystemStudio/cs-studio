package org.csstudio.trends.databrowser.model;

import org.csstudio.platform.util.ITimestamp;
import org.csstudio.swt.chart.ChartSampleSearch;

/** Helper for merging archived samples.
 *  @author Kay Kasemir
 */
public class ModelSampleMerger
{
    /** Merge samples to 'add' into 'old' list of samples.
     *  <p>
     *  Any overlap gets resolved in favor of the new samples.
     *  @param old Existing list of samples.
     *  @param add New samples to add.
     *  @return The merged samples.
     *          Typically, that is the modified 'old' array,
     *          but it could also be the 'add' array, in case that
     *          completely replaces the old samples.
     */
    @SuppressWarnings("nls")
    static ModelSampleArray merge(ModelSampleArray old,
                    ModelSampleArray add)
    {   // If one is empty, return the other as is:
        if (old == null  ||  old.size() <= 0)
            return add;
        if (add == null  ||  add.size() <= 0)
            return old;
        // Both lists have at least one sample.
        // Determine start/end times.
        ITimestamp old_start = old.get(0).getSample().getTime();
        ITimestamp old_end = old.get(old.size()-1).getSample().getTime();
        ITimestamp add_start = add.get(0).getSample().getTime();
        ITimestamp add_end = add.get(add.size()-1).getSample().getTime();

        /*
        System.out.print("Have samples " + old_start.toString());
        System.out.print(" to " + old_end.toString());
        System.out.println(" (" + old.size() + ")");

        System.out.print("New Samples  " + add_start.toString());
        System.out.print(" to " + add_end.toString());
        System.out.println(" (" + add.size() + ")");
         */
        
        // Assume old samples are this:  +=============+
        // new samples could be
        // Before    +--------+,
        if (add_end.isLessOrEqual(old_start))
        {
            ModelSampleArray result =
                new ModelSampleArray(add.size() + old.size());
            // result.addAll(add);
            int N = add.size();
            for (int i=0; i<N; ++i)
                result.add(add.get(i));            
            // result.addAll(old);
            N = old.size();
            for (int i=0; i<N; ++i)
                result.add(old.get(i));
            return result;
        }
        //                               +=============+
        // after existing sample                        +--------+,
        if (add_start.isGreaterOrEqual(old_end))
        {
            ModelSampleArray result =
                new ModelSampleArray(old.size() + add.size());
            // result.addAll(old);
            int N = old.size();
            for (int i=0; i<N; ++i)
                result.add(old.get(i));
            // result.addAll(add);
            N = add.size();
            for (int i=0; i<N; ++i)
                result.add(add.get(i));
            return result;
        }
        //                               +=x===========+
        // before with overlap    +--------+
        if (add_start.isLessOrEqual(old_start) &&
            add_end.isGreaterOrEqual(old_start) &&
            add_end.isLessOrEqual(old_end))
        {
            // Determine the first sample to use from the old list
            int x = ChartSampleSearch.findSampleGreaterOrEqual(old, add_end.toDouble());
            
            ModelSampleArray result =
                new ModelSampleArray(add.size() + old.size()-x);
            // result.addAll(add);
            int N = add.size();
            for (int i=0; i<N; ++i)
                result.add(add.get(i));
            // result.addAll(old[x, ... N-1]);
            N = old.size();
            for (int i=x; i<N; ++i)
                result.add(old.get(i));
            return result;
        }
        //                               +=========x===+
        // after with overlap                      +--------+
        if (add_start.isGreaterOrEqual(old_start) &&
            add_start.isLessOrEqual(old_end) &&
            add_end.isGreaterOrEqual(old_end))
        {
            // Determine the last sample to use from the old list
            int x = ChartSampleSearch.findSampleGreaterOrEqual(old, add_start.toDouble());
            ModelSampleArray result = new ModelSampleArray(x + add.size());
            // result.addAll(old[0 ... x-1]);
            for (int i=0; i<x; ++i)
                result.add(old.get(i));
            // result.addAll(add);
            int N = add.size();
            for (int i=0; i<N; ++i)
                result.add(add.get(i));
            return result;
        }
        //                               +===l=====r===+
        // within                            +-----+
        if (add_start.isGreaterOrEqual(old_start) &&
            add_end.isLessOrEqual(old_end))
        {
            // Determine the left/right indices of the section within 'old'.
            int l = ChartSampleSearch.findSampleLessOrEqual(old, add_start.toDouble());
            int r = ChartSampleSearch.findSampleGreaterOrEqual(old, add_end.toDouble());
            ModelSampleArray result =
                new ModelSampleArray(l+1 + add.size() + old.size()-r);
            // result.addAll(old[0 ... l]);
            for (int i=0; i<=l; ++i)
                result.add(old.get(i));
            // result.addAll(add);
            int N = add.size();
            for (int i=0; i<N; ++i)
                result.add(add.get(i));
            // result.addAll(old[r ... N-1]);
            N = old.size();
            for (int i=r; i<N; ++i)
                result.add(old.get(i));
            return result;
        }
        //                               +=============+
        // complete replacement    +------------------------+
        if (add_start.isLessOrEqual(old_start) &&
            add_end.isGreaterOrEqual(old_end))
            return add;

        throw new Error("Cannot handle this case");
    }
}
