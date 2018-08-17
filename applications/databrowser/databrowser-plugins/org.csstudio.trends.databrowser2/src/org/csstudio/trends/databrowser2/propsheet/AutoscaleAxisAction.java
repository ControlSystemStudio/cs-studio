package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.AxisConfig;

public class AutoscaleAxisAction extends CheckableAxisAction
{

    public AutoscaleAxisAction(final AxisConfig axis_config)
    {
        super(Messages.AutoScale, axis_config);
    }

    @Override
    protected Boolean getAxisState() {
        return axis_config.isAutoScale();
    }

    @Override
    protected void setAxisState(final Boolean state) {
        axis_config.setAutoScale(state);
    }


}