package org.csstudio.webopi.perspectives;

import org.csstudio.opibuilder.runmode.OPIView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Configures the perspective layout. This class is contributed through the
 * plugin.xml.
 */
public class StandaloneRuntimePerspective implements IPerspectiveFactory {

	public final static String ID = "org.csstudio.webopi.standaloneRuntime"; //$NON-NLS-1$
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
//		final IPlaceholderFolderLayout center = layout.createPlaceholderFolder(
//				"center",
//                IPageLayout.LEFT, 1.0f, editorArea);
//
//		center.addPlaceholder(OPIView.ID);
		OPIView.setOpenFromPerspective(true);
		layout.addStandaloneView(OPIView.ID, false, IPageLayout.LEFT, 1.0f,
				editorArea);
	}
}
