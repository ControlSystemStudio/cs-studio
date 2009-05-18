package de.desy.language.snl.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
		layout.addView(IPageLayout.ID_RES_NAV, IPageLayout.LEFT, 0.30f, IPageLayout.ID_EDITOR_AREA);
		layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, 0.75f, IPageLayout.ID_EDITOR_AREA);
	}

}
