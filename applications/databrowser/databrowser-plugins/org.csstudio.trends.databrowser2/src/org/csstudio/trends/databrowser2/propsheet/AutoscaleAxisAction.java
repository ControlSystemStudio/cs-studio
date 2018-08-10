package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.Model;
import org.eclipse.jface.action.Action;

public class AutoscaleAxisAction extends Action
{
    final private Model model;
    private Integer axis_index;

    public AutoscaleAxisAction(final Model model,
            final Integer axis_index)
    {
        super(Messages.AutoScale, Action.AS_CHECK_BOX);
        this.model = model;
        this.axis_index = axis_index;
        this.setChecked(model.getAxis(axis_index).isAutoScale());
    }

    @Override
    public void run()
    {
        AxisConfig axis = model.getAxis(axis_index);
        axis.setAutoScale(!axis.isAutoScale());
        this.setChecked(model.getAxis(axis_index).isAutoScale());
    }
}
