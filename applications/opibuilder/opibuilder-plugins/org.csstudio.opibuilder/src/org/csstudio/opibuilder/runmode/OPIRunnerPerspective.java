/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;

/**The perspective for OPI running environment, which has no views.
 * @author Xihui Chen
 *
 */
public class OPIRunnerPerspective implements IPerspectiveFactory {


    public enum Position {
        LEFT("Left"),
        RIGHT("Right"),
        TOP("Top"),
        BOTTOM("Bottom"),
        DETACHED("Detached"),
        DEFAULT_VIEW("Default View");

        private String description;
        private Position(String description) {
             this.description = description;
        }
        @Override
        public String toString() {
            return description;
        }
        public static String[] stringValues(){
            String[] sv = new String[values().length];
            int i=0;
            for(Position p : values())
                sv[i++] = p.toString();
            return sv;
        }
    }

    private static final String SECOND_ID = ":*"; //$NON-NLS-1$

    // Note that this used to be called "OPIRunner", just like the editor,
    // but in Eclipse 4 that re-used of the same ID for an Editor and a perspective caused trouble
    public final static String ID = "org.csstudio.opibuilder.OPIRuntime.perspective"; //$NON-NLS-1$

    private static final String ID_CONSOLE_VIEW =
        "org.eclipse.ui.console.ConsoleView";//$NON-NLS-1$

    @SuppressWarnings("deprecation")
    final static String ID_NAVIGATOR = IPageLayout.ID_RES_NAV;

    public void createInitialLayout(IPageLayout layout) {

        final String editor = layout.getEditorArea();

        final IPlaceholderFolderLayout left = layout.createPlaceholderFolder(Position.LEFT.name(),
                IPageLayout.LEFT, 0.25f, editor);
        left.addPlaceholder(OPIView.ID + SECOND_ID + Position.LEFT.name());

        final IPlaceholderFolderLayout right = layout.createPlaceholderFolder(Position.RIGHT.name(),
                IPageLayout.RIGHT, 0.75f, editor);
        right.addPlaceholder(OPIView.ID + SECOND_ID + Position.RIGHT.name());

        final IPlaceholderFolderLayout top = layout.createPlaceholderFolder(Position.TOP.name(),
                IPageLayout.TOP, 0.25f, editor);
        top.addPlaceholder(OPIView.ID + SECOND_ID + Position.TOP.name());


        final IPlaceholderFolderLayout bottom = layout.createPlaceholderFolder(Position.BOTTOM.name(),
                IPageLayout.BOTTOM, 0.75f, editor);


        bottom.addPlaceholder(OPIView.ID + SECOND_ID + Position.BOTTOM.name());

        if(!OPIBuilderPlugin.isRAP()){ //$NON-NLS-1$
            bottom.addPlaceholder(ID_CONSOLE_VIEW);
            layout.addShowViewShortcut(ID_CONSOLE_VIEW);
            left.addPlaceholder(ID_NAVIGATOR);
            layout.addShowViewShortcut(ID_NAVIGATOR);
        }
    }



}
