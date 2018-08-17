package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.AxisConfig;

public class ScaleTypeAxisAction extends CheckableAxisAction
{

    public ScaleTypeAxisAction(final AxisConfig axis_config)
    {
        super(Messages.LogScale, axis_config);
    }

    @Override
    protected Boolean getAxisState() {
        return axis_config.isLogScale();
    }

    @Override
    protected void setAxisState(final Boolean state) {
        axis_config.setLogScale(state);
    }


}