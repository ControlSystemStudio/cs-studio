/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editor;


import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.actions.ChangeOrderAction.OrderType;
import org.csstudio.opibuilder.actions.ChangeOrientationAction.OrientationType;
import org.csstudio.opibuilder.actions.CopyPropertiesAction;
import org.csstudio.opibuilder.actions.PastePropertiesAction;
import org.csstudio.opibuilder.actions.ReplaceWidgetsAction;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;

/**
 * ContextMenuProvider implementation for the OPI editor.
 * 
 * @author Xihui Chen
 * 
 */
public class OPIEditorContextMenuProvider extends ContextMenuProvider {
	public static final String GROUP_GROUP = "group";
	/**
	 * The action registry.
	 */
	private ActionRegistry actionRegistry;

	/**
	 * Constructor.
	 * 
	 * @param viewer
	 *            the graphical viewer
	 * @param actionRegistry
	 *            the action registry
	 */
	public OPIEditorContextMenuProvider(final EditPartViewer viewer,
			final ActionRegistry actionRegistry) {
		super(viewer);
		Assert.isNotNull(actionRegistry);
		this.actionRegistry = actionRegistry;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildContextMenu(final IMenuManager menu) {		
		menu.add(new Separator(GEFActionConstants.GROUP_UNDO));
		menu.add(new Separator(GEFActionConstants.GROUP_COPY));
		menu.add(new Separator(GEFActionConstants.GROUP_PRINT));
		menu.add(new Separator(GEFActionConstants.GROUP_EDIT));
		menu.add(new Separator(GEFActionConstants.GROUP_VIEW));
		menu.add(new Separator(GEFActionConstants.GROUP_FIND));
		menu.add(new Separator(GEFActionConstants.GROUP_ADD));
		menu.add(new Separator(GEFActionConstants.GROUP_REST));
		menu.add(new Separator(GEFActionConstants.GROUP_SAVE));
		menu.add(new Separator(GROUP_GROUP)); //$NON-NLS-1$
		menu.add(new Separator(GEFActionConstants.MB_ADDITIONS));
		
		
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, 
				getAction(ActionFactory.UNDO.getId()));
		menu.appendToGroup(
				GEFActionConstants.GROUP_UNDO, 
				getAction(ActionFactory.REDO.getId()));
		menu.appendToGroup(
				GEFActionConstants.GROUP_COPY,
				getAction(ActionFactory.COPY.getId()));		
		menu.appendToGroup(
				GEFActionConstants.GROUP_COPY,
				getAction(ActionFactory.CUT.getId()));
		((WorkbenchPartAction)getAction(ActionFactory.PASTE.getId())).update();
		menu.appendToGroup(
				GEFActionConstants.GROUP_COPY,
				getAction(ActionFactory.PASTE.getId()));
		
		menu.appendToGroup(
				GEFActionConstants.GROUP_COPY,
				getAction(CopyPropertiesAction.ID));
		menu.appendToGroup(
				GEFActionConstants.GROUP_COPY,
				getAction(PastePropertiesAction.ID));
		menu.appendToGroup(
				GEFActionConstants.GROUP_COPY,
				getAction(ReplaceWidgetsAction.ID));
		menu.appendToGroup(
				GEFActionConstants.GROUP_EDIT,
				getAction(ActionFactory.DELETE.getId()));
		
		menu.appendToGroup(
				GEFActionConstants.GROUP_EDIT,
				getAction(ActionFactory.PRINT.getId()));
		
		String orderGroup = "Order";
		MenuManager orderMenu = new MenuManager(orderGroup, 
				CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
						OPIBuilderPlugin.PLUGIN_ID, "icons/shape_move_front.png"), null);	 //$NON-NLS-1$	
		orderMenu.add(new Separator(orderGroup));
		for(OrderType orderType : OrderType.values()){
			orderMenu.appendToGroup(orderGroup, getAction(orderType.getActionID()));
		}
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, orderMenu);
		
		String orientationGroup = "Orientation";
		MenuManager orientationMenu = new MenuManager(orientationGroup, 
				CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
						OPIBuilderPlugin.PLUGIN_ID, "icons/flip_horizontal.png"), null);	 //$NON-NLS-1$	
		orientationMenu.add(new Separator(orientationGroup));
		for(OrientationType orientationType : OrientationType.values()){
			orientationMenu.appendToGroup(orientationGroup, getAction(orientationType.getActionID()));

		}
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, orientationMenu);	
				
//		MenuManager cssMenu = new MenuManager("CSS", "css");//$NON-NLS-1$ //$NON-NLS-2$
//		cssMenu.add(new Separator("additions"));//$NON-NLS-1$
//		menu.add(cssMenu);		
	}
	
	private IAction getAction(String actionId) {
		return actionRegistry.getAction(actionId);
	}

}
