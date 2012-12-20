/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.areapanel;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.ui.ContextMenuHelper;
import org.csstudio.alarm.beast.ui.SeverityColorProvider;
import org.csstudio.alarm.beast.ui.actions.AlarmPerspectiveAction;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;

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
	public AlarmPanelItem(final Composite parent, final SeverityColorProvider color_provider,
			final AlarmTreeItem item)
    {
		super(parent, SWT.DOUBLE_BUFFERED);
		this.color_provider = color_provider;
		this.item = item;
		addPaintListener(this);
		addContextMenu();
    }
	
	private void addContextMenu()
    {
		final List<AlarmTreeItem> item_as_list = new ArrayList<AlarmTreeItem>(1);
		item_as_list.add(item);

		final MenuManager manager = new MenuManager();
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener()
		{
			@Override
			public void menuAboutToShow(IMenuManager manager)
			{
				manager.add(new ShowInAlarmTreeAction(item));
				new ContextMenuHelper(null, manager, getShell(), item_as_list, true);
                manager.add(new Separator());
                manager.add(new AlarmPerspectiveAction());
                manager.add(new Separator());
                manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		final Menu menu = manager.createContextMenu(this);
		setMenu(menu);
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
