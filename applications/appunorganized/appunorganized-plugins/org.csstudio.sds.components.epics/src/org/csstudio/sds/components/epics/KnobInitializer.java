package org.csstudio.sds.components.epics;

import org.csstudio.sds.components.model.KnobModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;

public class KnobInitializer extends AbstractEpicsWidgetInitializer {

    @Override
    protected void initialize(AbstractControlSystemSchema schema) {
        initializeCommonConnectionStates();
        initializeCommonAlarmBehaviour();
        initializeDynamicProperty(KnobModel.PROP_MIN, "$channel$[graphMin], double");
        initializeDynamicProperty(KnobModel.PROP_MAX, "$channel$[graphMax], double");
        initializeDynamicProperty(KnobModel.PROP_HIHI_LEVEL,
                "$channel$[alarmMax], double");
        initializeDynamicProperty(KnobModel.PROP_HI_LEVEL, "$channel$[warningMax], double");
        initializeDynamicProperty(KnobModel.PROP_LOLO_LEVEL,
                "$channel$[alarmMin], double");
        initializeDynamicProperty(KnobModel.PROP_LO_LEVEL, "$channel$[warningMin], double");

        initializeDynamicProperty(KnobModel.PROP_VALUE, "$channel$");
   }
}
