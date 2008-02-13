package org.csstudio.swt.chart.test;

import org.csstudio.swt.chart.InteractiveChart;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class ToolBarActionHook
{
    public ToolBarActionHook(final InteractiveChart chart,
                    final Action action)
    {
        Button b = new Button(chart.getButtonBar(), SWT.PUSH);
        b.setText(action.getText());
        b.setToolTipText(action.getToolTipText());
        b.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                action.run();
            }
        });
    }
}
