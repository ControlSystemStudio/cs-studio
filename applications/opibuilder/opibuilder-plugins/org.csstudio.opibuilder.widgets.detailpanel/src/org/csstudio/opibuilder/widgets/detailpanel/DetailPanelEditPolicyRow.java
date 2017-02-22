package org.csstudio.opibuilder.widgets.detailpanel;

import org.csstudio.opibuilder.commands.SetWidgetPropertyCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

// This class implements the DIRECT_EDIT_ROLE edit policy that is registered
// by the edit part.  The override is called on completion of the edit
// to create a command to make the requested change.  We just make a
// standard property change command.

public class DetailPanelEditPolicyRow extends DirectEditPolicy {

    @Override
    protected Command getDirectEditCommand(DirectEditRequest edit) {
        Command result = null;
        DetailPanelEditpart editpart = (DetailPanelEditpart)getHost();
        int rowNumber = editpart.getFirstSelectedRow();
        if(rowNumber >= 0) {
            result = new SetWidgetPropertyCommand(editpart.getWidgetModel(),
                    DetailPanelModelRow.makePropertyName(DetailPanelModelRow.PROP_ROW_NAME, rowNumber),
                    (String)edit.getCellEditor().getValue());
        }
        return result;
    }

    @Override
    protected void showCurrentEditValue(DirectEditRequest edit) {
    }

}
