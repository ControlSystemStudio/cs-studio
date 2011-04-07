package org.csstudio.ui.util.widgets;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;

/** main() routine for testing the clock as an SWT app.
 *  @author Kay Kasemir
 */
public class MeterWidgetTest
{
    private static boolean run = true;
    
    @SuppressWarnings("nls")
    public static void main(final String[] args)
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setBounds(400, 100, 300, 350);
        GridLayout gl = new GridLayout();
        gl.numColumns = 1;
        shell.setLayout(gl);
        GridData gd;

        final MeterWidget meter = new MeterWidget(shell, 0);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        meter.setLayoutData(gd);
        
        meter.setLimits(0, 0.0, 2.0, 8.0, 9.0, 10.0, 2);
        
        final Slider slider = new Slider(shell, SWT.HORIZONTAL);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        slider.setLayoutData(gd);
        slider.setValues(10, 0, 21, 1, 1, 1);
        slider.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                final double value = slider.getSelection()-10.0;
                //System.out.println("Value: " + value);
                meter.setValue(value);
            }
        });
        
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
