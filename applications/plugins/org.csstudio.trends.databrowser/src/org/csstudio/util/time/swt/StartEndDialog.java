package org.csstudio.util.time.swt;

import java.util.Calendar;

import org.csstudio.util.time.AbsoluteTimeParser;
import org.csstudio.util.time.RelativeTime;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

/** Dialog for entering relative as well as absolute start and end times.
 *  @author Kay Kasemir
 */
public class StartEndDialog extends Dialog
    implements CalendarWidgetListener, RelativeTimeWidgetListener
{
    private CalendarWidget abs_start, abs_end;
    private RelativeTimeWidget rel_start, rel_end;
    private Text start_text, end_text;
    private Label info;
    
    public StartEndDialog(Shell shell)
    {
        super(shell);
    }
    
    /** @return the start time
    public ITimestamp getStart()
    {   return start;  }

     @return the end time
    public ITimestamp getEnd()
    {   return end; }
    */

    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText(Messages.StartEnd_Title);
    }
    
    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite box = (Composite) super.createDialogArea(parent);
        GridLayout layout = (GridLayout) box.getLayout();
        layout.numColumns = 4;
        GridData gd;

        // Abs Start/Relative  Start       Abs End/Relative End
        //      ....                            ....
        // Start: __________________       End: _______________
        // Info
        //                                      [OK] [Cancel]
        TabFolder left = new TabFolder(box, SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns/2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        left.setLayoutData(gd);

        TabItem tab = new TabItem(left, 0);
        tab.setText(Messages.StartEnd_AbsStart);
        tab.setToolTipText(Messages.StartEnd_AbsStart_TT);
        abs_start = new CalendarWidget(left, 0);
        abs_start.addListener(this);
        tab.setControl(abs_start);
        
        tab = new TabItem(left, 0);
        tab.setText(Messages.StartEnd_RelStart);
        tab.setToolTipText(Messages.StartEnd_RelStart_TT);
        rel_start = new RelativeTimeWidget(left, 0);
        rel_start.addListener(this);
        tab.setControl(rel_start);
        
        // ---- Right ------
        TabFolder right = new TabFolder(box, SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns/2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        right.setLayoutData(gd);

        tab = new TabItem(right, 0);
        tab.setText(Messages.StartEnd_AbsEnd);
        tab.setToolTipText(Messages.StartEnd_AbsEnd_TT);
        abs_end = new CalendarWidget(right, 0);
        abs_end.addListener(this);
        tab.setControl(abs_end);
        
        tab = new TabItem(right, 0);
        tab.setText(Messages.StartEnd_RelEnd);
        tab.setToolTipText(Messages.StartEnd_RelEnd_TT);
        rel_end = new RelativeTimeWidget(right, 0);
        rel_end.addListener(this);
        tab.setControl(rel_end);
        
        // New Row
        Label l = new Label(box, SWT.NULL);
        l.setText(Messages.StartEnd_StartTime);
        gd = new GridData();
        l.setLayoutData(gd);
        
        start_text = new Text(box, SWT.LEFT);
        start_text.setToolTipText(Messages.StartEnd_StartTime_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        start_text.setLayoutData(gd);

        l = new Label(box, SWT.NULL);
        l.setText(Messages.StartEnd_EndTime);
        gd = new GridData();
        l.setLayoutData(gd);

        end_text = new Text(box, SWT.LEFT);
        end_text.setToolTipText(Messages.StartEnd_EndTime_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        end_text.setLayoutData(gd);

        // New Row
        info = new Label(box, SWT.NULL);
        info.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        info.setLayoutData(gd);
        
        // Select the 'relative' tabs
        right.setSelection(1);
        left.setSelection(1);
        
        return box;
    }

    // CalendarWidgetWidgetListener
    public void updatedTimestamp(CalendarWidget source, Calendar calendar)
    {
        if (source == abs_start)
            start_text.setText(AbsoluteTimeParser.format(calendar));
        else
            end_text.setText(AbsoluteTimeParser.format(calendar));
            /*
        if (start.isGreaterOrEqual(end))
            info.setText(Messages.StartExceedsEnd);
        else
            info.setText(""); //$NON-NLS-1$
            */
    }
    
    // RelativeTimeWidgetListener
    public void updatedTime(RelativeTimeWidget source, RelativeTime time)
    {
        if (source == rel_start)
            start_text.setText(time.toString());
        else
            end_text.setText(time.toString());
    }
}
