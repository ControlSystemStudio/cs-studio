package org.csstudio.swt.chart.test;

import org.csstudio.swt.chart.InteractiveChart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ShowHideButton
{
    private final Composite bar;

    public ShowHideButton(InteractiveChart chart)
    {
        bar = chart.getButtonBar();
        
        Button b = new Button(bar, SWT.PUSH);
        b.setText("X");
        b.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (bar.isVisible())
                {
                    bar.setVisible(false);
                }
                else
                {
                    bar.setVisible(true);
                }
            }
        });
    }
}
