package org.csstudio.sds.ui.internal.editor;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.editparts.AbstractContainerEditPart;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.GroupingContainerEditPart;
import org.csstudio.sds.ui.internal.actions.CopyWidgetsAction;
import org.csstudio.sds.ui.internal.actions.CreateGroupAction;
import org.csstudio.sds.ui.internal.actions.MoveToBackAction;
import org.csstudio.sds.ui.internal.actions.MoveToFrontAction;
import org.csstudio.sds.ui.internal.actions.PasteWidgetsAction;
import org.csstudio.sds.ui.internal.actions.RemoveGroupAction;
import org.csstudio.sds.ui.internal.actions.StepBackAction;
import org.csstudio.sds.ui.internal.actions.StepFrontAction;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;

/**
 * ContextMenuProvider implementation for the display editor.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class DisplayContextMenuProvider extends ContextMenuProvider {
	/**
	 * The action registry.
	 */
	private ActionRegistry _actionRegistry;

	/**
	 * Constructor.
	 * 
	 * @param viewer
	 *            the graphical viewer
	 * @param actionRegistry
	 *            the action registry
	 */
	public DisplayContextMenuProvider(final EditPartViewer viewer,
			final ActionRegistry actionRegistry) {
		super(viewer);
		assert actionRegistry != null : "actionRegistry!=null"; //$NON-NLS-1$
		_actionRegistry = actionRegistry;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildContextMenu(final IMenuManager menu) {		
		GEFActionConstants.addStandardActionGroups(menu);
		
		// add Grouping Actions
		this.addGroupingActions(menu);
		
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, _actionRegistry
				.getAction(CopyWidgetsAction.ID));
		
		PasteWidgetsAction action = (PasteWidgetsAction) _actionRegistry
				.getAction(PasteWidgetsAction.ID);
		action.fetchCurrentCursorLocation();
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, _actionRegistry
				.getAction(ActionFactory.UNDO.getId()));

		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, _actionRegistry
				.getAction(ActionFactory.REDO.getId()));

		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, _actionRegistry
				.getAction(ActionFactory.DELETE.getId()));
		//menu.add(new Separator("ChangeOrder"));
		MenuManager orderMenu = new MenuManager("Order");
		orderMenu.add(new Separator("order"));
		orderMenu.appendToGroup("order", _actionRegistry
				.getAction(MoveToFrontAction.ID));
		orderMenu.appendToGroup("order", _actionRegistry
				.getAction(StepFrontAction.ID));
		orderMenu.appendToGroup("order", _actionRegistry
				.getAction(StepBackAction.ID));
		orderMenu.appendToGroup("order", _actionRegistry
				.getAction(MoveToBackAction.ID));
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, orderMenu);
		
		MenuManager cssMenu = new MenuManager("CSS", "css");
		cssMenu.add(new Separator("additions"));
		menu.add(cssMenu);
		
	}
	
	/**
	 * Adds the grouping actions.
	 * @param menu The {@link IMenuManager}, where the actions are added
	 */
	@SuppressWarnings("unchecked")
	private void addGroupingActions(final IMenuManager menu) {
		CommandStack commandStack = this.getViewer().getEditDomain().getCommandStack();
		List editParts = this.getViewer().getSelectedEditParts();
		if (editParts.size()==1 && editParts.get(0) instanceof GroupingContainerEditPart) {
			menu.appendToGroup(GEFActionConstants.GROUP_ADD, new RemoveGroupAction(commandStack, (GroupingContainerEditPart)editParts.get(0)));
		} else if (editParts.size()>1) {
			List<AbstractWidgetEditPart> childrenList = new ArrayList<AbstractWidgetEditPart>(editParts.size());
			for (Object object : editParts) {
				if (object instanceof AbstractContainerEditPart) {
					for (Object child : ((AbstractContainerEditPart)object).getChildren()) {
						childrenList.add((AbstractWidgetEditPart) child);
					}
				}
			}
			List<AbstractWidgetModel> widgetList = new ArrayList<AbstractWidgetModel>(editParts.size());
			for (Object object : editParts) {
				if (object instanceof AbstractBaseEditPart && !childrenList.contains(object)) {
					widgetList.add(((AbstractBaseEditPart)object).getWidgetModel());
				}
			}
			if (!widgetList.isEmpty()) {
				menu.appendToGroup(GEFActionConstants.GROUP_ADD, new CreateGroupAction(commandStack, widgetList));
			}
		}
	}

}
