/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.time;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import org.csstudio.apputil.time.AbsoluteTimeParser;
import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.apputil.time.RelativeTimeParser;
import org.csstudio.apputil.time.RelativeTimeParserResult;
import org.csstudio.apputil.time.StartEndTimeParser;
import org.csstudio.java.time.TimestampFormats;
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
    // GUI Elements
    private TabFolder left_tab, right_tab;
    private static final int ABS_TAB = 0;
    private static final int REL_TAB = 1;
    private CalendarWidget abs_start, abs_end;
    private RelativeTimeWidget rel_start, rel_end;
    private Text start_text, end_text;
    private Label info;

    // Start and end specification strings
    private String start_specification, end_specification;
    private StartEndTimeParser start_end;

    /** Create dialog with some default start and end time. */
    @SuppressWarnings("nls")
    public StartEndDialog(Shell shell)
    {
        this(shell, "-1" + RelativeTime.DAY_TOKEN, RelativeTime.NOW);
    }

    /** Create dialog with given start and end time specification. */
    public StartEndDialog(final Shell shell, final String start, final String end)
    {
        super(shell);
        start_specification = start;
        end_specification = end;
        // Allow resize
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    /** @return Start specification. */
    public String getStartSpecification()
    {   return start_specification;  }

    /** @return End specification. */
    public String getEndSpecification()
    {   return end_specification; }

    /** @return Calendar for start time. */
    public final Calendar getStartCalendar()
    {   return start_end.getStart();  }

    /** @return Calendar for end time. */
    public final Calendar getEndCalendar()
    {   return start_end.getEnd(); }

    /** @return <code>true</code> if end time is 'now' */
    public final boolean isEndNow()
    {   return start_end.isEndNow(); }

    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText(Messages.StartEnd_Title);
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        final Composite area = (Composite) super.createDialogArea(parent);

        final Composite box = new Composite(area, 0);
        box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        box.setLayout(layout);
        GridData gd;

        // ---- Left -----
        left_tab = new TabFolder(box, SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns/2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        left_tab.setLayoutData(gd);

        TabItem tab = new TabItem(left_tab, 0);
        tab.setText(Messages.StartEnd_AbsStart);
        tab.setToolTipText(Messages.StartEnd_AbsStart_TT);
        abs_start = new CalendarWidget(left_tab, 0);
        abs_start.addListener(this);
        tab.setControl(abs_start);

        tab = new TabItem(left_tab, 0);
        tab.setText(Messages.StartEnd_RelStart);
        tab.setToolTipText(Messages.StartEnd_RelStart_TT);
        rel_start = new RelativeTimeWidget(left_tab, 0);
        rel_start.addListener(this);
        tab.setControl(rel_start);

        right_tab = new TabFolder(box, SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns/2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        right_tab.setLayoutData(gd);

        tab = new TabItem(right_tab, 0);
        tab.setText(Messages.StartEnd_AbsEnd);
        tab.setToolTipText(Messages.StartEnd_AbsEnd_TT);
        abs_end = new CalendarWidget(right_tab, 0);
        abs_end.addListener(this);
        tab.setControl(abs_end);

        tab = new TabItem(right_tab, 0);
        tab.setText(Messages.StartEnd_RelEnd);
        tab.setToolTipText(Messages.StartEnd_RelEnd_TT);
        rel_end = new RelativeTimeWidget(right_tab, 0);
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

        // Initialize GUI content
        setFromSpecifications();

        return area;
    }

    /** If the dialog is closed via OK,
     *  update the start/end specs from the GUI.
     */
    @Override
    protected void okPressed()
    {
        start_specification = start_text.getText();
        end_specification = end_text.getText();
        // If the specifications don't parse, don't allow 'OK'
        try
        {
            start_end =
                new StartEndTimeParser(start_specification, end_specification);
            if (start_end.getStart().compareTo(start_end.getEnd()) >= 0)
            {
                info.setText(Messages.StartEnd_StartExceedsEnd);
                return;
            }
        }
        catch (Exception ex)
        {
            info.setText(Messages.StartEnd_Error + ex.getMessage());
            return;
        }
        // Proceed...
        super.okPressed();
    }

    /** @see #setFromSpecifications */
    private void setFromSpecification(TabFolder tab, CalendarWidget abs,
                    RelativeTimeWidget rel, Text text, String specification)
        throws Exception
    {
        text.setText(specification);
        RelativeTimeParserResult result = RelativeTimeParser.parse(specification);
        if (result.isAbsolute())
        {
            tab.setSelection(ABS_TAB);
            abs.setCalendar(AbsoluteTimeParser.parse(specification));
        }
        else
        {
            tab.setSelection(REL_TAB);
            rel.setRelativeTime(result.getRelativeTime());
        }
    }

    /** Set GUI from start/end strings. */
    private void setFromSpecifications()
    {
        try
        {
            setFromSpecification(left_tab, abs_start, rel_start, start_text,
                                 start_specification);
        }
        catch (Exception ex)
        {
            info.setText(Messages.StartEnd_StartError);
        }
        try
        {
            setFromSpecification(right_tab, abs_end, rel_end, end_text,
                                 end_specification);
        }
        catch (Exception ex)
        {
            info.setText(Messages.StartEnd_EndError);
        }
    }

    final static private DateTimeFormatter DATE_FORMAT =  TimestampFormats.SECONDS_FORMAT;

    // CalendarWidgetWidgetListener
    @Override
    public void updatedCalendar(CalendarWidget source, Calendar calendar)
    {
        if (source == abs_start)
            start_text.setText(DATE_FORMAT.format(calendar.toInstant()));
        else
            end_text.setText(DATE_FORMAT.format(calendar.toInstant()));
            /*
        if (start.isGreaterOrEqual(end))
            info.setText(Messages.StartExceedsEnd);
        else
            info.setText(""); //$NON-NLS-1$
            */
    }

    // RelativeTimeWidgetListener
    @Override
    public void updatedTime(RelativeTimeWidget source, RelativeTime time)
    {
        if (source == rel_start)
            start_text.setText(time.toString());
        else
            end_text.setText(time.toString());
    }
}
