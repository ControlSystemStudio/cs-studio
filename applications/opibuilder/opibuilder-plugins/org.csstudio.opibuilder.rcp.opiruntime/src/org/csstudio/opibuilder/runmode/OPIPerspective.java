package org.csstudio.opibuilder.runmode;

import org.eclipse.ui.IPageLayout;

/**
 *
 * <code>OPIPerspective</code> is an override of the original OPI Runtime perspective,
 * which does not use the editor area.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class OPIPerspective extends OPIRunnerPerspective {

    @Override
    public void createInitialLayout(IPageLayout layout) {
        super.createInitialLayout(layout);
        layout.setEditorAreaVisible(true);
    }
}
