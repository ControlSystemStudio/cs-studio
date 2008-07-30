package org.csstudio.nams.configurator.views;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.composite.FilterableBeanList;
import org.csstudio.nams.configurator.dnd.SelectionDragSourceListener;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;

public class FilterbedingungView extends AbstractNamsView {

	public static final String ID = "org.csstudio.nams.configurator.filterbedingung";

	@Override
	protected IConfigurationBean[] getTableContent() {
		FilterbedingungBean[] conditionsBeans = getConfigurationBeanService().getFilterConditionsBeans();
		List<FilterbedingungBean> result = new LinkedList<FilterbedingungBean>();
		for (FilterbedingungBean filterbedingungBean : conditionsBeans) {
			// TODO (gs) nur zum testen auskommentiert
//			if (!(filterbedingungBean instanceof JunctorConditionForFilterTreeBean) &&
//					!(filterbedingungBean instanceof NotConditionForFilterTreeBean)) {
				result.add(filterbedingungBean);
//			}
		}
		return result.toArray(new FilterbedingungBean[result.size()]);
	}

	@Override
	protected void initDragAndDrop(final FilterableBeanList filterableBeanList) {
		filterableBeanList.getTable().addDragSupport(DND.DROP_LINK,
				new Transfer[] { LocalSelectionTransfer.getTransfer() },
				new SelectionDragSourceListener(filterableBeanList.getTable()));
	}
	
	@Override
	protected Class<? extends IConfigurationBean> getBeanClass() {
		return FilterbedingungBean.class;
	}
}
