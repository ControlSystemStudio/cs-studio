package org.csstudio.trends.databrowser.model;

import java.util.ArrayList;

import org.csstudio.platform.data.IValue;

/** Plain array implementation of PlotSamples
 *  @author Kay Kasemir
 */
public class PlotSampleArray extends PlotSamples
{
    private PlotSample samples[];

    /** {@inheritDoc} */
    @Override
    synchronized public int getSize()
    {
        return samples.length;
    }

    /** {@inheritDoc} */
    @Override
    synchronized public PlotSample getSample(int index)
    {
        return samples[index];
    }

    /** @param source Source of the values
     *  @param values Values from which to set the sample array
     */
    synchronized public void set(final String source, final ArrayList<IValue> values)
    {
        samples = new PlotSample[values.size()];
        for (int i = 0; i < samples.length; ++i)
            samples[i] = new PlotSample(source, values.get(i));
        have_new_samples = true;
    }
}
