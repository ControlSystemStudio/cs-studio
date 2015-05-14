package org.csstudio.channel.views;

import org.csstudio.channel.widgets.WaterfallWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;

/**
 * View that allows to create a waterfall plot out of a given PV.
 */
public class WaterfallView extends AbstractChannelQueryView<WaterfallWidget> {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.csstudio.channel.views.WaterfallView";

    @Override
    public void saveWidgetState(WaterfallWidget widget, IMemento memento) {
        widget.saveState(memento);
    }

    @Override
    public void loadWidgetState(WaterfallWidget widget, IMemento memento) {
        widget.loadState(memento);
    }

    @Override
    protected WaterfallWidget createChannelQueryWidget(Composite parent,
            int style) {
        return new WaterfallWidget(parent, style);
    }

}