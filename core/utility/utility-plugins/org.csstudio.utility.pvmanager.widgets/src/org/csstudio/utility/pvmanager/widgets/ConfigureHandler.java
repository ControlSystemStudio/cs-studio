/**
 *
 */
package org.csstudio.utility.pvmanager.widgets;

import java.util.List;

import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;

/**
 *
 * A command that opens up the selected configurable widgets configuration
 * panel.
 *
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
