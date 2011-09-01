
package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;

public class FilterView extends AbstractNamsView {

	public static final String ID = "org.csstudio.nams.configurator.filter"; //$NON-NLS-1$

	@Override
	protected Class<? extends IConfigurationBean> getBeanClass() {
		return FilterBean.class;
	}

	@Override
	protected IConfigurationBean[] getTableContent() {
		return AbstractNamsView.getConfigurationBeanService().getFilterBeans();
	}
}
