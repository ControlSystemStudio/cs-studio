package org.csstudio.channel.widgets.util;

import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.wb.swt.SWTResourceManager;

public class ToolTipHelp extends DefaultToolTip {
	
	private static Color[] windowsBackground =
			new Color[] {SWTResourceManager.getColor(255,255,255), SWTResourceManager.getColor(228,229,240)};
	private static int[] colorSteps = new int[] {100};
	
	private Control control;

	public ToolTipHelp(Control control) {
		super(control, NO_RECREATE, false);
		this.control = control;
	}
	
	@Override
	protected Composite createToolTipContentArea(Event event,
			Composite parent) {
		setShift(new Point(- event.x, control.getSize().y - event.y));
		CLabel label = (CLabel) super.createToolTipContentArea(event, parent);
		// The tooltip background on windows 7 does not match
		// Hack a better match for standard configuration
		if ("Windows 7".equals(System.getProperty("os.name"))) {
			label.setBackground(windowsBackground, colorSteps, true);
		}
		return label;
	}
}
