package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.model.Model;

public class TraceNameAxisAction extends CheckableAxisAction
{

    public TraceNameAxisAction(final Model model,
            final Integer axis_index)
    {
        super("Show Trace Names", model,axis_index);
    }

    @Override
    protected Boolean getAxisState() {
        return axis_config.isUsingTraceNames();
    }

    @Override
    protected void setAxisState(final Boolean state) {
        axis_config.useTraceNames(state);
    }

}