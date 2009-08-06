package org.csstudio.opibuilder.editor;


import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.actions.CutWidgetsAction;
import org.csstudio.opibuilder.actions.ChangeOrderAction.OrderType;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.CommandStack;
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
public final class OPIEditorContextMenuProvider extends ContextMenuProvider {
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
		assert actionRegistry != null : "actionRegistry is null"; //$NON-NLS-1$
		this.actionRegistry = actionRegistry;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildContextMenu(final IMenuManager menu) {		
		GEFActionConstants.addStandardActionGroups(menu);
		
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, 
				getAction(ActionFactory.UNDO.getId()));
		menu.appendToGroup(
				GEFActionConstants.GROUP_UNDO, 
				getAction(ActionFactory.REDO.getId()));
		menu.appendToGroup(
				GEFActionConstants.GROUP_EDIT,
				getAction(ActionFactory.COPY.getId()));
		menu.appendToGroup(
				GEFActionConstants.GROUP_EDIT,
				getAction(ActionFactory.CUT.getId()));
		((WorkbenchPartAction)getAction(ActionFactory.PASTE.getId())).update();
		menu.appendToGroup(
				GEFActionConstants.GROUP_EDIT,
				getAction(ActionFactory.PASTE.getId()));
		
		menu.appendToGroup(
				GEFActionConstants.GROUP_EDIT,
				getAction(ActionFactory.DELETE.getId()));
		
		
		String orderGroup = "Order";
		MenuManager orderMenu = new MenuManager(orderGroup, 
				CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
						OPIBuilderPlugin.PLUGIN_ID, "icons/order.png"), null);	 //$NON-NLS-1$	
		orderMenu.add(new Separator(orderGroup));
		orderMenu.appendToGroup(orderGroup, getAction(OrderType.TO_FRONT.getActionID()));
		orderMenu.appendToGroup(orderGroup, getAction(OrderType.STEP_FRONT.getActionID()));
		orderMenu.appendToGroup(orderGroup, getAction(OrderType.STEP_BACK.getActionID()));
		orderMenu.appendToGroup(orderGroup, getAction(OrderType.TO_BACK.getActionID()));
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, orderMenu);
		
		MenuManager cssMenu = new MenuManager("CSS", "css");
		cssMenu.add(new Separator("additions"));
		menu.add(cssMenu);		
	}
	
	private IAction getAction(String actionId) {
		return actionRegistry.getAction(actionId);
	}

}
