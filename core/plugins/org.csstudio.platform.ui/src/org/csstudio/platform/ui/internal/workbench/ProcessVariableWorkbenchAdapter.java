package org.csstudio.platform.ui.internal.workbench;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.CSSPlatformUiPlugin;
import org.csstudio.platform.ui.util.ImageUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * A workbench adapter implementation for process variables.
 * 
 * @author swende
 * 
 */
public final class ProcessVariableWorkbenchAdapter extends WorkbenchAdapter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImageDescriptor getImageDescriptor(final Object object) {
		return ImageUtil.getInstance().getImageDescriptor(CSSPlatformUiPlugin.ID, "icons/pv.gif"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel(final Object object) {
		String label = "<unknown>"; //$NON-NLS-1$

		if (object instanceof IProcessVariable) {
			label = ((IProcessVariable) object).getName();
		}
		return label;
	}
}
