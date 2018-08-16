package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.AxisConfig;

public class TraceNameAxisAction extends CheckableAxisAction
{

    public TraceNameAxisAction(final AxisConfig axis_config)
    {
        super(Messages.ShowTraceNames, axis_config);
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