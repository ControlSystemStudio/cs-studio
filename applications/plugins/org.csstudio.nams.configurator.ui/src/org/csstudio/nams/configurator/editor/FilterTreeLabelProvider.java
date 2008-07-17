package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.eclipse.jface.viewers.LabelProvider;

public class FilterTreeLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		String result = "unknown";
		if (element instanceof FilterbedingungBean) {
			FilterbedingungBean bean = (FilterbedingungBean) element;
			result = bean.getDisplayName();
			String description = bean.getDescription();
			if (description != null && !description.equals("")) {
				result += "     [" + description + "]";
			}
		}
		
		return result;
	}
}
