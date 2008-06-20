package org.csstudio.nams.configurator;


import org.csstudio.nams.configurator.views.AlarmbearbeitergruppenView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.addView(AlarmbearbeitergruppenView.ID, IPageLayout.LEFT, 0.3f, editorArea);
	}

}
