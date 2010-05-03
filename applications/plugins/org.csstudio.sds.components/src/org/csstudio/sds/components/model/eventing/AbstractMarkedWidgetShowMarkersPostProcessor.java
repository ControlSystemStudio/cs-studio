package org.csstudio.sds.components.model.eventing;

import static org.csstudio.sds.components.model.AbstractMarkedWidgetModel.PROP_HIHI_COLOR;
import static org.csstudio.sds.components.model.AbstractMarkedWidgetModel.PROP_HIHI_LEVEL;
import static org.csstudio.sds.components.model.AbstractMarkedWidgetModel.PROP_HI_COLOR;
import static org.csstudio.sds.components.model.AbstractMarkedWidgetModel.PROP_HI_LEVEL;
import static org.csstudio.sds.components.model.AbstractMarkedWidgetModel.PROP_LOLO_COLOR;
import static org.csstudio.sds.components.model.AbstractMarkedWidgetModel.PROP_LOLO_LEVEL;
import static org.csstudio.sds.components.model.AbstractMarkedWidgetModel.PROP_LO_COLOR;
import static org.csstudio.sds.components.model.AbstractMarkedWidgetModel.PROP_LO_LEVEL;
import static org.csstudio.sds.components.model.AbstractMarkedWidgetModel.PROP_SHOW_HI;
import static org.csstudio.sds.components.model.AbstractMarkedWidgetModel.PROP_SHOW_HIHI;
import static org.csstudio.sds.components.model.AbstractMarkedWidgetModel.PROP_SHOW_LO;
import static org.csstudio.sds.components.model.AbstractMarkedWidgetModel.PROP_SHOW_LOLO;
import static org.csstudio.sds.components.model.AbstractMarkedWidgetModel.PROP_SHOW_MARKERS;

import org.csstudio.sds.components.model.AbstractMarkedWidgetModel;
import org.csstudio.sds.eventhandling.AbstractEnsureInvariantsCommand;
import org.csstudio.sds.eventhandling.AbstractWidgetPropertyPostProcessor;
import org.eclipse.gef.commands.Command;

public class AbstractMarkedWidgetShowMarkersPostProcessor extends
        AbstractWidgetPropertyPostProcessor<AbstractMarkedWidgetModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command doCreateCommand(final AbstractMarkedWidgetModel widget) {
        assert widget != null : "widget != null";
        return new AbstractEnsureInvariantsCommand<AbstractMarkedWidgetModel>(widget, PROP_SHOW_MARKERS) {
            @Override
            protected boolean shouldHideProperties(final AbstractMarkedWidgetModel widget,
                    final String propertyId) {
                return !widget.getBooleanProperty(propertyId);
            }

            @Override
            protected String[] getPropertyIds() {
                return new String[] {PROP_HIHI_LEVEL, PROP_SHOW_HIHI,PROP_HIHI_COLOR,
                                     PROP_HI_LEVEL, PROP_SHOW_HI,PROP_HI_COLOR,
                                     PROP_LO_LEVEL, PROP_SHOW_LO,PROP_LO_COLOR,
                                     PROP_LOLO_LEVEL, PROP_SHOW_LOLO,PROP_LOLO_COLOR
                };
            }
        };
    }
}
