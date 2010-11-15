package org.csstudio.opibuilder.runmode;


import java.util.List;

import org.csstudio.opibuilder.actions.CompactModeAction;
import org.csstudio.opibuilder.actions.ConfigureRuntimePropertiesAction;
import org.csstudio.opibuilder.actions.FullScreenAction;
import org.csstudio.opibuilder.actions.OpenRelatedDisplayAction;
import org.csstudio.opibuilder.actions.SendEMailAction;
import org.csstudio.opibuilder.actions.SendToElogAction;
import org.csstudio.opibuilder.actions.WidgetActionMenuAction;
import org.csstudio.opibuilder.actions.OpenRelatedDisplayAction.OPEN_DISPLAY_TARGET;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.csstudio.opibuilder.widgetActions.OpenDisplayAction;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

/**
 * ContextMenuProvider implementation for the OPI Runner.
 * 
 * @author Xihui Chen
 * 
 */
public final class OPIRunnerContextMenuProvider extends ContextMenuProvider {
	

	/**
	 * Constructor.
	 * 
	 * @param viewer
	 *            the graphical viewer
	 */
	public OPIRunnerContextMenuProvider(final EditPartViewer viewer) {
		super(viewer);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildContextMenu(final IMenuManager menu) {
		addSettingPropertiesAction(menu);
		addWidgetActionToMenu(menu);
		GEFActionConstants.addStandardActionGroups(menu);
		ActionRegistry actionRegistry =
			(ActionRegistry) PlatformUI.getWorkbench().getActiveWorkbenchWindow().
			getActivePage().getActiveEditor().getAdapter(ActionRegistry.class);
		
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, actionRegistry.getAction(FullScreenAction.ID));
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, actionRegistry.getAction(CompactModeAction.ID));

		if(SendToElogAction.isElogAvailable())
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, actionRegistry.getAction(SendToElogAction.ID));
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, actionRegistry.getAction(SendEMailAction.ID));
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, actionRegistry.getAction(ActionFactory.PRINT.getId()));

		MenuManager cssMenu = new MenuManager("CSS", "css");
		cssMenu.add(new Separator("additions")); //$NON-NLS-1$
		menu.add(cssMenu);		
	}
	
	/**
	 * Adds the defined {@link AbstractWidgetAction}s to the given {@link IMenuManager}.
	 * @param menu The {@link IMenuManager}
	 */
	@SuppressWarnings("unchecked")
	private void addWidgetActionToMenu(final IMenuManager menu) {
		List selectedEditParts = ((IStructuredSelection)getViewer().getSelection()).toList();
		if (selectedEditParts.size()==1) {
			if (selectedEditParts.get(0) instanceof AbstractBaseEditPart) {
				AbstractBaseEditPart editPart = (AbstractBaseEditPart) selectedEditParts.get(0);
				AbstractWidgetModel widget = editPart.getWidgetModel();
				
				//add menu Open, Open in New Tab and Open in New Window
				AbstractWidgetAction hookedAction = editPart.getHookedAction();
				if(hookedAction != null && hookedAction instanceof OpenDisplayAction){
					menu.add(new OpenRelatedDisplayAction(
							(OpenDisplayAction) hookedAction, OPEN_DISPLAY_TARGET.DEFAULT));
					menu.add(new OpenRelatedDisplayAction(
							(OpenDisplayAction) hookedAction, OPEN_DISPLAY_TARGET.TAB));
					menu.add(new OpenRelatedDisplayAction(
							(OpenDisplayAction) hookedAction, OPEN_DISPLAY_TARGET.NEW_WINDOW));					
				}
				
				ActionsInput ai = widget.getActionsInput();
				if(ai != null){
					List<AbstractWidgetAction> widgetActions = ai.getActionsList();
					if (!widgetActions.isEmpty()) {
						MenuManager actionMenu = new MenuManager("Actions", "actions");
						for (AbstractWidgetAction action : widgetActions) {
							actionMenu.add(new WidgetActionMenuAction(action));
						}
						menu.add(actionMenu);
					}
				}
			}
		}
	}
	
	private void addSettingPropertiesAction(final IMenuManager menu){
		List<?> selectedEditParts = ((IStructuredSelection)getViewer().getSelection()).toList();
		if (selectedEditParts.size()==1) {
			if (selectedEditParts.get(0) instanceof AbstractBaseEditPart) {
				AbstractBaseEditPart editPart = (AbstractBaseEditPart) selectedEditParts.get(0);
				AbstractWidgetModel widget = editPart.getWidgetModel();
				if(widget.getRuntimePropertyList() != null){
					menu.add(new ConfigureRuntimePropertiesAction(
							getViewer().getControl().getShell(), widget));
				}
			}

				
		}
		
		
	}
	
	

}
