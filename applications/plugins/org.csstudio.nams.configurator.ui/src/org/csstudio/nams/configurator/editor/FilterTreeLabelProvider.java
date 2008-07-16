package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.eclipse.jface.viewers.LabelProvider;

public class FilterTreeLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		String result = "unknown";
		if (element instanceof IConfigurationBean) {
			result = ((IConfigurationBean) element).getDisplayName();
		}
		
		return result;
	}
}
