package org.csstudio.channel.opiwidgets;

import java.util.List;

import org.csstudio.channel.widgets.ChannelTreeByPropertyWidget;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetModelDescription;

public class ChannelTreeByPropertyModel extends AbstractChannelWidgetWithNotificationModel {

    public ChannelTreeByPropertyModel() {
        super(AbstractSelectionWidgetModelDescription.newModelFrom(ChannelTreeByPropertyWidget.class));
    }

    public final String ID = "org.csstudio.channel.opiwidgets.ChannelTreeByProperty"; //$NON-NLS-1$

    public static final String TREE_PROPERTIES = "tree_properties"; //$NON-NLS-1$
    public static final String SHOW_CHANNEL_NAMES = "show_channel_names"; //$NON-NLS-1$

    @Override
    protected void configureProperties() {
        addProperty(new StringProperty(TREE_PROPERTIES, "Tree Properties", WidgetPropertyCategory.Basic, ""));
        addProperty(new BooleanProperty(SHOW_CHANNEL_NAMES, "Show Channel Names", WidgetPropertyCategory.Basic, true));
    }

    @Override
    public String getTypeID() {
        return ID;
    }

    public List<String> getTreeProperties() {
        return getListProperty(TREE_PROPERTIES);
    }

    @Override
    protected String defaultSelectionExpression() {
        if (isShowChannelNames()) {
            return "#(Channel Name)";
        } else {
            return "#(" + getTreeProperties().get(getTreeProperties().size() - 1) + ")";
        }
    }

    public boolean isShowChannelNames() {
        return getCastedPropertyValue(SHOW_CHANNEL_NAMES);
    }

}
