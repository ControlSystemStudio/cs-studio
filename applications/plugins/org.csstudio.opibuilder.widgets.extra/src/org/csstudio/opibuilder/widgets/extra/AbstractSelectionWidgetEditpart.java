/**
 * 
 */
package org.csstudio.opibuilder.widgets.extra;

import java.util.Arrays;
import java.util.Collection;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.ui.util.AdapterUtil;
import org.csstudio.utility.pvmanager.widgets.ConfigurableWidget;
import org.csstudio.utility.pvmanager.widgets.ConfigurableWidgetAdaptable;
import org.csstudio.utility.pvmanager.widgets.ProcessVariableAdaptable;
import org.csstudio.utility.pvmanager.widgets.VTypeAdaptable;
import org.epics.vtype.VType;

/**
 * Base class for all edit parts that are based on SWT widgets that provides a selection.
 * <p>
 * The selection is automatically use to adapt the edit part to pvs, values and
 * configurable widgets. If no selection is provided, or if the type is not
 * adaptable, the selection will be empty.
 * 
 * @author shroffk
 * 
 */
public abstract class AbstractSelectionWidgetEditpart
	<F extends AbstractSelectionWidgetFigure<?>, M extends AbstractWidgetModel> 
	extends AbstractWidgetEditPart
	implements ProcessVariableAdaptable, VTypeAdaptable, ConfigurableWidgetAdaptable {
	
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
	
	@Override
	public Collection<ProcessVariable> toProcessVariables() {
		return selectionToTypeCollection(ProcessVariable.class);
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
