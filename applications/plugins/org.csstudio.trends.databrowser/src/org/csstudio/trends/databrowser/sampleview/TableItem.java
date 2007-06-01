package org.csstudio.trends.databrowser.sampleview;

import org.csstudio.platform.model.IProcessVariableWithSample;
import org.csstudio.trends.databrowser.model.ModelSample;

/** One item displayed in the table.
 *  @author Kay Kasemir
 */
public final class TableItem implements IProcessVariableWithSample
{
    /** The actual sample of this table row. */
    final private ModelSample sample;
    
    /** The whole data as an IProcessVariableWithSample.
     *  <p>
     *  The sample would suffice for the table display, but
     *  we need to implement IProcessVariableWithSample for
     *  the context menu.
     */
    final private IProcessVariableWithSample ipv_with_samples;
    
    TableItem(ModelSample sample, IProcessVariableWithSample ipv_with_samples)
    {
        this.sample = sample;
        this.ipv_with_samples = ipv_with_samples;
    }
    
    /** @return The ModelSample. */
    ModelSample getSample()
    {
        return sample;
    }
    
    /** @see IProcessVariableWithSample */
    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter)
    {
        return ipv_with_samples.getAdapter(adapter);
    }

    /** @see IProcessVariableWithSample */
    public String getName()
    {
        return ipv_with_samples.getName();
    }

    /** @see IProcessVariableWithSample */
    public String getTypeId()
    {   return IProcessVariableWithSample.TYPE_ID; }

    /** @see IProcessVariableWithSample */
    public int getDBRTyp()
    {
        return ipv_with_samples.getDBRTyp();
    }

    /** @see IProcessVariableWithSample */
    public double[] getSampleValue()
    {
        return ipv_with_samples.getSampleValue();
    }

    /** @see IProcessVariableWithSample */
    public double[] getTimeStamp()
    {
        return ipv_with_samples.getTimeStamp();
    }

    /** @see IProcessVariableWithSample */
    public String[] getStatus()
    {
        return ipv_with_samples.getStatus();
    }

    /** @see IProcessVariableWithSample */
    public String[] getSeverity()
    {
        return ipv_with_samples.getSeverity();
    }

    /** @see IProcessVariableWithSample */
    public String getEGU()
    {
        return ipv_with_samples.getEGU();
    }
    
    /** @see IProcessVariableWithSample */
    public int getPrecision()
    {
        return ipv_with_samples.getPrecision();
    }

    /** @see IProcessVariableWithSample */
    public double getLow()
    {
        return ipv_with_samples.getLow();
    }

    /** @see IProcessVariableWithSample */
    public double getHigh()
    {
        return ipv_with_samples.getHigh();
    }
}
