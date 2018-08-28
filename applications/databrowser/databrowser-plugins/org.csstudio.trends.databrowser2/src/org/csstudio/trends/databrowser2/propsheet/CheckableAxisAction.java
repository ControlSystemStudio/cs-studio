package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.eclipse.jface.action.Action;

public abstract class CheckableAxisAction extends Action
{

    protected AxisConfig axis_config;

    public CheckableAxisAction(
            final String text,
            final AxisConfig axis_config)
    {
        super(text, Action.AS_CHECK_BOX);
        this.axis_config = axis_config;
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
