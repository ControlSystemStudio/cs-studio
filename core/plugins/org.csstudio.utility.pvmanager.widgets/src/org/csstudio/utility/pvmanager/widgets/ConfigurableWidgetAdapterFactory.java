/**
 * 
 */
package org.csstudio.utility.pvmanager.widgets;

import org.eclipse.core.runtime.IAdapterFactory;

/**
 * Adapter factor for the ConfigurableWidgets.
 * 
 * @author shroffk
 * 
 */
public class ConfigurableWidgetAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
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
	public Class<?>[] getAdapterList() {
		return new Class[] { ConfigurableWidget.class };
	}

}
