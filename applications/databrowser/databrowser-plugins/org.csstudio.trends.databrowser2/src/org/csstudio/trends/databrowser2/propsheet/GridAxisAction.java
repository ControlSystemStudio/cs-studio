package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.AxisConfig;

public class GridAxisAction extends CheckableAxisAction
{

    public GridAxisAction(final AxisConfig axis_config)
    {
        super(Messages.ShowGridLines, axis_config);
    }

    @Override
    protected Boolean getAxisState() {
        return axis_config.isGridVisible();
    }

    @Override
    protected void setAxisState(final Boolean state) {
        axis_config.setGridVisible(state);
    }


}
