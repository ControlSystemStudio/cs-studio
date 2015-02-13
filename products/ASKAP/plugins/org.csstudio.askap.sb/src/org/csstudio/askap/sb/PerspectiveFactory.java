package org.csstudio.askap.sb;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

public class PerspectiveFactory implements IPerspectiveFactory {

	public static final String ID = "org.csstudio.askap.sb.Perspective";
	
    public static void showPerspective() throws WorkbenchException {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        workbench.showPerspective(ID, window);
    }

	@Override
	public void createInitialLayout(IPageLayout layout) {
        String editor = layout.getEditorArea();
        IFolderLayout left = layout.createFolder("left",
                        IPageLayout.LEFT, 0.25f, editor);

        left.addView(ExecutiveSummaryView.ID);
        
        IFolderLayout bottom = layout.createFolder("bottom",
                IPageLayout.BOTTOM, 0.75f, editor);

        bottom.addView(ExecutiveLogView.ID); 
        
        SBExecutionView.openSBExecutionView();
	}

}
