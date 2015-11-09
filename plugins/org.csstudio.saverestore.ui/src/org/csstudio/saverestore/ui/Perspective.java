package org.csstudio.saverestore.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * <code>Perspective</code> defines the save and restore perspective.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Perspective implements IPerspectiveFactory {

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
     */
    @SuppressWarnings("deprecation")
    @Override
    public void createInitialLayout(IPageLayout layout) {
        IFolderLayout folder = layout.createFolder("left", IPageLayout.LEFT, 0.25f, layout.getEditorArea());
        folder.addPlaceholder(IPageLayout.ID_RES_NAV);
    }
}
