package org.csstudio.trends.databrowser.sampleview;

import org.csstudio.platform.data.IValue;
import org.csstudio.platform.model.IProcessVariableWithSamples;
import org.csstudio.trends.databrowser.model.ModelSample;
import org.eclipse.core.runtime.PlatformObject;

/** One item displayed in the table.
 *  <p>
 *  Also provides access to <u>all</u> items in the table
 *  via the IProcessVariableWithSamples interface.
 *  
 *  @author Kay Kasemir
 *  @author Helge Rickens
 */
public final class TableItem
    extends PlatformObject implements IProcessVariableWithSamples
{
    /** The table model for this item. */
    final private TableModel model;
    
    /** The row in the table model that has this item's sample. */
    final private int row;
    
    TableItem(final TableModel model, final int row)
    {
        this.model = model;
        this.row = row;
    }
    
    /** @see IProcessVariableWithSamples */
    public String getTypeId()
    {
        return IProcessVariableWithSamples.TYPE_ID;
    }

    /** @see IProcessVariableWithSamples */
    public String getName()
    {
        return model.getName();
    }

    /** Get model sample for the label provider
     *  @return The ModelSample.
     */
    ModelSample getSample()
    {
        return model.getSample(row);
    }

    /** @see IProcessVariableWithSamples */
    public IValue getSample(final int row)
    {
        return model.getSample(row).getSample();
    }

    /** @see IProcessVariableWithSamples */
    public int size()
    {
        return model.size();
    }
}
