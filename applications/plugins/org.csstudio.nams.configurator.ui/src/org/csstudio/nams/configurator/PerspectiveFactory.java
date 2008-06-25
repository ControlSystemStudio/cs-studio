package org.csstudio.nams.configurator;


import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
//		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
//		IFolderLayout folder = layout.createFolder("amsview.folder", IPageLayout.LEFT, 0.4f, editorArea);
//		folder.addView(AlarmbearbeiterView.ID);
//		folder.addView(AlarmbearbeitergruppenView.ID);
//		folder.addView(AlarmtopicView.ID);
//		folder.addView(FilterbedingungView.ID);
//		folder.addView(FilterView.ID);
	}

}
