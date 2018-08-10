package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.Model;
import org.eclipse.jface.action.Action;

public class ScaleTypeAxisAction extends Action
{

    private AxisConfig axis_config;

    public ScaleTypeAxisAction(final Model model,
            final Integer axis_index)
    {
        super(Messages.LogScale, Action.AS_CHECK_BOX);
        this.axis_config = model.getAxis(axis_index);
        this.setChecked(axis_config.isLogScale());
    }

    @Override
    public void run()
    {
        axis_config.setLogScale(!axis_config.isLogScale());
        this.setChecked(axis_config.isLogScale());
    }
}
