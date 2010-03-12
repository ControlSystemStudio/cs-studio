package org.csstudio.sds.behavior.desy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.cosyrules.color.Alarm;
import org.csstudio.sds.eventhandling.AbstractBehavior;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.sds.model.LabelModel;
import org.csstudio.sds.util.ColorAndFontUtil;
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
public class BargraphBehavior extends AbstractBehavior<BargraphModel> {
	private Map<org.epics.css.dal.context.ConnectionState, String> colorsByConnectionState;

	private Map<ConnectionState, Boolean> transparencyByConnectionState;

	public BargraphBehavior() {
		colorsByConnectionState = new HashMap<ConnectionState, String>();
		colorsByConnectionState.put(ConnectionState.INITIAL, ColorAndFontUtil.toHex(255, 168, 222));
		colorsByConnectionState.put(ConnectionState.CONNECTED, ColorAndFontUtil.toHex(120, 120, 120));
		colorsByConnectionState.put(ConnectionState.CONNECTION_LOST, ColorAndFontUtil.toHex(255, 9, 163));

		transparencyByConnectionState = new HashMap<ConnectionState, Boolean>();
		transparencyByConnectionState.put(ConnectionState.CONNECTED, true);
		transparencyByConnectionState.put(ConnectionState.CONNECTION_LOST, false);
		transparencyByConnectionState.put(ConnectionState.INITIAL, false);
	}

	@Override
	protected String[] doGetInvisiblePropertyIds() {
		return new String[] { BargraphModel.PROP_MIN, BargraphModel.PROP_MAX, BargraphModel.PROP_HIHI_LEVEL, BargraphModel.PROP_HI_LEVEL,
				BargraphModel.PROP_LOLO_LEVEL, BargraphModel.PROP_LO_LEVEL, BargraphModel.PROP_DEFAULT_FILL_COLOR,
				BargraphModel.PROP_FILLBACKGROUND_COLOR, BargraphModel.PROP_FILL, BargraphModel.PROP_TRANSPARENT };

	}

	@Override
	protected void doInitialize(BargraphModel widget) {
		widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE, BorderStyleEnum.DOTTED.getIndex());
		widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH, 2);
		widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR, "#C0C000"); 
	}

	@Override
	protected void doProcessValueChange(BargraphModel widget, AnyData anyData) {
		// .. fill level (influenced by current value)
		widget.setPropertyValue(BargraphModel.PROP_FILL, anyData.doubleValue());

		// .. fill color (influenced by severity)
		Severity severity = anyData.getSeverity();
		if (severity != null) {
			widget.setPropertyValue(BargraphModel.PROP_DEFAULT_FILL_COLOR, determineColorBySeverity(severity));
		}
	}

	@Override
	protected void doProcessConnectionStateChange(BargraphModel widget, org.epics.css.dal.context.ConnectionState connectionState) {
		System.out.println(connectionState);
		// .. change border
		if(connectionState!=ConnectionState.INITIAL) {
			widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE, BorderStyleEnum.NONE.getIndex());
			System.out.println("setting border style to none");
		}
		
		// .. change color
		String color = colorsByConnectionState.get(connectionState);
		if (color != null) {
			widget.setPropertyValue(BargraphModel.PROP_FILLBACKGROUND_COLOR, color);
			widget.setPropertyValue(BargraphModel.PROP_COLOR_BACKGROUND, color);
			widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR, color);
		}

		// .. change transparency
		Boolean transparent = transparencyByConnectionState.get(connectionState);

		if (transparent != null) {
			widget.setPropertyValue(BargraphModel.PROP_TRANSPARENT, transparent);
		}
	}

	@Override
	protected void doProcessMetaDataChange(BargraphModel widget, AnyData anyData) {
		MetaData meta = anyData.getMetaData();

		if (meta != null) {
			// .. limits
			widget.setPropertyValue(BargraphModel.PROP_MIN, meta.getDisplayLow());
			widget.setPropertyValue(BargraphModel.PROP_MAX, meta.getDisplayHigh());
			widget.setPropertyValue(BargraphModel.PROP_HIHI_LEVEL, meta.getAlarmHigh());
			widget.setPropertyValue(BargraphModel.PROP_HI_LEVEL, meta.getWarnHigh());
			widget.setPropertyValue(BargraphModel.PROP_LOLO_LEVEL, meta.getAlarmLow());
			widget.setPropertyValue(BargraphModel.PROP_LO_LEVEL, meta.getWarnLow());
		}
	}

	/**
	 * The new way?
	 * 
	 * @param severity
	 * @return
	 */
	public static String determineColorBySeverity(Severity severity) {
		assert severity != null;

		String color = "#000000";

		if (severity.isOK()) {
			// .. green
			color = ColorAndFontUtil.toHex(0, 216, 0);
		} else if (severity.isMinor()) {
			// .. yellow
			color = ColorAndFontUtil.toHex(251, 243, 74);
		} else if (severity.isMajor()) {
			// .. red
			color = ColorAndFontUtil.toHex(253, 0, 0);
		} else {
			// .. white
			color = ColorAndFontUtil.toHex(255, 255, 255);
		}

		return color;
	}

	/**
	 * Extracted from {@link Alarm}.
	 * 
	 * @param d
	 * @return
	 */
	public static String determineColorBySeverity(double d) {
		String color = "#000000";

		if (Math.abs(d - 0.0) < 0.00001) {
			// .. green
			color = ColorAndFontUtil.toHex(0, 216, 0);
		} else if (Math.abs(d - 1.0) < 0.00001) {
			// .. yellow
			color = ColorAndFontUtil.toHex(251, 243, 74);
		} else if (Math.abs(d - 2.0) < 0.00001) {
			// .. red
			color = ColorAndFontUtil.toHex(253, 0, 0);
		} else if (d >= 3.0 && d <= 255.0) {
			// .. white
			color = ColorAndFontUtil.toHex(255, 255, 255);
		}

		return color;
	}

}
