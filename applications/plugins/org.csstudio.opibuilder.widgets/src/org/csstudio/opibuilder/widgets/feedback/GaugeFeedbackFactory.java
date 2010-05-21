package org.csstudio.opibuilder.widgets.feedback;

import org.csstudio.opibuilder.widgets.model.GaugeModel;

/**Feedback Factory for Gauge.
 * @author Xihui Chen
 *
 */
public class GaugeFeedbackFactory extends AbstractFixRatioSizeFeedbackFactory {

	@Override
	public int getMinimumWidth() {
		return GaugeModel.MINIMUM_SIZE;
	}
	
}
