/**
 * 
 */
package org.csstudio.graphene;

import org.csstudio.ui.util.ConfigurableWidget;
import org.csstudio.ui.util.ConfigurableWidgetAdaptable;
import org.epics.pvmanager.graphene.Graph2DResult;
import org.epics.vtype.VType;

/**
 * TODO better ways to handle null parts to the selection
 * 
 * @author shroffk
 * 
 */
public class LineGraph2DSelection implements VTypeAdaptable,
		ConfigurableWidgetAdaptable {

	private final LineGraph2DWidget line2dPlotWidget;

	public LineGraph2DSelection(LineGraph2DWidget line2dPlotWidget) {
		this.line2dPlotWidget = line2dPlotWidget;
	}
	
	@Override
	public VType toVType() {
		Graph2DResult result = line2dPlotWidget.getCurrentResult();
		if (result != null) {
			return result.getData();
		}
		return null;
	}

	@Override
	public ConfigurableWidget toConfigurableWidget() {
		return line2dPlotWidget;
	}

}
