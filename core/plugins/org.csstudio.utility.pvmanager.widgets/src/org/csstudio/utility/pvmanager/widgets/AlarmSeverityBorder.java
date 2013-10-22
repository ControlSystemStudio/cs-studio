package org.csstudio.utility.pvmanager.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.ValueUtil;

public class AlarmSeverityBorder extends Composite {
	private AlarmSeverity alarmSeverity = AlarmSeverity.NONE;
	private int borderSize = 3;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public AlarmSeverityBorder(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
		FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
		fillLayout.marginWidth = 0;
		fillLayout.marginHeight = 0;
		setLayout(fillLayout);
	}
	
	public AlarmSeverity getAlarmSeverity() {
		return alarmSeverity;
	}
	
	public void setAlarmSeverity(AlarmSeverity alarmSeverity) {
		if (alarmSeverity == null) {
			throw new NullPointerException("Alarm severity should not be null");
		}
		
		if (alarmSeverity.equals(this.alarmSeverity)) {
			return;
		}
		
		AlarmSeverity oldAlarmSeverity = this.alarmSeverity;
		this.alarmSeverity = alarmSeverity;
		
		java.awt.Color awtColor = new java.awt.Color(ValueUtil.colorFor(alarmSeverity));
		setBackground(SWTResourceManager.getColor(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue()));
		
		if (AlarmSeverity.NONE.equals(oldAlarmSeverity) || AlarmSeverity.NONE.equals(alarmSeverity)) {
			int newBorderSize;
			if (AlarmSeverity.NONE.equals(alarmSeverity)) {
				newBorderSize = 0;
			} else {
				newBorderSize = borderSize;
			}
			FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
			fillLayout.marginWidth = newBorderSize;
			fillLayout.marginHeight = newBorderSize;
			setLayout(fillLayout);
			this.layout();
		}
	}
	
	

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
