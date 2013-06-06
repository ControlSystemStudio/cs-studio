/**
 * 
 */
package org.csstudio.ui.util;

import java.util.Collection;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * Adapter factor for the common adaptables. This will adapt a selection of
 * {@link ConfigurableWidgetAdaptable}, {@link ProcessVariableAdaptable},
 * {@link 2DPlotAdaptable} to the appropriate objects and arrays so that the
 * logic does not have to be replicated over and over.
 * 
 * @author shroffk
 * 
 */
public class CommonAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
	if (adaptableObject instanceof ProcessVariableAdaptable) {
	    ProcessVariableAdaptable processVariableAdaptable = (ProcessVariableAdaptable) adaptableObject;
	    Collection<ProcessVariable> pvs = processVariableAdaptable
		    .toProcessVariables();
	    if (adapterType == ProcessVariable.class) {
		if (pvs != null && pvs.size() == 1)
		    return pvs.iterator().next();
	    } else if (adapterType == ProcessVariable[].class) {
		if (pvs != null && !pvs.isEmpty())
		    return pvs.toArray(new ProcessVariable[pvs.size()]);
	    }
	}
	if (adaptableObject instanceof ConfigurableWidgetAdaptable) {
	    if (adapterType == ConfigurableWidget.class) {
		ConfigurableWidgetAdaptable configurableWidgetAdaptable = (ConfigurableWidgetAdaptable) adaptableObject;
		if (configurableWidgetAdaptable != null
			&& configurableWidgetAdaptable.toConfigurableWidget() != null
			&& configurableWidgetAdaptable.toConfigurableWidget()
				.isConfigurable())
		    return configurableWidgetAdaptable.toConfigurableWidget();
		else
		    return null;
	    }

	}
	return null;
    }

    @Override
    public Class[] getAdapterList() {
	return new Class[] { ProcessVariable.class, ProcessVariable[].class,
		ConfigurableWidget.class };
    }

}
