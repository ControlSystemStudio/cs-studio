/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.plotwidget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** main() routine for testing the plot widget as an SWT app.
 *  @author Kay Kasemir
 */
public class PlotWidgetTest
{
    private static boolean run = true;
    
    @SuppressWarnings("nls")
    public static void main(String[] args)
    {
        PlotSamples samples = new PlotSamples()
        {
            public int getTraceCount()
            {
                return 3;
            }

            public Color getColor(int trace)
            {
                switch (trace)
                {
                case 0:
                    return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
                case 1:
                    return Display.getCurrent().getSystemColor(SWT.COLOR_GREEN);
                default:
                    return Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
                }
            }

            public int getSampleCount()
            {
                return 10;
            }

            public double[] getValues(int i)
            {
                return new double[] { i, 2*i, i*i };
            }
        };
        
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setBounds(400, 100, 300, 350);
        GridLayout gl = new GridLayout();
        gl.numColumns = 1;
        shell.setLayout(gl);
        GridData gd;

        PlotWidget plot = new PlotWidget(shell, 0);
        plot.setSamples(samples);
        
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        plot.setLayoutData(gd);
        
        Button ok = new Button(shell, SWT.PUSH);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.RIGHT;
        ok.setLayoutData(gd);
        ok.setText("Ok");
        ok.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                run = false;
            }
        });        
        
        shell.open();
        // Message loop left to the application
        while (run && !shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        display.dispose(); // !
    }
}
