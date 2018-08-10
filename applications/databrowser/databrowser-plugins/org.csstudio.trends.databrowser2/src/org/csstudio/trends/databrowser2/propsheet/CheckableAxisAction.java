package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.Model;
import org.eclipse.jface.action.Action;

public abstract class CheckableAxisAction extends Action
{

    protected AxisConfig axis_config;

    public CheckableAxisAction(
            final String text,
            final Model model,
            final Integer axis_index)
    {
        super(text, Action.AS_CHECK_BOX);
        this.axis_config = model.getAxis(axis_index);
        this.setChecked(this.getAxisState());
    }

    protected abstract Boolean getAxisState();
    protected abstract void setAxisState(final Boolean state);

    protected void toggleAxisState() {
        this.setAxisState(!this.getAxisState());
    }

    @Override
    public void run()
    {
        this.toggleAxisState();
        this.setChecked(this.getAxisState());
    }
}
