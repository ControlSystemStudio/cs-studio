package org.csstudio.util.swt;

import java.util.ArrayList;
import java.util.Calendar;

import org.csstudio.platform.util.ITimestamp;
import org.csstudio.platform.util.TimestampFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarEvent;
import org.vafada.swtcalendar.SWTCalendarListener;

/** Widget for displaying and setting a CSS Timestamp.
 *  @author Kay Kasemir
 */
public class TimestampWidget extends Composite
{
    private Label current_label;
    private SWTCalendar calendar;
    private Spinner hour, minute, second;
    private ITimestamp timestamp = TimestampFactory.createTimestamp();
    /** Used to prevent recursion when the widget updates the GUI,
     *  which in turn fires listener notifications...
     */
    private boolean in_GUI_update = false;
    
    private ArrayList<TimestampWidgetListener> listeners
       = new ArrayList<TimestampWidgetListener>();

    /** Construct widget, initialized to the 'current' time.
     *  @param parent Widget parent.
     *  @param flags SWT widget flags.
     */
    public TimestampWidget(Composite parent, int flags)
    {
        this(parent, flags, TimestampFactory.now());
    }
        
    /** Construct widget, initialized to given time.
     *  @param parent Widget parent.
     *  @param flags SWT widget flags.
     */
    public TimestampWidget(Composite parent, int flags, ITimestamp stamp)
    {
        super(parent, flags);
        GridLayout layout = new GridLayout();
        layout.numColumns = 6;
        setLayout(layout);
        GridData gd;
        
        // current            [ now ]
        // |                           |
        // |          Calendar         |
        // |                           |
        // Time: (hour)+- : (minute)+- : (second)+-
        //                       [Midnight] [Noon]
        current_label = new Label(this, SWT.BOLD);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns - 1;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        current_label.setLayoutData(gd);
        
        Button now = new Button(this, SWT.PUSH);
        now.setText(Messages.Time_Now);
        now.setToolTipText(Messages.Time_Now_TT);
        gd = new GridData();
        gd.horizontalAlignment = SWT.RIGHT;
        now.setLayoutData(gd);

        // New row
        calendar = new SWTCalendar(this, SWTCalendar.RED_WEEKEND);
        calendar.setToolTipText(Messages.Time_SelectDate);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessVerticalSpace = true;
        gd.verticalAlignment = SWT.FILL;
        calendar.setLayoutData(gd);
        
        // New row
        Label l = new Label(this, SWT.NONE);
        l.setText(Messages.Time_Time);
        gd = new GridData();
        l.setLayoutData(gd);

        hour = new Spinner(this, SWT.BORDER | SWT.WRAP);
        hour.setToolTipText(Messages.Time_SelectHour);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        hour.setLayoutData(gd);
        hour.setMinimum(0);
        hour.setMaximum(23);
        hour.setIncrement(1);
        hour.setPageIncrement(6);
        l = new Label(this, SWT.NONE);
        l.setText(Messages.Time_Sep);
        gd = new GridData();
        l.setLayoutData(gd);

        minute = new Spinner(this, SWT.BORDER | SWT.WRAP);
        hour.setToolTipText(Messages.Time_SelectMinute);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        minute.setLayoutData(gd);
        minute.setMinimum(0);
        minute.setMaximum(59);
        minute.setIncrement(1);
        minute.setPageIncrement(10);

        l = new Label(this, SWT.NONE);
        l.setText(Messages.Time_Sep);
        gd = new GridData();
        l.setLayoutData(gd);

        second = new Spinner(this, SWT.BORDER | SWT.WRAP);
        hour.setToolTipText(Messages.Time_SelectSeconds);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        second.setLayoutData(gd);
        second.setMinimum(0);
        second.setMaximum(59);
        second.setIncrement(1);
        second.setPageIncrement(10);
        
        // New row        
        Button midnight = new Button(this, SWT.PUSH);
        midnight.setText(Messages.Time_Midnight);
        midnight.setToolTipText(Messages.Time_Midnight_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = layout.numColumns - 1;
        gd.horizontalAlignment = SWT.RIGHT;
        midnight.setLayoutData(gd);

        Button noon = new Button(this, SWT.PUSH);
        noon.setText(Messages.Time_Noon);
        noon.setToolTipText(Messages.Time_Noon_TT);
        gd = new GridData();
        gd.horizontalAlignment = SWT.RIGHT;
        noon.setLayoutData(gd);
        
        // Initialize to 'now'
        setTimestamp(stamp);
        
        // Hookup listeners
        now.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if (!in_GUI_update)
                    setNow();
            }
        });
        midnight.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if (!in_GUI_update)
                {
                    hour.setSelection(0);
                    minute.setSelection(0);
                    second.setSelection(0);
                    updateTimestampFromGUI();
                }
            }
        });
        noon.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if (!in_GUI_update)
                {
                    hour.setSelection(12);
                    minute.setSelection(0);
                    second.setSelection(0);
                    updateTimestampFromGUI();
                }
            }
        });
        calendar.addSWTCalendarListener(new SWTCalendarListener()
        {
            public void dateChanged(SWTCalendarEvent calendarEvent)
            {
                if (!in_GUI_update)
                    updateTimestampFromGUI();
            }
        });
        SelectionAdapter update = new SelectionAdapter() 
        {
            public void widgetSelected(SelectionEvent e)
            {
                if (!in_GUI_update)
                    updateTimestampFromGUI();
            }
        };
        hour.addSelectionListener(update);
        minute.addSelectionListener(update);
        second.addSelectionListener(update);
    }
    
    /** Add given listener. */
    public void addListener(TimestampWidgetListener listener)
    {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }
    
    /** Remove given listener. */
    public void removeListener(TimestampWidgetListener listener)
    {
        listeners.remove(listener);
    }
    
    /** Set the widget to display the given time.
     *  @see #setNow()
     */
    public void setTimestamp(ITimestamp stamp)
    {
        // Initialize with 0 nanoseconds.
        // Since we don't show and allow modification of the
        // nanoseconds, odd things can happen in start/end time
        // comparisons when there are 'hidden', non-zero nanosecs.
        timestamp.setSecondsAndNanoseconds(stamp.seconds(), 0);
        updateGUIfromTimestamp();
    }

    /** Set the widget to display the current time.
     *  @see #setTimestamp(ITimestamp)
     */
    public void setNow()
    {
        setTimestamp(TimestampFactory.now());
    }

    /** @return Returns the currently selected time. */
    public ITimestamp getTimestamp()
    {
        return timestamp;
    }
    
    /** Update the timestamp from the interactive GUI elements. */
    private void updateTimestampFromGUI()
    {
        Calendar cal = calendar.getCalendar();
        cal.set(Calendar.HOUR_OF_DAY, hour.getSelection());
        cal.set(Calendar.MINUTE, minute.getSelection());
        cal.set(Calendar.SECOND, second.getSelection());
        long millisec = cal.getTime().getTime();
        long seconds = millisec / 1000;
        timestamp.setSeconds(seconds);
        updateGUIfromTimestamp();
    }

    /** Display the current value of the timestamp on the GUI. */
    private void updateGUIfromTimestamp()
    {
        long pieces[] = timestamp.toPieces();
        Calendar cal = Calendar.getInstance();
        cal.set((int)pieces[ITimestamp.YEAR],
                (int)pieces[ITimestamp.MONTH] - 1,
                (int)pieces[ITimestamp.DAY],
                0, 0, 0);
        in_GUI_update = true;
        calendar.setCalendar(cal);
        hour.setSelection((int)pieces[ITimestamp.HOUR]);
        minute.setSelection((int)pieces[ITimestamp.MINUTE]);
        second.setSelection((int)pieces[ITimestamp.SECOND]);
        current_label.setText(timestamp.format(ITimestamp.FMT_DATE_HH_MM_SS));
        in_GUI_update = false;
        
        // fireUpdatedTimestamp
        for (TimestampWidgetListener l : listeners)
            l.updatedTimestamp(this, timestamp);
    }
}
