/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.components.model.SimpleSliderModel;
import org.csstudio.sds.eventhandling.AbstractBehavior;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;
import org.epics.css.dal.simple.Severity;

/**
 * Default DESY-Behaviour for the {@link BargraphModel} widget.
 *
 * @author Sven Wende
 *
 */
public class SimpleSliderBehavior extends AbstractBehavior<SimpleSliderModel> {

	@Override
	protected String[] doGetInvisiblePropertyIds() {
		return new String[] { SimpleSliderModel.PROP_NAME, SimpleSliderModel.PROP_VALUE, SimpleSliderModel.PROP_MAX, SimpleSliderModel.PROP_MIN,
				SimpleSliderModel.PROP_BORDER_COLOR, SimpleSliderModel.PROP_BORDER_STYLE, SimpleSliderModel.PROP_BORDER_WIDTH };
	}

	@Override
	protected void doInitialize(final SimpleSliderModel widget) {
	}

	@Override
	protected void doProcessConnectionStateChange(final SimpleSliderModel widget, final ConnectionState connectionState) {
		// TODO: 11.03.2010: swende: Keine status-abhängigen Einstellungen?
	}

	@Override
	protected void doProcessMetaDataChange(final SimpleSliderModel widget, final MetaData meta) {
		if (meta != null) {
			// .. update min / max
			widget.setPropertyValue(SimpleSliderModel.PROP_MAX, meta.getDisplayHigh());
			widget.setPropertyValue(SimpleSliderModel.PROP_MIN, meta.getDisplayLow());
		}
	}

	@Override
	protected void doProcessValueChange(final SimpleSliderModel widget, final AnyData anyData) {
		// .. update slider value
		widget.setPropertyValue(SimpleSliderModel.PROP_VALUE, anyData.doubleValue());

		// .. update alarm state depending on severity
		Severity severity = anyData.getSeverity();
		if (severity != null) {
			widget.setPropertyValue(SimpleSliderModel.PROP_BORDER_COLOR, SeverityUtil.determineColorBySeverity(severity));
			widget.setPropertyValue(SimpleSliderModel.PROP_BORDER_STYLE, SeverityUtil.determineBorderStyleBySeverity(severity).getIndex());
			widget.setPropertyValue(SimpleSliderModel.PROP_BORDER_WIDTH, SeverityUtil.determineBorderWidthBySeverity(severity));
		}

	}

	@Override
	protected String[] doGetSettablePropertyIds() {
		return new String[] { SimpleSliderModel.PROP_VALUE};
	}

}
