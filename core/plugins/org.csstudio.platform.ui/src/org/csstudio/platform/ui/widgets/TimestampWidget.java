/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.platform.ui.widgets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.csstudio.platform.ui.internal.localization.Messages;
import org.csstudio.platform.ui.internal.vafada.swtcalendar.SWTCalendar;
import org.csstudio.platform.ui.internal.vafada.swtcalendar.SWTCalendarEvent;
import org.csstudio.platform.ui.internal.vafada.swtcalendar.SWTCalendarListener;
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

/**
 * Widget for displaying and setting a CSS Timestamp.
 * 
 * @author Kay Kasemir
 */
public final class TimestampWidget extends Composite {
	/**
	 * A label widget.
	 */
	private Label _currentLabel;

	/**
	 * A calendar widget.
	 */
	private SWTCalendar _calendar;

	/**
	 * Some spinner widgets.
	 */
	private Spinner _hour, _minute, _second;

	/**
	 * A timestamp.
	 */
	private ITimestamp _timestamp = TimestampFactory.createTimestamp();

	/**
	 * Used to prevent recursion when the widget updates the GUI, which in turn
	 * fires listener notifications...
	 */
	private boolean _inGuiUpdate = false;

	/**
	 * Contains the widget listeners.
	 */
	private List<TimestampWidgetListener> _listeners = new ArrayList<TimestampWidgetListener>();

	/**
	 * Construct widget, initialized to the 'current' time.
	 * 
	 * @param parent
	 *            Widget parent.
	 * @param flags
	 *            SWT widget flags.
	 */
	public TimestampWidget(final Composite parent, final int flags) {
		this(parent, flags, TimestampFactory.now());
	}

	/**
	 * Construct widget, initialized to given time.
	 * 
	 * @param stamp
	 *            the time stamp
	 * 
	 * @param parent
	 *            Widget parent.
	 * @param flags
	 *            SWT widget flags.
	 */
	public TimestampWidget(final Composite parent, final int flags,
			final ITimestamp stamp) {
		super(parent, flags);
		GridLayout layout = new GridLayout();
		layout.numColumns = 6;
		setLayout(layout);
		GridData gd;

		// current [ now ]
		// | |
		// | Calendar |
		// | |
		// Time: (hour)+- : (minute)+- : (second)+-
		_currentLabel = new Label(this, SWT.BOLD);
		gd = new GridData();
		gd.horizontalSpan = layout.numColumns - 1;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		_currentLabel.setLayoutData(gd);

		Button now = new Button(this, SWT.PUSH);
		now.setText(Messages.getString("TimeStampWidget.Time_Now"));
		now.setToolTipText(Messages.getString("TimeStampWidget.Time_Now_TT"));
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		now.setLayoutData(gd);

		_calendar = new SWTCalendar(this, SWTCalendar.RED_WEEKEND);
		_calendar.setToolTipText(Messages
				.getString("TimeStampWidget.Time_SelectDate"));
		gd = new GridData();
		gd.horizontalSpan = layout.numColumns;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessVerticalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		_calendar.setLayoutData(gd);

		Label l = new Label(this, SWT.NONE);
		l.setText(Messages.getString("TimeStampWidget.Time_Time"));
		gd = new GridData();
		l.setLayoutData(gd);

		_hour = new Spinner(this, SWT.BORDER | SWT.WRAP);
		_hour.setToolTipText(Messages
				.getString("TimeStampWidget.Time_SelectHour"));
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		_hour.setLayoutData(gd);
		_hour.setMinimum(0);
		_hour.setMaximum(23);
		_hour.setIncrement(1);
		_hour.setPageIncrement(6);
		l = new Label(this, SWT.NONE);
		l.setText(Messages.getString("TimeStampWidget.Time_Sep"));
		gd = new GridData();
		l.setLayoutData(gd);

		_minute = new Spinner(this, SWT.BORDER | SWT.WRAP);
		_hour.setToolTipText(Messages
				.getString("TimeStampWidget.Time_SelectMinute"));
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		_minute.setLayoutData(gd);
		_minute.setMinimum(0);
		_minute.setMaximum(59);
		_minute.setIncrement(1);
		_minute.setPageIncrement(10);

		l = new Label(this, SWT.NONE);
		l.setText(Messages.getString("TimeStampWidget.Time_Sep"));
		gd = new GridData();
		l.setLayoutData(gd);

		_second = new Spinner(this, SWT.BORDER | SWT.WRAP);
		_hour.setToolTipText(Messages
				.getString("TimeStampWidget.Time_SelectSeconds"));
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		_second.setLayoutData(gd);
		_second.setMinimum(0);
		_second.setMaximum(59);
		_second.setIncrement(1);
		_second.setPageIncrement(10);

		// Initialize to 'now'
		setTimestamp(stamp);

		// Hookup listeners
		now.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (!_inGuiUpdate) {
					setNow();
				}
			}
		});
		_calendar.addSWTCalendarListener(new SWTCalendarListener() {
			public void dateChanged(final SWTCalendarEvent calendarEvent) {
				if (!_inGuiUpdate) {
					updateTimestampFromGUI();
				}
			}
		});
		SelectionAdapter update = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (!_inGuiUpdate) {
					updateTimestampFromGUI();
				}
			}
		};
		_hour.addSelectionListener(update);
		_minute.addSelectionListener(update);
		_second.addSelectionListener(update);
	}

	/**
	 * Add given listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addListener(final TimestampWidgetListener listener) {
		if (!_listeners.contains(listener)) {
			_listeners.add(listener);
		}
	}

	/**
	 * Remove given listener. *
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeListener(final TimestampWidgetListener listener) {
		_listeners.remove(listener);
	}

	/**
	 * Set the widget to display the given time.
	 * 
	 * @param stamp
	 *            the time stamp
	 * @see #setNow()
	 */
	public void setTimestamp(final ITimestamp stamp) {
		// Initialize with 0 nanoseconds.
		// Since we don't show and allow modification of the
		// nanoseconds, odd things can happen in start/end time
		// comparisons when there are 'hidden', non-zero nanosecs.
		_timestamp = TimestampFactory.createTimestamp(stamp.seconds(), 0);
		updateGUIfromTimestamp();
	}

	/**
	 * Set the widget to display the current time.
	 * 
	 * @see #setTimestamp(ITimestamp)
	 */
	public void setNow() {
		setTimestamp(TimestampFactory.now());
	}

	/** @return Returns the currently selected time. */
	public ITimestamp getTimestamp() {
		return _timestamp;
	}

	/** Update the timestamp from the interactive GUI elements. */
	private void updateTimestampFromGUI() {
		Calendar cal = _calendar.getCalendar();
		cal.set(Calendar.HOUR_OF_DAY, _hour.getSelection());
		cal.set(Calendar.MINUTE, _minute.getSelection());
		cal.set(Calendar.SECOND, _second.getSelection());
		long millisec = cal.getTime().getTime();
		long seconds = millisec / 1000;
		_timestamp.setSeconds(seconds);
		updateGUIfromTimestamp();
	}

	/** Display the current value of the timestamp on the GUI. */
	private void updateGUIfromTimestamp() {
		long[] pieces = _timestamp.toPieces();
		Calendar cal = Calendar.getInstance();
		cal.set((int) pieces[ITimestamp.YEAR],
				(int) pieces[ITimestamp.MONTH] - 1,
				(int) pieces[ITimestamp.DAY], 0, 0, 0);
		_inGuiUpdate = true;
		_calendar.setCalendar(cal);
		_hour.setSelection((int) pieces[ITimestamp.HOUR]);
		_minute.setSelection((int) pieces[ITimestamp.MINUTE]);
		_second.setSelection((int) pieces[ITimestamp.SECOND]);
		_currentLabel.setText(_timestamp.format(ITimestamp.FMT_DATE_HH_MM_SS));
		_inGuiUpdate = false;

		// fireUpdatedTimestamp
		for (TimestampWidgetListener l : _listeners) {
			l.updatedTimestamp(this, _timestamp);
		}
	}
}
