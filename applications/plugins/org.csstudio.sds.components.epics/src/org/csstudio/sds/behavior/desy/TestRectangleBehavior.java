package org.csstudio.sds.behavior.desy;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.sds.behaviors.AbstractBehavior;
import org.csstudio.sds.components.model.RectangleModel;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;

public class TestRectangleBehavior extends
		AbstractBehavior<RectangleModel> {

	@Override
	protected String[] doGetInvisiblePropertyIds() {
		String[] result = new String[] { RectangleModel.PROP_FILL,
				RectangleModel.PROP_COLOR_FOREGROUND, RectangleModel.PROP_WIDTH, RectangleModel.PROP_HEIGHT };
		return result;
	}

	@Override
	protected void doUpdate(RectangleModel model, AnyData value) {
		if (!value.getSeverity().isOK()) {
			model.setPropertyValue(RectangleModel.PROP_COLOR_FOREGROUND, CustomMediaFactory.COLOR_RED);
		} else {
			model.setPropertyValue(RectangleModel.PROP_COLOR_FOREGROUND, CustomMediaFactory.COLOR_BLACK);
		}

		model.setPropertyValue(RectangleModel.PROP_FILL, value.doubleValue());
		MetaData metaData = value.getMetaData();
		model.setPropertyValue(RectangleModel.PROP_HEIGHT, 50);
		if (metaData != null) {
			model.setPropertyValue(RectangleModel.PROP_WIDTH, (int) metaData.getAlarmHigh());
		} else {
			model.setPropertyValue(RectangleModel.PROP_WIDTH, 50);
		}
	}

}
