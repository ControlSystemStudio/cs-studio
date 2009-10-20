package org.csstudio.opibuilder.editor;


import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.actions.CopyPropertiesAction;
import org.csstudio.opibuilder.actions.PastePropertiesAction;
import org.csstudio.opibuilder.actions.PrintDisplayAction;
import org.csstudio.opibuilder.actions.ShowMacrosAction;
import org.csstudio.opibuilder.actions.ChangeOrderAction.OrderType;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.PlatformUI;
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
				getAction(ShowMacrosAction.ID));
		menu.appendToGroup(
				GEFActionConstants.GROUP_EDIT,
				getAction(ActionFactory.DELETE.getId()));
		
		menu.appendToGroup(
				GEFActionConstants.GROUP_EDIT,
				getAction(ActionFactory.PRINT.getId()));
		
		String orderGroup = "Order";
		MenuManager orderMenu = new MenuManager(orderGroup, 
				CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
						OPIBuilderPlugin.PLUGIN_ID, "icons/order.png"), null);	 //$NON-NLS-1$	
		orderMenu.add(new Separator(orderGroup));
		orderMenu.appendToGroup(orderGroup, getAction(OrderType.TO_FRONT.getActionID()));
		orderMenu.appendToGroup(orderGroup, getAction(OrderType.STEP_FRONT.getActionID()));
		orderMenu.appendToGroup(orderGroup, getAction(OrderType.STEP_BACK.getActionID()));
		orderMenu.appendToGroup(orderGroup, getAction(OrderType.TO_BACK.getActionID()));
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, orderMenu);
		
		menu.add(new Separator("group")); //$NON-NLS-1$
		
		MenuManager cssMenu = new MenuManager("CSS", "css");//$NON-NLS-1$ //$NON-NLS-2$
		cssMenu.add(new Separator("additions"));//$NON-NLS-1$
		menu.add(cssMenu);		
	}
	
	private IAction getAction(String actionId) {
		return actionRegistry.getAction(actionId);
	}

}
