package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.Model;

public class ScaleTypeAxisAction extends CheckableAxisAction
{

    public ScaleTypeAxisAction(final Model model,
            final Integer axis_index)
    {
        super(Messages.LogScale, model,axis_index);
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