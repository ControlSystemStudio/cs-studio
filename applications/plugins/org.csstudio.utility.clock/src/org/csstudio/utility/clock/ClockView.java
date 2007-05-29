package org.csstudio.utility.clock;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/** ViewPart for the clock.
 *  @author Kay Kasemir
 */
public class ClockView extends ViewPart
{
	public static final String ID = ClockView.class.getName();
    // The one and only widget in this view
    private ClockWidget clock;

    /** Fill the view. */
    @Override
    public void createPartControl(Composite parent)
    {
        GridLayout gl = new GridLayout();
        gl.numColumns = 1;
        parent.setLayout(gl);
        GridData gd;

        clock = new ClockWidget(parent, 0);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        clock.setLayoutData(gd);
    }

    /** Set focus on clock, though that's a NOP. */
    @Override
    public void setFocus()
    {
        clock.setFocus();
    }
}
