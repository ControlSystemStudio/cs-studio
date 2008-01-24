package org.csstudio.sds.ui.internal.properties.view;

import org.eclipse.jface.action.Action;

/**
 * This is the base class of all the local actions used in the PropertySheet.
 * 
 * @author Sven Wende
 */
abstract class PropertySheetAction extends Action implements IWidgetSelectionListener {
	/**
	 * A property sheet viewer.
	 */
	private PropertySheetViewer _viewer;

	/**
	 * Create a PropertySheetViewer action.
	 * 
	 * @param viewer
	 *            a property sheet viewer
	 * @param id
	 *            the action id
	 */
	protected PropertySheetAction(final PropertySheetViewer viewer,
			final String id) {
		super(id);
		_viewer = viewer;
		_viewer.addWidgetSelectionListener(this);
		setId(id);
	}

	/**
	 * @return Returns the PropertySheetViewer.
	 */
	public PropertySheetViewer getPropertySheet() {
		return _viewer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void handleWidgetSelection(final Object[] selectedObjects) {
		this.setEnabled((selectedObjects!=null && selectedObjects.length>0));
	}

}
