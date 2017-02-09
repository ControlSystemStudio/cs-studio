package org.csstudio.opibuilder.widgets.detailpanel;

import org.eclipse.gef.editpolicies.SelectionEditPolicy;

public class DetailPanelEditPolicy extends SelectionEditPolicy {

    DetailPanelEditpart editpart;

    public DetailPanelEditPolicy(DetailPanelEditpart editpart) {
        this.editpart = editpart;
    }

    protected DetailPanelFigure getFigure() {
        return (DetailPanelFigure)getHostFigure();
    }

    @Override
    protected void hideSelection() {
        getFigure().deselectAll();
    }

    @Override
    protected void showSelection() {
    }

}
