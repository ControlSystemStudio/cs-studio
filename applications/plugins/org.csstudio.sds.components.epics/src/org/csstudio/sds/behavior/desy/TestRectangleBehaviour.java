package org.csstudio.sds.behavior.desy;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.sds.components.model.RectangleModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableRectangleFigure;
import org.csstudio.sds.ui.behaviors.AbstractBehavior;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;

public class TestRectangleBehaviour extends
		AbstractBehavior<RectangleModel, RefreshableRectangleFigure> {

	@Override
	protected String[] doGetInvisiblePropertyIds() {
		String[] result = new String[] { RectangleModel.PROP_FILL,
				RectangleModel.PROP_COLOR_FOREGROUND };
		return result;
	}

	@Override
	protected void doUpdate(RectangleModel model,
			RefreshableRectangleFigure figure, AnyData value) {
		if (!value.getSeverity().isOK()) {
			figure.setForegroundColor(CustomMediaFactory.getInstance()
					.getColor(CustomMediaFactory.COLOR_RED));
		} else {
			figure.setForegroundColor(CustomMediaFactory.getInstance()
					.getColor(CustomMediaFactory.COLOR_BLACK));
		}

		figure.setFill(value.doubleValue());
		MetaData metaData = value.getMetaData();
		if (metaData != null) {
			figure.setSize(100, (int) metaData.getAlarmHigh());
		} else {
			figure.setSize(100, 50);
		}
	}

}
