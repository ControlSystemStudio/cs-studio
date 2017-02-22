package org.csstudio.opibuilder.widgets.detailpanel;

import org.csstudio.opibuilder.actions.AbstractWidgetTargetAction;
import org.csstudio.opibuilder.commands.SetWidgetPropertyCommand;
import org.eclipse.jface.action.IAction;

public class DetailPanelDisplayLevelHighAction extends AbstractWidgetTargetAction {

    public void run(IAction action) {
        DetailPanelEditpart editpart = (DetailPanelEditpart)selection.getFirstElement();
        execute(new SetWidgetPropertyCommand(editpart.getWidgetModel(),
                        DetailPanelModel.PROP_DISPLAY_LEVEL, DetailPanelModel.DisplayLevel.HIGH.ordinal()));
    }
}
