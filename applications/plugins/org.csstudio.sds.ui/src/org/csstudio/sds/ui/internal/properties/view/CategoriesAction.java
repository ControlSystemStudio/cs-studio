package org.csstudio.sds.ui.internal.properties.view;

import org.eclipse.ui.PlatformUI;

/**
 *  This action hides or shows categories in the <code>PropertySheetViewer</code>.
 *  
 * @author Sven Wende
 */
final class CategoriesAction extends PropertySheetAction {
    /**
     * Creates the Categories action. This action is used to show
     * or hide categories properties.
     * @param viewer the viewer
     * @param name the name
     */
    public CategoriesAction(final PropertySheetViewer viewer, final String name) {
        super(viewer, name);
        PlatformUI.getWorkbench().getHelpSystem()
                .setHelp(this, IPropertiesHelpContextIds.CATEGORIES_ACTION);
    }

    /**
     * Toggles the display of categories for the properties.
     */
    @Override
	public void run() {
        PropertySheetViewer ps = getPropertySheet();
        ps.deactivateCellEditor();
        if (isChecked()) {
            ps.showCategories();
        } else {
            ps.hideCategories();
        }
    }
}

