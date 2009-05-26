package org.csstudio.sds.components.epics;

import org.csstudio.sds.components.model.GaugeModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;

public class GaugeInitializer extends AbstractEpicsWidgetInitializer {

    @Override
    protected void initialize(AbstractControlSystemSchema schema) {
        initializeCommonConnectionStates();
        initializeCommonAlarmBehaviour();
        initializeDynamicProperty(GaugeModel.PROP_MIN, "$channel$[graphMin], double");
        initializeDynamicProperty(GaugeModel.PROP_MAX, "$channel$[graphMax], double");
        initializeDynamicProperty(GaugeModel.PROP_HIHI_LEVEL,
                "$channel$[alarmMax], double");
        initializeDynamicProperty(GaugeModel.PROP_HI_LEVEL, "$channel$[warningMax], double");
        initializeDynamicProperty(GaugeModel.PROP_LOLO_LEVEL,
                "$channel$[alarmMin], double");
        initializeDynamicProperty(GaugeModel.PROP_LO_LEVEL, "$channel$[warningMin], double");

        initializeDynamicProperty(GaugeModel.PROP_VALUE, "$channel$");
    }
}
