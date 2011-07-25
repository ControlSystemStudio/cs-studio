
package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.configurator.Messages;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.filters.NotConditionForFilterTreeBean;
import org.eclipse.jface.viewers.LabelProvider;

public class FilterTreeLabelProvider extends LabelProvider {

	@Override
	public String getText(final Object element) {
		String result = Messages.FilterTreeLabelProvider_unknown_filter_condition;
		if (element instanceof FilterbedingungBean) {
			final FilterbedingungBean bean = (FilterbedingungBean) element;
			result = bean.getDisplayName();
			String description = bean.getDescription();

			if (bean instanceof NotConditionForFilterTreeBean) {
				NotConditionForFilterTreeBean notBean = (NotConditionForFilterTreeBean) bean;
				description = notBean.getFilterbedingungBean().getDescription();
			}

			if ((description != null) && !description.equals("")) { //$NON-NLS-1$
				result += "     [" + description + "]"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		return result;
	}
}
