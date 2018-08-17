package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.model.Model;
import org.eclipse.jface.action.Action;

public class TimeAxisGridAction extends Action
{
    final Model model;

    public TimeAxisGridAction(
            final String text,
            final Model model)
    {
        super(text, Action.AS_CHECK_BOX);
        this.model = model;
        this.setChecked(this.getAxisState());
    }

    protected Boolean getAxisState() {
        return model.isGridVisible();
    }

    protected void setAxisState(final Boolean state) {
        model.setGridVisible(state);
    }

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
