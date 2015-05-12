package org.csstudio.sds.components.epics;

import org.csstudio.sds.components.model.TankModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;

public class TankInitializer extends AbstractEpicsWidgetInitializer {

    @Override
    protected void initialize(AbstractControlSystemSchema schema) {
        initializeCommonConnectionStates();
        initializeCommonAlarmBehaviour();
        initializeDynamicProperty(TankModel.PROP_MIN, "$channel$[graphMin], double");
        initializeDynamicProperty(TankModel.PROP_MAX, "$channel$[graphMax], double");
        initializeDynamicProperty(TankModel.PROP_HIHI_LEVEL,
                "$channel$[alarmMax], double");
        initializeDynamicProperty(TankModel.PROP_HI_LEVEL, "$channel$[warningMax], double");
        initializeDynamicProperty(TankModel.PROP_LOLO_LEVEL,
                "$channel$[alarmMin], double");
        initializeDynamicProperty(TankModel.PROP_LO_LEVEL, "$channel$[warningMin], double");

        initializeDynamicProperty(TankModel.PROP_VALUE, "$channel$");
    }

}
