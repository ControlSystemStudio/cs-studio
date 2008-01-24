package org.csstudio.sds.ui.internal.workbench;

import org.csstudio.sds.ui.internal.connection.ConnectionStateView;
import org.csstudio.sds.ui.internal.runmode.DisplayViewPart;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Perspective for the <b>Synoptic Display Studio</b>.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class DisplayTestingPerspective implements IPerspectiveFactory {
	/**
	 * The perspective's ID.
	 */
	public static final String ID = "org.csstudio.sds.ui.internal.workbench.DisplayTestingPerspective"; //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	public void createInitialLayout(final IPageLayout layout) {
		layout.setEditorAreaVisible(false);

		layout.addView("org.eclipse.ui.views.ResourceNavigator", //$NON-NLS-1$
				IPageLayout.LEFT, 0.2f, IPageLayout.ID_EDITOR_AREA);

		layout.addView(ConnectionStateView.VIEW_ID, IPageLayout.BOTTOM, 0.8f,
				"org.eclipse.ui.views.ResourceNavigator"); //$NON-NLS-1$

		IFolderLayout folder = layout.createFolder("displays",
				IPageLayout.RIGHT, 0.25f,
				"org.eclipse.ui.views.ResourceNavigator");

		for (int i = 0; i < 100; i++) {
			folder.addPlaceholder(DisplayViewPart.PRIMARY_ID + ":" + i);
		}
	}
}
