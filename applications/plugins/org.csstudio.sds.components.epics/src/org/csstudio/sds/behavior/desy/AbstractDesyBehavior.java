package org.csstudio.sds.behavior.desy;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.cosyrules.color.Alarm;
import org.csstudio.sds.eventhandling.AbstractBehavior;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.sds.util.ColorAndFontUtil;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.simple.Severity;

/**
 * Default DESY-Behaviour for the {@link BargraphModel} widget.
 * 
 * @author Sven Wende
 * 
 */
public abstract class AbstractDesyBehavior<W extends AbstractWidgetModel> extends AbstractBehavior<W> {
	private static final String YELLOW = ColorAndFontUtil.toHex(255, 168, 222);
	private static final String GREEN = ColorAndFontUtil.toHex(120, 120, 120);
	private static final String RED = ColorAndFontUtil.toHex(255, 9, 163);
	private Map<ConnectionState, String> colorsByConnectionState;
	private Map<ConnectionState, BorderStyleEnum> borderStyleByConnectionState;
	private Map<ConnectionState, String> borderColorsByConnectionState;
	private Map<ConnectionState, Integer> borderWidthByConnectionState;

	public AbstractDesyBehavior() {
		colorsByConnectionState = new HashMap<ConnectionState, String>();
		colorsByConnectionState.put(ConnectionState.CONNECTED, GREEN);
		colorsByConnectionState.put(ConnectionState.CONNECTING, YELLOW);
		colorsByConnectionState.put(ConnectionState.INITIAL, YELLOW);
		colorsByConnectionState.put(ConnectionState.CONNECTION_FAILED, RED);
		colorsByConnectionState.put(ConnectionState.CONNECTION_LOST, RED);
		colorsByConnectionState.put(ConnectionState.DESTROYED, RED);
		colorsByConnectionState.put(ConnectionState.DISCONNECTED, RED);
		colorsByConnectionState.put(ConnectionState.DISCONNECTING, RED);
		colorsByConnectionState.put(ConnectionState.READY, GREEN);

		borderColorsByConnectionState = new HashMap<ConnectionState, String>();
		borderColorsByConnectionState.put(ConnectionState.CONNECTED, GREEN);
		borderColorsByConnectionState.put(ConnectionState.CONNECTING, YELLOW);
		borderColorsByConnectionState.put(ConnectionState.INITIAL, YELLOW);
		borderColorsByConnectionState.put(ConnectionState.CONNECTION_FAILED, RED);
		borderColorsByConnectionState.put(ConnectionState.CONNECTION_LOST, RED);
		borderColorsByConnectionState.put(ConnectionState.DESTROYED, RED);
		borderColorsByConnectionState.put(ConnectionState.DISCONNECTED, RED);
		borderColorsByConnectionState.put(ConnectionState.DISCONNECTING, RED);
		borderColorsByConnectionState.put(ConnectionState.READY, GREEN);
		
		borderStyleByConnectionState = new HashMap<ConnectionState, BorderStyleEnum>();
		borderStyleByConnectionState.put(ConnectionState.CONNECTED, BorderStyleEnum.NONE);
		borderStyleByConnectionState.put(ConnectionState.CONNECTING, BorderStyleEnum.DASH_DOT);
		borderStyleByConnectionState.put(ConnectionState.INITIAL, BorderStyleEnum.DASH_DOT);
		borderStyleByConnectionState.put(ConnectionState.CONNECTION_FAILED, BorderStyleEnum.DASH_DOT);
		borderStyleByConnectionState.put(ConnectionState.CONNECTION_LOST, BorderStyleEnum.DASH_DOT);
		borderStyleByConnectionState.put(ConnectionState.DESTROYED, BorderStyleEnum.DASH_DOT);
		borderStyleByConnectionState.put(ConnectionState.DISCONNECTED, BorderStyleEnum.DASH_DOT);
		borderStyleByConnectionState.put(ConnectionState.DISCONNECTING, BorderStyleEnum.DASH_DOT);
		borderStyleByConnectionState.put(ConnectionState.READY, BorderStyleEnum.NONE);
		
		borderWidthByConnectionState = new HashMap<ConnectionState, Integer>();
		borderWidthByConnectionState.put(ConnectionState.CONNECTED, 0);
		borderWidthByConnectionState.put(ConnectionState.CONNECTING, 1);
		borderWidthByConnectionState.put(ConnectionState.INITIAL,1);
		borderWidthByConnectionState.put(ConnectionState.CONNECTION_FAILED, 1);
		borderWidthByConnectionState.put(ConnectionState.CONNECTION_LOST, 1);
		borderWidthByConnectionState.put(ConnectionState.DESTROYED, 1);
		borderWidthByConnectionState.put(ConnectionState.DISCONNECTED, 1);
		borderWidthByConnectionState.put(ConnectionState.DISCONNECTING, 1);
		borderWidthByConnectionState.put(ConnectionState.READY, 0);
		
		
	}

	protected BorderStyleEnum determineBorderStyle(ConnectionState connectionState) {
		return connectionState != null ? borderStyleByConnectionState.get(connectionState) : borderStyleByConnectionState
				.get(ConnectionState.INITIAL);
	}

	protected String determineBackgroundColor(ConnectionState connectionState) {
		return connectionState != null ? colorsByConnectionState.get(connectionState) : colorsByConnectionState.get(ConnectionState.INITIAL);
	}

	protected String determineBorderColor(ConnectionState connectionState) {
		return connectionState != null ? borderColorsByConnectionState.get(connectionState) : borderColorsByConnectionState.get(ConnectionState.INITIAL);
	}

	protected Integer determineBorderWidth(ConnectionState connectionState) {
		return connectionState != null ? borderWidthByConnectionState.get(connectionState) : borderWidthByConnectionState.get(ConnectionState.INITIAL);
	}
	
	/**
	 * The new way?
	 * 
	 * @param severity
	 * @return
	 */
	protected String determineColorBySeverity(Severity severity) {
		String color = "#000000";

		if (severity != null) {
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
