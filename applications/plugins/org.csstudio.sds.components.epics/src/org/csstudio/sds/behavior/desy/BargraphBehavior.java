package org.csstudio.sds.behavior.desy;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;

/**
 * Default DESY-Behaviour for the {@link BargraphModel} widget.
 * 
 * @author Sven Wende
 * 
 */
public class BargraphBehavior extends AbstractDesyBehavior<BargraphModel> {
    private Map<ConnectionState, Boolean> transparencyByConnectionState;

    public BargraphBehavior() {
        transparencyByConnectionState = new HashMap<ConnectionState, Boolean>();
        transparencyByConnectionState.put(ConnectionState.CONNECTED, true);
        transparencyByConnectionState.put(ConnectionState.CONNECTION_LOST, false);
        transparencyByConnectionState.put(ConnectionState.INITIAL, false);
    }

    @Override
    protected String[] doGetInvisiblePropertyIds() {
        return new String[] { BargraphModel.PROP_MIN, BargraphModel.PROP_MAX,
                BargraphModel.PROP_HIHI_LEVEL, BargraphModel.PROP_HI_LEVEL,
                BargraphModel.PROP_LOLO_LEVEL, BargraphModel.PROP_LO_LEVEL,
                BargraphModel.PROP_DEFAULT_FILL_COLOR, BargraphModel.PROP_FILLBACKGROUND_COLOR,
                BargraphModel.PROP_FILL, BargraphModel.PROP_TRANSPARENT,
                BargraphModel.PROP_ACTIONDATA, BargraphModel.PROP_PERMISSSION_ID,
                BargraphModel.PROP_BORDER_STYLE};

    }

    @Override
    protected void doInitialize(BargraphModel widget) {
        // .. border
        widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE,
                determineBorderStyle(ConnectionState.INITIAL));
        widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH,
                determineBorderWidth(ConnectionState.INITIAL));
        widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR,
                determineBorderColor(ConnectionState.INITIAL));
    }

    @Override
    protected void doProcessValueChange(BargraphModel widget, AnyData anyData) {
        // .. fill level (influenced by current value)
        widget.setPropertyValue(BargraphModel.PROP_FILL, anyData.doubleValue());

        // .. fill color (influenced by severity)
        widget.setPropertyValue(BargraphModel.PROP_DEFAULT_FILL_COLOR,
                determineColorBySeverity(anyData.getSeverity()));
    }

    @Override
    protected void doProcessConnectionStateChange(BargraphModel widget,
            org.epics.css.dal.context.ConnectionState connectionState) {
        // .. border
        widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE,
                determineBorderStyle(connectionState));
        widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH,
                determineBorderWidth(connectionState));
        widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR,
                determineBorderColor(connectionState));

        // .. background colors
        widget.setPropertyValue(BargraphModel.PROP_FILLBACKGROUND_COLOR,
                determineBackgroundColor(connectionState));
        widget.setPropertyValue(BargraphModel.PROP_COLOR_BACKGROUND,
                determineBackgroundColor(connectionState));

        // .. transparency
        Boolean transparent = transparencyByConnectionState.get(connectionState);

        if (transparent != null) {
            widget.setPropertyValue(BargraphModel.PROP_TRANSPARENT, transparent);
        }
    }

    @Override
    protected void doProcessMetaDataChange(BargraphModel widget, MetaData meta) {
        if (meta != null) {
            // .. limits
            widget.setPropertyValue(BargraphModel.PROP_MIN, meta.getDisplayLow());
            widget.setPropertyValue(BargraphModel.PROP_MAX, meta.getDisplayHigh());
            widget.setPropertyValue(BargraphModel.PROP_HIHI_LEVEL, meta.getAlarmHigh());
            widget.setPropertyValue(BargraphModel.PROP_HI_LEVEL, meta.getWarnHigh());
            widget.setPropertyValue(BargraphModel.PROP_LOLO_LEVEL, meta.getAlarmLow());
            widget.setPropertyValue(BargraphModel.PROP_LO_LEVEL, meta.getWarnLow());
        }
    }

}
