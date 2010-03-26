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
	protected void doInitialize(SimpleSliderModel widget) {
	}

	@Override
	protected void doProcessConnectionStateChange(SimpleSliderModel widget, ConnectionState connectionState) {
		// TODO: 11.03.2010: swende: Keine status-abhängigen Einstellungen?
	}

	@Override
	protected void doProcessMetaDataChange(SimpleSliderModel widget, MetaData meta) {
		if (meta != null) {
			// .. update min / max
			widget.setPropertyValue(SimpleSliderModel.PROP_MAX, meta.getDisplayHigh());
			widget.setPropertyValue(SimpleSliderModel.PROP_MIN, meta.getDisplayLow());
		}
	}

	@Override
	protected void doProcessValueChange(SimpleSliderModel widget, AnyData anyData) {
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
