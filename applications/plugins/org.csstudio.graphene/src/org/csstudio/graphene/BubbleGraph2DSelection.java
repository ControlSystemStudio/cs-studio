/**
 * 
 */
package org.csstudio.graphene;

import org.csstudio.ui.util.ConfigurableWidget;
import org.csstudio.ui.util.ConfigurableWidgetAdaptable;
import org.csstudio.utility.pvmanager.widgets.VTypeAdaptable;
import org.epics.pvmanager.graphene.Graph2DResult;
import org.epics.vtype.VType;

/**
 * @author shroffk
 * 
 */
public class BubbleGraph2DSelection implements VTypeAdaptable,
		ConfigurableWidgetAdaptable {

	private final BubbleGraph2DWidget widget;

	public BubbleGraph2DSelection(BubbleGraph2DWidget widget) {
		this.widget = widget;
	}

	@Override
	public VType toVType() {
		Graph2DResult result = widget.getCurrentResult();
		if (result != null) {
			return result.getData();
		}
		return null;
	}

	@Override
	public ConfigurableWidget toConfigurableWidget() {
		return widget;
	}
}
