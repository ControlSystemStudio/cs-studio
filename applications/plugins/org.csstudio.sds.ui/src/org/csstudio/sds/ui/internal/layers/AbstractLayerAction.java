package org.csstudio.sds.ui.internal.layers;

import org.csstudio.sds.model.layers.Layer;
import org.csstudio.sds.model.layers.LayerSupport;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * Base action for layer manipulating actions.
 * 
 * @author swende
 * 
 */
abstract class AbstractLayerAction implements IViewActionDelegate {

	/**
	 * The active layer management view.
	 */
	private LayerManagementView _activeView;

	/**
	 * The current selected layer.
	 */
	private Layer _selectedLayer;

	/**
	 * {@inheritDoc}
	 */
	public final void init(final IViewPart view) {
		assert view instanceof LayerManagementView : "view instanceof LayerManagementView";
		_activeView = (LayerManagementView) view;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void selectionChanged(final IAction action,
			final ISelection selection) {
		IStructuredSelection sel = (IStructuredSelection) selection;

		if (sel != null && sel.getFirstElement() != null) {
			_selectedLayer = (Layer) sel.getFirstElement();
		} else {
			_selectedLayer = null;
		}

		selectedLayerChanged(_selectedLayer, _activeView.getCurrentLayerSupport(),  action);
	}

	/**
	 * {@inheritDoc}
	 */
	public final void run(final IAction action) {
		Command cmd = createCommand(_selectedLayer, _activeView
				.getCurrentLayerSupport(), action);

		if (cmd != null) {
			CommandStack commandStack = _activeView.getCommandStack();

			if (commandStack != null) {
				commandStack.execute(cmd);
			}
		}
		this.selectedLayerChanged(_selectedLayer, _activeView.getCurrentLayerSupport(), action);
	}

	/**
	 * Subclasses have to implement and return a command, which does the real
	 * action work.
	 * 
	 * @param selectedLayer
	 *            the currently selected layer
	 * @param layerSupport
	 *            the access class to the layer model
	 * @param action
	 *            the workbench proxy action
	 * @return a command, which does the work of this action or null, if the
	 *         provided parameters do not allow any action
	 */
	protected abstract Command createCommand(final Layer selectedLayer,
			final LayerSupport layerSupport, final IAction action);

	/**
	 * This method informs subclasses when the currently selected layer changes.
	 * 
	 * Subclasses have the opportunity to change the state of the workbench
	 * proxy action accordingly.
	 * 
	 * @param layer
	 *            the currently selected layer
	 * @param layerSupport
	 * 			  the currently used {@link LayerSupport}
	 * @param action
	 *            the workbench proxy action
	 */
	protected abstract void selectedLayerChanged(final Layer layer, final LayerSupport layerSupport,
			final IAction action);

}
