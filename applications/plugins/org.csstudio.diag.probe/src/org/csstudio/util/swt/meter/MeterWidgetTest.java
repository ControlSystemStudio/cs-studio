package org.csstudio.util.swt.meter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** main() routine for testing the clock as an SWT app.
 *  @author Kay Kasemir
 */
public class MeterWidgetTest
{
    private static boolean run = true;
    
    private static void updateMeter(final MeterWidget meter)
    {
        meter.setValue(-10.0 + 20.0 * Math.random());
        meter.getDisplay().timerExec(200, new Runnable()
        {
            public void run()
            {
                updateMeter(meter);
            }
        });
    }

    @SuppressWarnings("nls")
    public static void main(String[] args)
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setBounds(400, 100, 300, 350);
        GridLayout gl = new GridLayout();
        gl.numColumns = 1;
        shell.setLayout(gl);
        GridData gd;

        MeterWidget meter = new MeterWidget(shell, 0);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        meter.setLayoutData(gd);
        
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

        updateMeter(meter);
        
        shell.open();
        // Message loop left to the application
        while (run && !shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        display.dispose(); // !
    }
}
