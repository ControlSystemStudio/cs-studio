package org.csstudio.opibuilder.widgets.feedback;

import org.csstudio.opibuilder.widgets.model.KnobModel;

/**Feedback Factory for Knob.
 * @author Xihui Chen
 *
 */
public class KnobFeedbackFactory extends AbstractFixRatioSizeFeedbackFactory {

	@Override
	public int getMinimumWidth() {
		return KnobModel.MINIMUM_SIZE;
	}
	
}
