package org.csstudio.opibuilder.palette;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.WidgetDescriptor;
import org.eclipse.gef.requests.CreationFactory;


/**The CreationFactory to create the widget.
 * @author Xihui Chen
 *
 */
public class WidgetCreationFactory implements CreationFactory {

	private final WidgetDescriptor widgetDescriptor;
	private AbstractWidgetModel widgetModel = null;
	
	public WidgetCreationFactory(WidgetDescriptor widgetDescriptor) {
		this.widgetDescriptor = widgetDescriptor;
	}

	public Object getNewObject() {
		widgetModel = widgetDescriptor.getWidgetModel();
		return widgetModel;
	}

	public Object getObjectType() {
		if(widgetModel == null)
			widgetModel = widgetDescriptor.getWidgetModel();
		Object widgetClass = widgetModel.getClass();
		return widgetClass;
	}

}
