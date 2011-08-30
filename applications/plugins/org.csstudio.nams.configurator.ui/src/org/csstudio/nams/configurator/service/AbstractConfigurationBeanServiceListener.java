
package org.csstudio.nams.configurator.service;

import org.csstudio.nams.configurator.beans.IConfigurationBean;

public abstract class AbstractConfigurationBeanServiceListener implements
		ConfigurationBeanServiceListener {

	@Override
    public void onBeanDeleted(final IConfigurationBean bean) {
	    // Not used yet
	}

	@Override
    public void onBeanInsert(final IConfigurationBean bean) {
	    // Not used yet
	}

	@Override
    public void onBeanUpdate(final IConfigurationBean bean) {
	    // Not used yet
	}
}
