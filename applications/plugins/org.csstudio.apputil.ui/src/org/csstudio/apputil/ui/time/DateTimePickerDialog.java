package org.csstudio.apputil.ui.time;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**A datetime picker dialog.
 * @author Xihui Chen
 *
 */
public class DateTimePickerDialog extends Dialog {

	private CalendarWidget calendarWidget;
	private Date dateTime;


	/**Create a datetime picker dialog.
	 * @param parentShell the parent shell, or null to create a top-level shell
	 */
	public DateTimePickerDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Select Datetime"); //$NON-NLS-1$
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);

		calendarWidget = new CalendarWidget(area, SWT.None);
		if(dateTime != null){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateTime);
		calendarWidget.setCalendar(calendar);
		}
		return area;
	}

	/**Set the date time of the calendar when dialog is open.
	 * @param dateTime
	 */
	public void setDateTime(Date dateTime){
		this.dateTime = dateTime;
	}

	/**
	 * @return the datetime picked.
	 */
	public Date getDateTime(){
		return calendarWidget.getCalendar().getTime();
	}


}
