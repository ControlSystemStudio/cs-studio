package org.csstudio.shift.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


public class ShiftViewerPerspective implements IPerspectiveFactory {

    public static final String ID = "org.csstudio.shift.ui.ShiftViewerPerspective";

    @Override
    public void createInitialLayout(final IPageLayout layout) {

        final String editor = layout.getEditorArea();
        final IFolderLayout left = layout.createFolder("Left", IPageLayout.LEFT, 0.33f, editor);
        left.addView(org.csstudio.shift.ui.ShiftTableView.ID);
    }
}
