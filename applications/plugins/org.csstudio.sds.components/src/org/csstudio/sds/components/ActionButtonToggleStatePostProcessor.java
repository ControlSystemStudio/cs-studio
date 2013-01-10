package org.csstudio.sds.components;

import org.csstudio.sds.components.model.ActionButtonModel;
import org.csstudio.sds.eventhandling.AbstractEnsureInvariantsCommand;
import org.csstudio.sds.eventhandling.AbstractWidgetPropertyPostProcessor;
import org.eclipse.gef.commands.Command;

public class ActionButtonToggleStatePostProcessor extends
        AbstractWidgetPropertyPostProcessor<ActionButtonModel> {
    
    @Override
    protected Command doCreateCommand(ActionButtonModel widget) {
        assert widget != null : "widget != null";
        
        return new AbstractEnsureInvariantsCommand<ActionButtonModel>(widget,
                ActionButtonModel.PROP_ACTIONDATA) {
            
            @Override
            protected boolean shouldHideProperties(ActionButtonModel widget, String propertyId) {
                return !widget.isToggleButton();
            }
            
            @Override
            protected String[] getPropertyIds() {
                return new String[] { ActionButtonModel.PROP_TOGGLE_STATE };
            }
            
        };
    }
    
}
