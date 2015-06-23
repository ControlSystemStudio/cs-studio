/**
 *
 */
package org.csstudio.opibuilder.widgets.extra;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.visualparts.BorderFactory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.ui.util.AdapterUtil;
import org.csstudio.ui.util.composites.BeanComposite;
import org.csstudio.utility.pvmanager.widgets.ConfigurableWidget;
import org.csstudio.utility.pvmanager.widgets.ConfigurableWidgetAdaptable;
import org.csstudio.utility.pvmanager.widgets.ProcessVariableAdaptable;
import org.csstudio.utility.pvmanager.widgets.VTableWidget;
import org.csstudio.utility.pvmanager.widgets.VTypeAdaptable;
import org.eclipse.draw2d.Border;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wb.swt.SWTResourceManager;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VType;
import org.epics.vtype.ValueUtil;

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
    <F extends AbstractSelectionWidgetFigure<?>, M extends AbstractSelectionWidgetModel>
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

    @Override
    public Border calculateBorder() {
        if (getWidgetModel().isEnableBorderAlarmSensitiveProperty() && getWidgetModel().isAlarmSensitive()) {
            return createBorderFromAlarm((((VTableDisplayFigure) getFigure()).getSWTWidget()).getAlarm());
        } else {
            return super.calculateBorder();
        }
    }

    protected Border createBorderFromAlarm(Alarm alarm) {
        if (alarm.getAlarmSeverity() == AlarmSeverity.NONE) {
            return super.calculateBorder();
        } else {
            java.awt.Color awtColor = new java.awt.Color(ValueUtil.colorFor(alarm.getAlarmSeverity()));
            RGB swtColor = SWTResourceManager.getColor(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue()).getRGB();
            return BorderFactory.createBorder(BorderStyle.LINE,
                    getWidgetModel().getBorderWidth(), swtColor,
                    "");
        }
    }

    protected final void registerCommonProperties() {
        if (getWidgetModel().isEnableBorderAlarmSensitiveProperty()) {
            ((BeanComposite) getFigure().getSWTWidget()).addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent event) {
                    if ("alarm".equals(event.getPropertyName())) {
                        setFigureBorder(createBorderFromAlarm(((VTableWidget) event.getSource()).getAlarm()));
                    }
                }
            });
            setFigureBorder(calculateBorder());
        }
    }
}
