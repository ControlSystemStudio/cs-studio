package org.csstudio.alarm.table.timeSelection;

import org.csstudio.data.Timestamp;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


public class StartEndDialog extends Dialog implements TimestampWidgetListener
{
    private Timestamp start;
    private Timestamp end;
    private TimestampWidget start_widget;
    private TimestampWidget end_widget;
    private Label info;
    
    public StartEndDialog(Shell shell, Timestamp start, Timestamp end)
    {
        super(shell);
        this.start = start;
        this.end = end;
    }
    
    /** @return the start time */
    public Timestamp getStart()
    {
        return start;
    }

    /** @return the end time */
    public Timestamp getEnd()
    {
        return end;
    }

    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText("Start/End Time");
    }
    
    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite box = (Composite) super.createDialogArea(parent);
        GridLayout layout = (GridLayout) box.getLayout();
        layout.numColumns = 2;
        GridData gd;

        Group left = new Group(box, 0);
        left.setText("Start Time");
        gd = new GridData();
        left.setLayoutData(gd);
        left.setLayout(new FillLayout());
        start_widget = new TimestampWidget(left, 0, start);
        start_widget.addListener(this);

        Group right = new Group(box, 0);
        right.setText("End Time");
        gd = new GridData();
        right.setLayoutData(gd);
        right.setLayout(new FillLayout());
        end_widget = new TimestampWidget(right, 0, end);
        end_widget.addListener(this);
        
        info = new Label(box, SWT.NULL);
        info.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        info.setLayoutData(gd);
        
        return box;
    }

    // TimestampWidgetListener
    public void updatedTimestamp(TimestampWidget source, Timestamp stamp)
    {
        if (source == start_widget)
            start = stamp;
        else
            end = stamp;
        
        System.out.println("Start: " + start.format(Timestamp.FMT_DATE_HH_MM_SS));
        System.out.println("End  : " + end.format(Timestamp.FMT_DATE_HH_MM_SS));
        
        if (start.isGreaterOrEqual(end))
            info.setText("Start time must be 'before' end time!");
        else
            info.setText("");
    }
}
