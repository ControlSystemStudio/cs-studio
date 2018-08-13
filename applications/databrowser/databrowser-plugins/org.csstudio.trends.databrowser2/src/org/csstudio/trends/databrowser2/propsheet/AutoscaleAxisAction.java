package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.model.Model;

public class AutoscaleAxisAction extends CheckableAxisAction
{

    public AutoscaleAxisAction(final Model model,
            final Integer axis_index)
    {
        super("Auto-Scale", model,axis_index);
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