/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.util.time.swt;

import java.util.ArrayList;
import java.util.Calendar;

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

/** Widget for displaying and selecting an absolute date and time.
 *  @author Kay Kasemir
 */
public class CalendarWidget extends Composite
{
    /** The SWTCalendar widget, actually only handles the date. */
    private SWTCalendar date;
    /** Widgets for time pieces. */
    private Spinner hour, minute, second;
    /** The currently configed calendar (date and time). */
    private Calendar calendar;
    
    /** Used to prevent recursion when the widget updates the GUI,
     *  which in turn fires listener notifications...
     */
    private boolean in_GUI_update = false;
    
    private ArrayList<CalendarWidgetListener> listeners
       = new ArrayList<CalendarWidgetListener>();

    /** Construct widget, initialized to the 'current' time.
     *  @param parent Widget parent.
     *  @param flags SWT widget flags.
     */
    public CalendarWidget(Composite parent, int flags)
    {
        this(parent, flags, Calendar.getInstance());
    }
        
    /** Construct widget, initialized to given time.
     *  @param parent Widget parent.
     *  @param flags SWT widget flags.
     */
    public CalendarWidget(Composite parent, int flags, Calendar calendar)
    {
        super(parent, flags);
        GridLayout layout = new GridLayout();
        layout.numColumns = 6;
        setLayout(layout);
        GridData gd;
        
        // |                                      |
        // |              Calendar                |
        // |                                      |
        // Time: (hour)+- : (minute)+- : (second)+-
        //                        [Midnight] [Noon]

        date = new SWTCalendar(this, SWTCalendar.RED_WEEKEND);
        date.setToolTipText(Messages.Time_SelectDate);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessVerticalSpace = true;
        gd.verticalAlignment = SWT.FILL;
        date.setLayoutData(gd);
        
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
        minute.setToolTipText(Messages.Time_SelectMinute);
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
        second.setToolTipText(Messages.Time_SelectSeconds);
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
        setCalendar(calendar);
        
        midnight.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (!in_GUI_update)
                {
                    hour.setSelection(0);
                    minute.setSelection(0);
                    second.setSelection(0);
                    updateDataFromGUI();
                }
            }
        });
        noon.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (!in_GUI_update)
                {
                    hour.setSelection(12);
                    minute.setSelection(0);
                    second.setSelection(0);
                    updateDataFromGUI();
                }
            }
        });
        date.addSWTCalendarListener(new SWTCalendarListener()
        {
            public void dateChanged(SWTCalendarEvent calendarEvent)
            {
                if (!in_GUI_update)
                    updateDataFromGUI();
            }
        });
        SelectionAdapter update = new SelectionAdapter() 
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (!in_GUI_update)
                    updateDataFromGUI();
            }
        };
        hour.addSelectionListener(update);
        minute.addSelectionListener(update);
        second.addSelectionListener(update);
    }
    
    /** Add given listener. */
    public void addListener(CalendarWidgetListener listener)
    {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }
    
    /** Remove given listener. */
    public void removeListener(CalendarWidgetListener listener)
    {
        listeners.remove(listener);
    }
    
    /** Set the widget to display the given time.
     *  @see #setNow()
     */
    public void setCalendar(Calendar calendar)
    {
        this.calendar = calendar;
        updateGUIfromData();
    }

    /** @return Returns the currently selected time. */
    public Calendar getCalendar()
    {
        return (Calendar) calendar.clone();
    }
    
    /** Update the data from the interactive GUI elements. */
    private void updateDataFromGUI()
    {
        calendar = date.getCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, hour.getSelection());
        calendar.set(Calendar.MINUTE, minute.getSelection());
        calendar.set(Calendar.SECOND, second.getSelection());
        updateGUIfromData();
    }

    /** Display the current value of the data on the GUI. */
    private void updateGUIfromData()
    {
        in_GUI_update = true;
        date.setCalendar(calendar);
        hour.setSelection(calendar.get(Calendar.HOUR_OF_DAY));
        minute.setSelection(calendar.get(Calendar.MINUTE));
        second.setSelection(calendar.get(Calendar.SECOND));
        in_GUI_update = false;
        
        // fireUpdatedTimestamp
        for (CalendarWidgetListener l : listeners)
            l.updatedCalendar(this, calendar);
    }
}
