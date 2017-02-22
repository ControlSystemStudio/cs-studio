package org.csstudio.opibuilder.widgets.detailpanel;

import org.csstudio.opibuilder.actions.AbstractWidgetTargetAction;
import org.eclipse.jface.action.IAction;

public class DetailPanelOutdentAction extends AbstractWidgetTargetAction {

    @Override
    public void run(IAction action) {
        DetailPanelEditpart editpart = (DetailPanelEditpart)selection.getFirstElement();
        execute(editpart.outdentSelectedRows());
    }
}
