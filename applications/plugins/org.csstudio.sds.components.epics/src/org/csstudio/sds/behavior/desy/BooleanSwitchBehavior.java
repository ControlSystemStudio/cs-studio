package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.components.model.BooleanSwitchModel;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;

public class BooleanSwitchBehavior extends
		AbstractDesyBehavior<BooleanSwitchModel> {

	@Override
	protected String[] doGetInvisiblePropertyIds() {
		return new String[] { BooleanSwitchModel.PROP_VALUE};
	}

	@Override
	protected void doInitialize(BooleanSwitchModel widget) {
	}

	@Override
	protected void doProcessConnectionStateChange(BooleanSwitchModel widget,
			ConnectionState connectionState) {
	}

	@Override
	protected void doProcessMetaDataChange(BooleanSwitchModel widget,
			MetaData metaData) {
	}

	@Override
	protected void doProcessValueChange(BooleanSwitchModel model,
			AnyData anyData) {
		// .. value (influenced by current value, depending on onTrue Value)
		double value = anyData.doubleValue();
		boolean b = value == model
				.getDoubleProperty(BooleanSwitchModel.PROP_ON_STATE_VALUE);
		model.setPropertyValue(BooleanSwitchModel.PROP_VALUE, b);
	}

	@Override
	protected Object doConvertOutgoingValue(BooleanSwitchModel widgetModel,
			String propertyId, Object value) {
		if (propertyId.equals(BooleanSwitchModel.PROP_VALUE)) {
			boolean currentValue = widgetModel
					.getBooleanProperty(BooleanSwitchModel.PROP_VALUE);
			double outgoingValue = widgetModel
					.getDoubleProperty(BooleanSwitchModel.PROP_OFF_STATE_VALUE);
			if (currentValue) {
				outgoingValue = widgetModel
						.getDoubleProperty(BooleanSwitchModel.PROP_ON_STATE_VALUE);
			}
			return outgoingValue;
		} else {
			return super.doConvertOutgoingValue(widgetModel, propertyId, value);
		}
	}

}
