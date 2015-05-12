package org.csstudio.sds.components.model.eventing;

import org.csstudio.sds.components.model.ActionButtonModel;
import org.csstudio.sds.eventhandling.AbstractEnsureInvariantsCommand;
import org.csstudio.sds.eventhandling.AbstractWidgetPropertyPostProcessor;
import org.csstudio.sds.model.ActionData;
import org.eclipse.gef.commands.Command;

/**
 *
 * @author Kai Meyer (C1 WPS)
 *
 */
public class ActionButtonDataPostProcessor extends
        AbstractWidgetPropertyPostProcessor<ActionButtonModel> {

    @Override
    protected Command doCreateCommand(ActionButtonModel widget) {
        assert widget != null : "widget != null";

        return new AbstractEnsureInvariantsCommand<ActionButtonModel>(widget, ActionButtonModel.PROP_ACTIONDATA) {

            @Override
            protected boolean shouldHideProperties(ActionButtonModel widget,
                    String propertyId) {
                ActionData data = widget.getActionDataProperty(propertyId);
                return data.getWidgetActions().isEmpty();
            }

            @Override
            protected String[] getPropertyIds() {
                return new String[] {
                        ActionButtonModel.PROP_ACTION_PRESSED_INDEX,
                        ActionButtonModel.PROP_ACTION_RELEASED_INDEX };
            }

        };

    }

}
