package org.csstudio.multichannelviewer;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory implements IPerspectiveFactory {

	/**
     * Plugin id.
     */
    public final static String PERSPECTIVE_ID = "org.csstudio.multiChannelViewer.perspective";
    
    
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editor = layout.getEditorArea();
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				0.25f, editor);
		IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.66f, editor);

		// Left
		left.addView(IPageLayout.ID_PROJECT_EXPLORER);

		// Bottom
		bottom.addView(ChannelsListView.ID);
//		bottom.addPlaceholder(ChannelFinderView.ID);;
		bottom.addPlaceholder(IPageLayout.ID_PROGRESS_VIEW);
		bottom.addPlaceholder(IPageLayout.ID_PROBLEM_VIEW);

		// Populate the "Window/Views..." menu with suggested views
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(IPageLayout.ID_PROGRESS_VIEW);
	}

}
