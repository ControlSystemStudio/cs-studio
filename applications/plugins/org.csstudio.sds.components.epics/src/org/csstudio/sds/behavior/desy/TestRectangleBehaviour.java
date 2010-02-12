package org.csstudio.sds.behavior.desy;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.sds.components.model.RectangleModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableRectangleFigure;
import org.csstudio.sds.ui.behaviors.AbstractBehavior;
import org.eclipse.draw2d.IFigure;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;

public class TestRectangleBehaviour extends AbstractBehavior {

	@Override
	protected String[] doGetInvisiblePropertyIds() {
		String[] result = new String[] { RectangleModel.PROP_FILL,
				RectangleModel.PROP_COLOR_FOREGROUND };
		return result;
	}

	@Override
	protected void doUpdate(IFigure figure, AnyData value) {
		if (figure instanceof RefreshableRectangleFigure) {
			RefreshableRectangleFigure rectangle = (RefreshableRectangleFigure) figure;
			if (!value.getSeverity().isOK()) {
				rectangle.setForegroundColor(CustomMediaFactory.getInstance()
						.getColor(CustomMediaFactory.COLOR_RED));
			} else {
				rectangle.setForegroundColor(CustomMediaFactory.getInstance()
						.getColor(CustomMediaFactory.COLOR_BLACK));
			}
			
			rectangle.setFill(value.doubleValue());
			MetaData metaData = value.getMetaData();
			if (metaData != null) {
				rectangle.setSize(100, (int)metaData.getAlarmHigh());
			} else {
				rectangle.setSize(100, 50);
			}
		}
	}

}
