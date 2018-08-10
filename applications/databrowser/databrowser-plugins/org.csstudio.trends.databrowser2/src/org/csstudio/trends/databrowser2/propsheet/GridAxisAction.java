package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.Model;

public class GridAxisAction extends CheckableAxisAction
{

    public GridAxisAction(final Model model,
            final Integer axis_index)
    {
        super(Messages.GridTT, model,axis_index);
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
