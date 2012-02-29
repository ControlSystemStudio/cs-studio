/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.commands;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractLayoutModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.util.SchemaService;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * The command to add a widget to a container.
 * 
 * @author Xihui Chen
 * 
 */
public class WidgetCreateCommand extends Command {

	private AbstractWidgetModel newWidget;

	private final AbstractContainerModel container;

	private Rectangle bounds;

	private boolean append;

	private Rectangle oldBounds;

	private int index = -1;



	/**
	 * @param newWidget
	 *            The new Widget to be added.
	 * @param container
	 *            the parent.
	 * @param bounds
	 *            the bounds for the new widget
	 * @param append
	 *            true if its selection is appended to other selections.
	 * @param applySchema
	 *            true if the new widget's properties are applied with schema.
	 */
	public WidgetCreateCommand(AbstractWidgetModel newWidget,
			AbstractContainerModel container, Rectangle bounds, boolean append,
			boolean applySchema) {
		this(newWidget, container, bounds, append);
		if (applySchema) {
			SchemaService.getInstance().applySchema(this.newWidget);
		}
	}



	/**
	 * @param newWidget
	 *            The new Widget to be added.
	 * @param container
	 *            the parent.
	 * @param bounds
	 *            the bounds for the new widget
	 * @param append
	 *            true if its selection is appended to other selections.
	 */
	public WidgetCreateCommand(AbstractWidgetModel newWidget,
			AbstractContainerModel container, Rectangle bounds,
			boolean append) {
		this.newWidget = newWidget;
		this.container = container;
		this.bounds = bounds;
		this.append = append;
		setLabel("create widget");
	}

	@Override
	public boolean canExecute() {
		return newWidget != null && container != null;
	}

	@Override
	public void execute() {
		oldBounds = newWidget.getBounds();
		newWidget.generateNewWUID();
		//If the new created widget has connections on it, remove their points.
		for(ConnectionModel conn : newWidget.getSourceConnections()){
			conn.setPoints(new PointList());
		}
		redo();
	}

	@Override
	public void redo() {
		if (newWidget instanceof AbstractLayoutModel
				&& container.getLayoutWidget() != null) {
			MessageDialog
					.openError(
							null,
							"Creating widget failed",
							"There is already a layout widget in the container. "
									+ "Please delete it before you can add a new layout widget.");
			return;
		}
		if (bounds != null) {
			newWidget.setLocation(bounds.x, bounds.y);
			if (bounds.width > 0 && bounds.height > 0)
				newWidget.setSize(bounds.width, bounds.height);
		}
		boolean autoName = false;
		for (AbstractWidgetModel child : container.getChildren()) {
			if (child.getName().equals(newWidget.getName()))
				autoName = true;
		}
		if (autoName) {
			Map<String, Integer> nameMap = new HashMap<String, Integer>();
			for (AbstractWidgetModel child : container.getChildren()) {
				String key = child.getName();
				int tailNo = 0;
				if (key.matches(".*_\\d+")) { //$NON-NLS-1$
					int i = key.lastIndexOf('_'); //$NON-NLS-1$
					tailNo = Integer.parseInt(key.substring(i + 1));
					key = key.substring(0, i);
				}
				if (nameMap.containsKey(key))
					nameMap.put(key, Math.max(nameMap.get(key) + 1, tailNo));
				else
					nameMap.put(key, 0);
			}
			String nameHead = newWidget.getName();
			if (nameHead.matches(".*_\\d+")) { //$NON-NLS-1$
				nameHead = nameHead.substring(0, nameHead.lastIndexOf('_')); //$NON-NLS-1$
			}
			newWidget.setName(nameHead
					+ "_" //$NON-NLS-1$
					+ (nameMap.get(nameHead) == null ? 0 : nameMap
							.get(nameHead) + 1));
		}
		container.addChild(index, newWidget);
		container.selectWidget(newWidget, append);
	}

	@Override
	public void undo() {
		newWidget.setBounds(oldBounds);
		container.removeChild(newWidget);		
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
