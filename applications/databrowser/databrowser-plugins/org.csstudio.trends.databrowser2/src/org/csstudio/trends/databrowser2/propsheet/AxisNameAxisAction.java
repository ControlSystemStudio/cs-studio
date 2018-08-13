package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.model.Model;

public class AxisNameAxisAction extends CheckableAxisAction
{

    public AxisNameAxisAction(final Model model,
            final Integer axis_index)
    {
        super("Show Axis Name", model,axis_index);
    }

    @Override
    protected Boolean getAxisState() {
        return axis_config.isUsingAxisName();
    }

    @Override
    protected void setAxisState(final Boolean state) {
        axis_config.useAxisName(state);
    }

}