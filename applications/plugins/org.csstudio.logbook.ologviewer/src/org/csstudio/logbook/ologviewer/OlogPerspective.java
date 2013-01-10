package org.csstudio.logbook.ologviewer;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

public class OlogPerspective implements IPerspectiveFactory {

	final public static String ID = "org.csstudio.logbook.ologviewer.ologperspective";

	public static void showPerspective() throws WorkbenchException {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		workbench.showPerspective(OlogPerspective.ID, window);
	}

	@Override
	public void createInitialLayout(IPageLayout layout) {
		defineActions(layout);
		defineLayout(layout);
	}

	public void defineActions(IPageLayout layout) {
		// Add "new wizards".
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");

		// Add "show views".
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
	}

	public void defineLayout(IPageLayout layout) {
		// Editors are placed for free.
		String editorArea = layout.getEditorArea();

		// Place navigator and outline to left of
		// editor area.
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				(float) 0.26, editorArea);
		left.addView(IPageLayout.ID_RES_NAV);
		left.addView(IPageLayout.ID_OUTLINE);
		left.addView(IPageLayout.ID_PROJECT_EXPLORER);
	}

}
