package org.csstudio.trends.databrowser.sampleview;

import org.csstudio.platform.data.IValue;
import org.csstudio.platform.model.IProcessVariableWithSamples;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.ModelSample;
import org.eclipse.core.runtime.PlatformObject;

/** One row in the table, i.e. one sample of a ModelItem.
 *  <p>
 *  Also provides access to <u>all</u> sampes of the ModelItem,
 *  i.e. all rows in the table via IProcessVariableWithSamples.
 *  
 *  @author Kay Kasemir
 *  @author Helge Rickens
 */
public final class TableItem
    extends PlatformObject implements IProcessVariableWithSamples
{
    /** The table model for this item. */
    final private IModelItem model_item;
    
    /** The row in the table model that has this item's sample. */
    final private int row;
    
    TableItem(final IModelItem model_item, final int row)
    {
        this.model_item = model_item;
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
        return model_item.getName();
    }

    /** Get model sample for the label provider
     *  @return The ModelSample.
     */
    ModelSample getSample()
    {
        return model_item.getModelSample(row);
    }

    /** @see IProcessVariableWithSamples */
    public IValue getSample(final int row)
    {
        return model_item.getSample(row);
    }

    /** @see IProcessVariableWithSamples */
    public int size()
    {
        return model_item.size();
    }
}
