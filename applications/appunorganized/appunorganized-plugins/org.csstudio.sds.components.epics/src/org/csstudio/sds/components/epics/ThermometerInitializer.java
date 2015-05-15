package org.csstudio.sds.components.epics;

import org.csstudio.sds.components.model.ThermometerModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;

public class ThermometerInitializer extends AbstractEpicsWidgetInitializer {

    @Override
    protected void initialize(AbstractControlSystemSchema schema) {
        initializeCommonConnectionStates();
        initializeCommonAlarmBehaviour();
        initializeDynamicProperty(ThermometerModel.PROP_MIN, "$channel$[graphMin], double");
        initializeDynamicProperty(ThermometerModel.PROP_MAX, "$channel$[graphMax], double");
        initializeDynamicProperty(ThermometerModel.PROP_HIHI_LEVEL,
                "$channel$[alarmMax], double");
        initializeDynamicProperty(ThermometerModel.PROP_HI_LEVEL, "$channel$[warningMax], double");
        initializeDynamicProperty(ThermometerModel.PROP_LOLO_LEVEL,
                "$channel$[alarmMin], double");
        initializeDynamicProperty(ThermometerModel.PROP_LO_LEVEL, "$channel$[warningMin], double");

        initializeDynamicProperty(ThermometerModel.PROP_VALUE, "$channel$");
    }

}
