package org.csstudio.sds.components.epics;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.sds.cosyrules.color.Alarm;
import org.csstudio.sds.cosyrules.color.AlarmBorder;
import org.csstudio.sds.cosyrules.color.AlarmBorderWidth;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.initializers.AbstractWidgetModelInitializer;
import org.csstudio.sds.util.ColorAndFontUtil;

/**
 * Common EPICS initializer for (dynamic) properties of all widgets.
 *
 * @author jhatje
 *
 */
public abstract class AbstractEpicsWidgetInitializer extends AbstractWidgetModelInitializer {

//    public void initializeCommonStaticProperties() {
//        initializeStaticProperty(AbstractWidgetModel.PROP_COLOR_BACKGROUND, new RGB(255,
//                255, 255));
//        initializeStaticProperty(AbstractWidgetModel.PROP_BORDER_STYLE, 0);
//        initializeStaticProperty(AbstractWidgetModel.PROP_BORDER_WIDTH, 3);
//    }

    public void initializeCommonDynamicProperties() {

    }

    public void initializeCommonAlarmBehaviour() {
//        initializeStaticProperty(AbstractWidgetModel.PROP_BORDER_WIDTH, 3);
        initializeDynamicProperty(AbstractWidgetModel.PROP_BORDER_COLOR, "$channel$[severity]", null, Alarm.TYPE_ID);
        initializeDynamicProperty(AbstractWidgetModel.PROP_BORDER_STYLE, "$channel$[severity]", null, AlarmBorder.TYPE_ID);
        initializeDynamicProperty(AbstractWidgetModel.PROP_BORDER_WIDTH, "$channel$[severity]", null, AlarmBorderWidth.TYPE_ID);
    }

    public void initializeCommonConnectionStates() {
        Map<ConnectionState, Object> colorsByConnectionState = new HashMap<ConnectionState, Object>();
        colorsByConnectionState.put(ConnectionState.CONNECTION_LOST, ColorAndFontUtil.toHex(255,
                9, 163));
        colorsByConnectionState.put(ConnectionState.INITIAL, ColorAndFontUtil.toHex(255, 168,
                222));
        colorsByConnectionState.put(ConnectionState.CONNECTED, ColorAndFontUtil.toHex(230, 230,
                230));
        initializeDynamicPropertyForConnectionState(
                AbstractWidgetModel.PROP_COLOR_BACKGROUND, "$channel$",
                colorsByConnectionState);
    }

}
