package org.csstudio.opibuilder.widgets.feedback;

import org.csstudio.opibuilder.widgets.model.KnobModel;

/**Feedback Factory for Knob.
 * @author Xihui Chen
 *
 */
public class KnobFeedbackFactory extends AbstractSquareSizeFeedbackFactory {

	@Override
	public int getMinimumSize() {
		return KnobModel.MINIMUM_SIZE;
	}
	
}
