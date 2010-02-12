package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.model.LabelModel;
import org.csstudio.sds.ui.behaviors.AbstractBehavior;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;

public class TestLabelBehavior extends
		AbstractBehavior<LabelModel, RefreshableLabelFigure> {

	@Override
	protected String[] doGetInvisiblePropertyIds() {
		return new String[] { LabelModel.PROP_TEXTVALUE };
	}

	@Override
	protected void doUpdate(LabelModel model, RefreshableLabelFigure figure,
			AnyData value) {
		StringBuffer buffer = new StringBuffer();

		MetaData metaData = value.getMetaData();
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

		figure.setTextValue(buffer.toString());
	}

}
