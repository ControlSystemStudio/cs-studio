package org.csstudio.channel.views;

import org.csstudio.channel.widgets.ChannelTreeByPropertyWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;

/**
 * View that allows to create a tree view out of the results of a channel query.
 */
public class ChannelTreeByPropertyView extends AbstractChannelQueryView<ChannelTreeByPropertyWidget> {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.csstudio.channel.views.ChannelTreeByPropertyView";

    @Override
    public void saveWidgetState(ChannelTreeByPropertyWidget widget,
            IMemento memento) {
        widget.saveState(memento);
    }

    @Override
    public void loadWidgetState(ChannelTreeByPropertyWidget widget,
            IMemento memento) {
        widget.loadState(memento);
    }

    @Override
    protected ChannelTreeByPropertyWidget createChannelQueryWidget(
            Composite parent, int style) {
        return new ChannelTreeByPropertyWidget(parent, style);
    }

}