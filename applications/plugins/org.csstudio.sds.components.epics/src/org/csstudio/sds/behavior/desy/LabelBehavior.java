package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.eventhandling.AbstractBehavior;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.LabelModel;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;

public class LabelBehavior extends
		AbstractBehavior<LabelModel> {


	private static final String YELLOW = "#ffff00";
	private static final String GREEN = "#00ff00";
	private static final String RED = "#ff0000";

	@Override
	protected String[] doGetInvisiblePropertyIds() {
		return new String[] { AbstractWidgetModel.PROP_BORDER_STYLE, AbstractWidgetModel.PROP_BORDER_WIDTH,
				AbstractWidgetModel.PROP_BORDER_COLOR, LabelModel.PROP_TEXTVALUE };
	}

	@Override
	protected void doInitialize(LabelModel widget) {
		indicateState(widget, YELLOW, "III");
		
	}
	
	@Override
	protected void doProcessValueChange(LabelModel model, AnyData anyData) {
		model.setPropertyValue(LabelModel.PROP_TEXTVALUE, anyData.stringValue());
	}

	@Override
	protected void doProcessConnectionStateChange(LabelModel widget, org.epics.css.dal.context.ConnectionState connectionState) {
		switch (connectionState) {
		case CONNECTED:
			indicateState(widget, GREEN, "Connected");
			break;
		case INITIAL: 
			indicateState(widget, YELLOW, "Connecting ...");
			break;
		case CONNECTION_LOST:
			indicateState(widget, RED, "Lost");
			break;
		case CONNECTION_FAILED:
			indicateState(widget, YELLOW, "Failed");
			break;
		case DISCONNECTED:
			indicateState(widget, YELLOW, "Disconnected");
			break;
		default:
			break;
		}

		widget.setPropertyValue(LabelModel.PROP_TOOLTIP, connectionState.toString());
	}

	@Override
	protected void doProcessMetaDataChange(LabelModel widget, AnyData anyData) {
		StringBuffer buffer = new StringBuffer();

		MetaData metaData = anyData.getMetaData();
		if (metaData == null) {
			buffer.append("No Metadata available");
		} else {
			buffer.append("AlarmHigh: ");
			buffer.append(metaData.getAlarmHigh());
			buffer.append("\n");
			buffer.append("AlarmLow: ");
			buffer.append(metaData.getAlarmLow());
			buffer.append("\n");
			buffer.append("DisplayHigh: ");
			buffer.append(metaData.getDisplayHigh());
			buffer.append("\n");
			buffer.append("DisplayLow: ");
			buffer.append(metaData.getDisplayLow());
			buffer.append("\n");
			buffer.append("Precision: ");
			buffer.append(metaData.getPrecision());
			buffer.append("\n");
			buffer.append("Units: ");
			buffer.append(metaData.getUnits());
			buffer.append("\n");
			buffer.append("WarnHigh: ");
			buffer.append(metaData.getWarnHigh());
			buffer.append("\n");
			buffer.append("WarnLow: ");
			buffer.append(metaData.getWarnLow());
			buffer.append("\n");
			buffer.append("States: ");
			if (metaData.getStates() != null) {
				for (String state : metaData.getStates()) {
					buffer.append("\t");
					buffer.append(state);
					buffer.append("\n");
				}
			}
		}

		widget.setPropertyValue(LabelModel.PROP_TEXTVALUE, buffer.toString());
	}

	private void indicateState(LabelModel widget, String color, String text) {
		widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE, 1);
		widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH, 2);
		widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR, color);
		widget.setPropertyValue(LabelModel.PROP_TEXTVALUE, text);
	}
	
	
}
