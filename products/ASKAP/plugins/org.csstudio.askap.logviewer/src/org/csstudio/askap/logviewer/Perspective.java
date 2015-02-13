package org.csstudio.askap.logviewer;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;


public class Perspective implements IPerspectiveFactory {
	
    final public static String ID = "org.csstudio.askap.logviewer.Perspective";
    
    public static void showPerspective() throws WorkbenchException {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        workbench.showPerspective(Perspective.ID, window);
    }

    
	@Override
	public void createInitialLayout(IPageLayout layout) {
        String editor = layout.getEditorArea();
        IFolderLayout left = layout.createFolder("left",
                        IPageLayout.LEFT, 0.25f, editor);

        left.addView(LogQuery.ID);
	}

}
