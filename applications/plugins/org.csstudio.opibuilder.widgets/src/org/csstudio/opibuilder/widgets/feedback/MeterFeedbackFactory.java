package org.csstudio.opibuilder.widgets.feedback;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.figures.MeterFigure;
import org.csstudio.opibuilder.widgets.model.XMeterModel;

/**Feedback Factory for LED.
 * @author Xihui Chen
 *
 */
public class MeterFeedbackFactory extends AbstractFixRatioSizeFeedbackFactory {

	@Override
	public int getMinimumWidth() {
		return XMeterModel.MINIMUM_WIDTH;
	}
	
	@Override
	public int getHeightFromWidth(int width, AbstractWidgetModel widgetModel) {
		return (int) (MeterFigure.HW_RATIO * (width));
	}

	@Override
	public int getWidthFromHeight(int height, AbstractWidgetModel widgetModel) {
		return (int) (height/MeterFigure.HW_RATIO );
	}
	
}
