package org.csstudio.channel.views;

import org.csstudio.channel.widgets.PVTableByPropertyWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;

/**
 * View that allows to create a waterfall plot out of a given PV.
 */
public class PVTableByPropertyView extends AbstractChannelQueryView<PVTableByPropertyWidget> {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.csstudio.channel.views.PVTableByPropertyView";

    @Override
    public void saveWidgetState(PVTableByPropertyWidget widget, IMemento memento) {
        widget.saveState(memento);
    }

    @Override
    public void loadWidgetState(PVTableByPropertyWidget widget, IMemento memento) {
        widget.loadState(memento);
    }

    @Override
    protected PVTableByPropertyWidget createChannelQueryWidget(
            Composite parent, int style) {
        return new PVTableByPropertyWidget(parent, style);
    }
}