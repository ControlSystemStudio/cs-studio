
package org.csstudio.nams.configurator;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory implements IPerspectiveFactory {

	@Override
    public void createInitialLayout(final IPageLayout layout) {
		layout.setEditorAreaVisible(true);
	}
}
