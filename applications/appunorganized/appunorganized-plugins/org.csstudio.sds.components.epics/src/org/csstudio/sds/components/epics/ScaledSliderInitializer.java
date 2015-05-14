package org.csstudio.sds.components.epics;

import org.csstudio.sds.components.model.ScaledSliderModel;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;

public class ScaledSliderInitializer extends AbstractEpicsWidgetInitializer {

    @Override
    protected void initialize(AbstractControlSystemSchema schema) {
        initializeCommonConnectionStates();
        initializeCommonAlarmBehaviour();
        initializeDynamicProperty(ScaledSliderModel.PROP_MIN, "$channel$[graphMin], double");
        initializeDynamicProperty(ScaledSliderModel.PROP_MAX, "$channel$[graphMax], double");
        initializeDynamicProperty(ScaledSliderModel.PROP_HIHI_LEVEL,
                "$channel$[alarmMax], double");
        initializeDynamicProperty(ScaledSliderModel.PROP_HI_LEVEL, "$channel$[warningMax], double");
        initializeDynamicProperty(ScaledSliderModel.PROP_LOLO_LEVEL,
                "$channel$[alarmMin], double");
        initializeDynamicProperty(ScaledSliderModel.PROP_LO_LEVEL, "$channel$[warningMin], double");

        initializeDynamicProperty(ScaledSliderModel.PROP_VALUE, "$channel$");
    }

}
