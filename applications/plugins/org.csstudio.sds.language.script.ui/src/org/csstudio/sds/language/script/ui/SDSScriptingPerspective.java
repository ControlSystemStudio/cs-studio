package org.csstudio.sds.language.script.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class SDSScriptingPerspective implements IPerspectiveFactory {

    public void createInitialLayout(IPageLayout layout) {
        layout.addView("org.eclipse.ui.views.ResourceNavigator",
                IPageLayout.LEFT, 0.2f, layout.getEditorArea());
        layout.addView("org.eclipse.ui.views.ContentOutline",
                IPageLayout.RIGHT, 0.8f, layout.getEditorArea());
    }

}
