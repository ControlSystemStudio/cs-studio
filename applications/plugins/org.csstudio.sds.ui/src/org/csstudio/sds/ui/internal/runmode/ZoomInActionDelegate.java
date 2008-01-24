package org.csstudio.sds.ui.internal.runmode;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * Action delegate for "Zoom In" functionaliy in SDS display views.
 * 
 * @author Sven Wende
 * 
 */
public final class ZoomInActionDelegate implements IViewActionDelegate {

	/**
	 * The real action.
	 */
	private IAction _action;

	/**
	 * {@inheritDoc}
	 */
	public void init(final IViewPart view) {
		if (view instanceof DisplayViewPart) {
			_action = ((DisplayViewPart) view).getZoomInAction();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void run(final IAction action) {
		if (_action != null) {
			_action.run();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void selectionChanged(final IAction action,
			final ISelection selection) {

	}

}
