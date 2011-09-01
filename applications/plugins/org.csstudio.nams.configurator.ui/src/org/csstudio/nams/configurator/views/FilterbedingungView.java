
package org.csstudio.nams.configurator.views;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionForFilterTreeBean;
import org.csstudio.nams.configurator.beans.filters.NotConditionForFilterTreeBean;
import org.csstudio.nams.configurator.composite.FilterableBeanList;
import org.csstudio.nams.configurator.dnd.SelectionDragSourceListener;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;

public class FilterbedingungView extends AbstractNamsView {

	public static final String ID = "org.csstudio.nams.configurator.filterbedingung"; //$NON-NLS-1$

	@Override
	protected Class<? extends IConfigurationBean> getBeanClass() {
		return FilterbedingungBean.class;
	}

	@Override
	protected IConfigurationBean[] getTableContent() {
		final FilterbedingungBean[] conditionsBeans = AbstractNamsView
				.getConfigurationBeanService().getFilterConditionsBeans();
		final List<FilterbedingungBean> result = new LinkedList<FilterbedingungBean>();
		for (final FilterbedingungBean filterbedingungBean : conditionsBeans) {
			// (gs) filtern der AND, OR, NOT Bedingungen
			if (!(filterbedingungBean instanceof JunctorConditionForFilterTreeBean)
					&& !(filterbedingungBean instanceof NotConditionForFilterTreeBean)) {
				result.add(filterbedingungBean);
			}
		}
		return result.toArray(new FilterbedingungBean[result.size()]);
	}

	@Override
	protected void initDragAndDrop(final FilterableBeanList filterableBeanList) {
		filterableBeanList.getTable().addDragSupport(DND.DROP_LINK,
				new Transfer[] { LocalSelectionTransfer.getTransfer() },
				new SelectionDragSourceListener(filterableBeanList.getTable()));
	}
}
