package org.csstudio.opibuilder.adl2boy;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {
	private static final String ID_LEFT = "leftFolder";
	private static final String ID_RIGHT = "rightFolder";
	private static final String ID_BOTTOM = "bottomFolder";
	private static final String ID_TOP = "topFolder";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(true);
		
		IFolderLayout folder_left = layout.createFolder(ID_LEFT,
				IPageLayout.LEFT, .3f, editorArea);
        final IFolderLayout folder_bottom = layout.createFolder(ID_BOTTOM,
                IPageLayout.BOTTOM, 0.7f, editorArea);
        final IFolderLayout folder_right = layout.createFolder(ID_RIGHT,
                IPageLayout.RIGHT, 0.7f, editorArea);
//        final IFolderLayout folder_top = layout.createFolder(ID_TOP,
//                IPageLayout.TOP, 0.5f, editorArea);
		folder_left.addView("org.eclipse.ui.views.ResourceNavigator");



		folder_bottom.addView("org.eclipse.ui.console.ConsoleView");
		folder_right.addView(IPageLayout.ID_PROP_SHEET);
	}

}
