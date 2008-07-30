package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.eclipse.jface.viewers.LabelProvider;

public class FilterTreeLabelProvider extends LabelProvider {

	@Override
	public String getText(final Object element) {
		String result = "unknown";
		if (element instanceof FilterbedingungBean) {
			final FilterbedingungBean bean = (FilterbedingungBean) element;
			result = bean.getDisplayName();
			final String description = bean.getDescription();
			if ((description != null) && !description.equals("")) {
				result += "     [" + description + "]";
			}
		}

		return result;
	}
}
