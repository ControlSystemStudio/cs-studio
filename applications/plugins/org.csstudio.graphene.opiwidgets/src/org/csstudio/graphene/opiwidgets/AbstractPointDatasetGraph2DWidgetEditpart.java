/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import java.util.Arrays;
import java.util.Collection;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.ui.util.AdapterUtil;
import org.csstudio.ui.util.ConfigurableWidget;
import org.csstudio.ui.util.ConfigurableWidgetAdaptable;
import org.csstudio.utility.pvmanager.widgets.VTypeAdaptable;
import org.epics.vtype.VType;

/**
 * @author shroffk
 * 
 */
public abstract class AbstractPointDatasetGraph2DWidgetEditpart<F extends AbstractPointDatasetGraph2DWidgetFigure<?>,
M extends AbstractWidgetModel> extends AbstractWidgetEditPart implements
		VTypeAdaptable,
		ConfigurableWidgetAdaptable {
	
	@Override
	public F getFigure() {
		@SuppressWarnings("unchecked")
		F figure = (F) super.getFigure();
		return figure;
	}
	
	@Override
	public M getWidgetModel() {
		@SuppressWarnings("unchecked")
		M widgetModel = (M) super.getWidgetModel();
		return widgetModel;
	}

	@Override
	public VType toVType() {
		return selectionToTypeSingle(VType.class);
	}

	@Override
	public ConfigurableWidget toConfigurableWidget() {
		return selectionToTypeSingle(ConfigurableWidget.class);
	}

	protected <T> Collection<T> selectionToTypeCollection(Class<T> clazz) {
		if (getFigure().getSelectionProvider() == null)
			return null;
		return Arrays.asList(AdapterUtil.convert(
				getFigure().getSelectionProvider()
						.getSelection(), clazz));
	}

	protected <T> T selectionToTypeSingle(Class<T> clazz) {
		if (getFigure().getSelectionProvider() == null)
			return null;
		T[] adapted = AdapterUtil.convert(getFigure().getSelectionProvider()
						.getSelection(), clazz);
		if (adapted.length == 0) {
			return null;
		} else {
			return adapted[0];
		}
	}

}
