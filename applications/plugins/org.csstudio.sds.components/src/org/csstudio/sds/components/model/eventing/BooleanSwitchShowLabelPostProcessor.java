package org.csstudio.sds.components.model.eventing;

import org.csstudio.sds.components.model.BooleanSwitchModel;
import org.csstudio.sds.eventhandling.AbstractEnsureInvariantsCommand;
import org.csstudio.sds.eventhandling.AbstractWidgetPropertyPostProcessor;
import org.eclipse.gef.commands.Command;

public class BooleanSwitchShowLabelPostProcessor extends
        AbstractWidgetPropertyPostProcessor<BooleanSwitchModel> {

    @Override
    protected Command doCreateCommand(BooleanSwitchModel widget) {
        assert widget != null : "widget != null";
        return new AbstractEnsureInvariantsCommand<BooleanSwitchModel>(widget, BooleanSwitchModel.PROP_LABEL_VISIBLE) {
            @Override
            protected boolean shouldHideProperties(BooleanSwitchModel widget,
                    String propertyId) {
                return !widget.getBooleanProperty(propertyId);
            }

            @Override
            protected String[] getPropertyIds() {
                return new String[] {BooleanSwitchModel.PROP_ON_LABEL, BooleanSwitchModel.PROP_OFF_LABEL};
            }
        };
    }

}
