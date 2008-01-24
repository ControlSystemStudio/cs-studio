package org.csstudio.sds.ui.internal.workbench;

import org.csstudio.sds.ui.internal.properties.view.PropertySheet;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Perspective for the <b>Synoptic Display Studio</b>.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class DisplayDevelopmentPerspective implements
		IPerspectiveFactory {
	/**
	 * The perspective's ID.
	 */
	public static final String ID = "org.csstudio.sds.ui.internal.workbench.DisplayDevelopmentPerspective"; //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	public void createInitialLayout(final IPageLayout layout) {
		layout.setEditorAreaVisible(true);
		layout.addView("org.eclipse.ui.views.ResourceNavigator", IPageLayout.LEFT, 0.2f, //$NON-NLS-1$
				IPageLayout.ID_EDITOR_AREA);
		layout.addView(PropertySheet.VIEW_ID, IPageLayout.RIGHT, 0.8f,
				IPageLayout.ID_EDITOR_AREA);
	}
}
