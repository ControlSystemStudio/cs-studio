package org.csstudio.sds.ui.internal.runmode;

import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.properties.actions.WidgetAction;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.widgetactionhandler.WidgetActionHandlerService;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * ContextMenuProvider implementation for the display editor.
 * 
 * @author Sven Wende, Kai Meyer
 * @version $Revision$
 * 
 */
public final class RunModeContextMenuProvider extends ContextMenuProvider {
	
	/**
	 * The ID for the close action.
	 */
	public static final String CLOSE_ACTION_ID = "closeAction";
	/**
	 * The action registry.
	 */
	private ActionRegistry _actionRegistry;
	
	/**
	 * The selected widget model.
	 */
	private AbstractWidgetModel _selectedWidgetModel;

	/**
	 * Constructor.
	 * 
	 * @param viewer
	 *            the graphical viewer
	 * @param actionRegistry
	 *            the action registry
	 */
	public RunModeContextMenuProvider(final EditPartViewer viewer,
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
		IAction closeAction = _actionRegistry.getAction(CLOSE_ACTION_ID);
		if (closeAction!=null) {
			menu.add(closeAction);
		}
		GEFActionConstants.addStandardActionGroups(menu);
		this.addWidgetActionToMenu(menu);
		MenuManager cssMenu = new MenuManager("CSS", "css");
		cssMenu.add(new Separator("additions"));
		menu.add(cssMenu);
	}
	
	/**
	 * Adds the defined {@link WidgetAction}s to the given {@link IMenuManager}.
	 * @param menu The {@link IMenuManager}
	 */
	@SuppressWarnings("unchecked")
	private void addWidgetActionToMenu(final IMenuManager menu) {
		List selectedEditParts = this.getViewer().getSelectedEditParts();
		if (selectedEditParts.size()==1) {
			if (selectedEditParts.get(0) instanceof AbstractBaseEditPart) {
				AbstractBaseEditPart editPart = (AbstractBaseEditPart) selectedEditParts.get(0);
				_selectedWidgetModel = editPart.getWidgetModel();
				List<WidgetAction> widgetActions = _selectedWidgetModel.getActionData().getWidgetActions();
				if (!widgetActions.isEmpty()) {
					MenuManager actionMenu = new MenuManager("Actions", "actions");
					for (WidgetAction action : widgetActions) {
						actionMenu.add(new MenuAction(action));
					}
					menu.add(actionMenu);
				}
			}
		}
	}
	
	/**
	 * An Action, which encapsulates a {@link WidgetAction}.
	 * @author Kai Meyer
	 *
	 */
	private final class MenuAction extends Action {
		/**
		 * The {@link WidgetAction}.
		 */
		private WidgetAction _widgetAction;
		
		/**
		 * Constructor.
		 * @param widgetAction The encapsulated {@link WidgetAction}
		 */
		public MenuAction(final WidgetAction widgetAction) {
			_widgetAction = widgetAction;
			this.setText(_widgetAction.getActionLabel());
			IWorkbenchAdapter adapter = (IWorkbenchAdapter) Platform.getAdapterManager().getAdapter(widgetAction, IWorkbenchAdapter.class);
			if (adapter!=null) {
				this.setImageDescriptor(adapter.getImageDescriptor(widgetAction));
			}
			this.setEnabled(widgetAction.isEnabled());
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			WidgetActionHandlerService.getInstance().performAction(_selectedWidgetModel.getProperty(AbstractWidgetModel.PROP_ACTIONDATA), _widgetAction);
		}
	}

}
