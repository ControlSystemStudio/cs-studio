package org.csstudio.dct.ui.workbenchintegration;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * The DCT perspective.
 *
 * @author Sven Wende
 *
 */
public final class DctPerspective implements IPerspectiveFactory {

    /**
     *{@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    public void createInitialLayout(IPageLayout layout) {
        layout.setEditorAreaVisible(true);

        // TODO 2010-04-06 jp: Switch to project explorer
        // (IPageLayout.ID_PROJECT_EXPLORER) when it becomes mature
        layout.addView(IPageLayout.ID_RES_NAV, IPageLayout.LEFT, 0.2f, IPageLayout.ID_EDITOR_AREA);

        layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, 0.7f, IPageLayout.ID_EDITOR_AREA);

        layout.addView("org.csstudio.dct.ui.HierarchyView", //$NON-NLS-1$
                       IPageLayout.BOTTOM,
                       0.6f,
                       IPageLayout.ID_OUTLINE);

        layout.addView(IPageLayout.ID_PROBLEM_VIEW,
                       IPageLayout.BOTTOM,
                       0.75f,
                       IPageLayout.ID_EDITOR_AREA);

    }

}
