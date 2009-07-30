package org.csstudio.opibuilder.editor;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/** Perspective for using the OPI Editor
 *  @author Kay Kasemir
 */
public class OPIEditorPerspective implements IPerspectiveFactory
{
    /** ID of navigator view.
     *  This one is deprecated, but don't know what else to use.
     */
    final String ID_NAVIGATOR = IPageLayout.ID_RES_NAV;

    /** Create suggested OPI Editor layout:
     *  <pre>Navigator | Editor | Properties</pre>
     */
    @SuppressWarnings("nls")
    public void createInitialLayout(IPageLayout layout)
    {
        final String editor = layout.getEditorArea();
        
        final IFolderLayout left = layout.createFolder("left",
                IPageLayout.LEFT, 0.2f, editor);
        final IFolderLayout right = layout.createFolder("right",
                IPageLayout.RIGHT, 0.75f, editor);
        final IFolderLayout bottom = layout.createFolder("bottom",
                IPageLayout.BOTTOM, 0.75f, editor);

        // Stuff for 'left'
        left.addView(ID_NAVIGATOR);
        left.addPlaceholder(IPageLayout.ID_RES_NAV);
        
        // Stuff for 'right'
        right.addView(IPageLayout.ID_PROP_SHEET);
             
        
        //Stuff for 'bottom'
        bottom.addView(IPageLayout.ID_OUTLINE);
        bottom.addPlaceholder(IPageLayout.ID_PROGRESS_VIEW);
        
        // Populate the "Window/Views..." menu with suggested views
        layout.addShowViewShortcut(ID_NAVIGATOR);
        layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
    }
}
