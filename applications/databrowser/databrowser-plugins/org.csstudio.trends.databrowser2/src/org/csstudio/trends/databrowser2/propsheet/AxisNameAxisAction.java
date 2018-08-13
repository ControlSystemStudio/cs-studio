package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.AxisConfig;

public class AxisNameAxisAction extends CheckableAxisAction
{

    public AxisNameAxisAction(final AxisConfig axis_config)
    {
        super(Messages.ShowAxisName, axis_config);
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