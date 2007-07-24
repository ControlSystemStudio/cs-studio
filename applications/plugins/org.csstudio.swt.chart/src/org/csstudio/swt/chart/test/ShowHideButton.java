package org.csstudio.swt.chart.test;

import org.csstudio.swt.chart.InteractiveChart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class ShowHideButton
{
    private final InteractiveChart chart;

    public ShowHideButton(InteractiveChart chart)
    {
        this.chart = chart;
        
        Button b = new Button(chart.getButtonBar(), SWT.PUSH);
        b.setText("X"); //$NON-NLS-1$
        b.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                toggle();
            }
        });
    }
    
    private void toggle()
    {
        chart.showButtonBar(! chart.getButtonBar().isVisible());
    }
}
