package org.csstudio.diag.diles;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public static final String ID = "org.csstudio.diag.diles.Perspective";

	private static final String ID_TABS_FOLDER = "org.csstudio.diag.diles.tabs";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		IFolderLayout tabs = layout.createFolder(ID_TABS_FOLDER,
				IPageLayout.BOTTOM, 0.8f, editorArea);
		tabs.addView(IPageLayout.ID_OUTLINE);
		tabs.addView(InOutView.ID);
		tabs.addPlaceholder(IPageLayout.ID_PROP_SHEET);
		System.out.println("test");
	}
}
