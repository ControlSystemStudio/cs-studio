package org.csstudio.trends.databrowser.sampleview;

import org.csstudio.platform.data.IValue;
//import org.csstudio.platform.model.IProcessVariableWithSample;
import org.csstudio.platform.model.IProcessVariableWithSamples;
import org.csstudio.trends.databrowser.model.ModelSample;

/** One item displayed in the table.
 *  @author Kay Kasemir
 *  @author Helge Rickens
 */
public final class TableItem implements IProcessVariableWithSamples
{
    /** The actual sample of this table row. */
    final private ModelSample sample;
    
    /** The whole data as an IProcessVariableWithSample.
     *  <p>
     *  The sample would suffice for the table display, but
     *  we need to implement IProcessVariableWithSample for
     *  the context menu.
     */
    final private IProcessVariableWithSamples ipv_with_samples;
    
    TableItem(ModelSample sample, IProcessVariableWithSamples ipv_with_samples)
    {
        this.sample = sample;
        this.ipv_with_samples = ipv_with_samples;
    }
    
    /** @return The ModelSample. */
    ModelSample getSample()
    {
        return sample;
    }

    /** @see IProcessVariableWithSamples */
    public IValue getSample(int index) {
        return ipv_with_samples.getSample(index);
    }

    /** @see IProcessVariableWithSamples */
    public int size() {
        return ipv_with_samples.size();
    }

    /** @see IProcessVariableWithSamples */
    public String getName() {
        return ipv_with_samples.getName();
    }

    /** @see IProcessVariableWithSamples */
    public String getTypeId() {
        return ipv_with_samples.getTypeId();
    }

    /** @see IProcessVariableWithSamples */
    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter) {
        return ipv_with_samples.getAdapter(adapter);
    }
}
