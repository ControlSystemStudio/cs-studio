/**
 * 
 */
package org.csstudio.ui.util;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;

/**
 * @author shroffk
 * 
 */
public class ConfigureHandler extends
	AbstractAdaptedHandler<ConfigurableWidget> {

    public ConfigureHandler() {
	super(ConfigurableWidget.class);
    }

    @Override
    protected void execute(List<ConfigurableWidget> data, ExecutionEvent event) {
	if (!data.isEmpty()) {
	    data.get(0).openConfigurationDialog();
	}
    }

}
