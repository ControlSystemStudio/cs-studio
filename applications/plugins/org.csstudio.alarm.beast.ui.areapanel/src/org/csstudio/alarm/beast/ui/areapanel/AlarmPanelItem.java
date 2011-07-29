/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.areapanel;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.ui.SeverityColorProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/** GUI element for one {@link AlarmTreeItem} on the alarm panel
 *  @author Kay Kasemir
 */
public class AlarmPanelItem extends Canvas implements PaintListener
{
	final private SeverityColorProvider color_provider;
	final private AlarmTreeItem item;
	private SeverityLevel last_alarm_state;

	/** Initialize
	 *  @param parent
	 *  @param color_provider
	 *  @param item
	 */
	public AlarmPanelItem(final Composite parent, final SeverityColorProvider color_provider, AlarmTreeItem item)
    {
		super(parent, SWT.DOUBLE_BUFFERED);
		this.color_provider = color_provider;
		this.item = item;
		addPaintListener(this);
    }
	
	/** Draw the item
	 *  {@inheritDoc}
	 */
	@Override
    public void paintControl(final PaintEvent e)
    {
		final Display display = getDisplay();
		final GC gc = e.gc;
		final Rectangle bounds = getBounds();

		last_alarm_state = item.getSeverity();
		gc.setBackground(color_provider.getColor(last_alarm_state));
		gc.fillRoundRectangle(0, 0, bounds.width, bounds.height, 10, 10);

		gc.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_BORDER));
		gc.drawRoundRectangle(0, 0, bounds.width-1, bounds.height-1, 10, 10);

		
		// Draw Text
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		final String label = item.getName();
		final Point extend = gc.textExtent(label);
		gc.drawString(label,
				(bounds.width - extend.x)/2,
				(bounds.height - extend.y)/2, true);
    }

	/** If the alarm tree item has changed since the last redraw,
	 *  refresh the display.
	 */
	public void updateAlarmState()
    {
		if (item.getSeverity().equals(last_alarm_state))
			return;
		redraw();
    }
}
