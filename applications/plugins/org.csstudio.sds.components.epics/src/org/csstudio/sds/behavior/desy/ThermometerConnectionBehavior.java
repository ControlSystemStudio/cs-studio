/*
		* Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
		* Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
		*
		* THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
		* WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT
		NOT LIMITED
		* TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE
		AND
		* NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
		BE LIABLE
		* FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
		CONTRACT,
		* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
		SOFTWARE OR
		* THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE
		DEFECTIVE
		* IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
		REPAIR OR
		* CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART
		OF THIS LICENSE.
		* NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
		DISCLAIMER.
		* DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
		ENHANCEMENTS,
		* OR MODIFICATIONS.
		* THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
		MODIFICATION,
		* USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
		DISTRIBUTION OF THIS
		* PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
		MAY FIND A COPY
		* AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
		*/
package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.components.model.AbstractMarkedWidgetModel;
import org.csstudio.sds.components.model.ThermometerModel;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;

/**
 * Default DESY-Behavior for the {@link ThermometerModel} widget with Connection state.

 *
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
public class ThermometerConnectionBehavior extends AbstractDesyConnectionBehavior<ThermometerModel> {

    private String _defFillColor;
    private String _defFillBackColor;

    /**
     * Constructor.
     */
    public ThermometerConnectionBehavior() {
        addInvisiblePropertyId(AbstractMarkedWidgetModel.PROP_MIN);
        addInvisiblePropertyId(AbstractMarkedWidgetModel.PROP_MAX);
        addInvisiblePropertyId(AbstractMarkedWidgetModel.PROP_HIHI_LEVEL);
        addInvisiblePropertyId(AbstractMarkedWidgetModel.PROP_HI_LEVEL);
        addInvisiblePropertyId(AbstractMarkedWidgetModel.PROP_LOLO_LEVEL);
        addInvisiblePropertyId(AbstractMarkedWidgetModel.PROP_LO_LEVEL);
        addInvisiblePropertyId(ThermometerModel.PROP_FILL_COLOR);
        addInvisiblePropertyId(ThermometerModel.PROP_FILLBACKGROUND_COLOR);
        addInvisiblePropertyId(AbstractMarkedWidgetModel.PROP_TRANSPARENT);
        addInvisiblePropertyId(AbstractMarkedWidgetModel.PROP_ACTIONDATA);
        addInvisiblePropertyId(AbstractMarkedWidgetModel.PROP_BORDER_STYLE);
        addInvisiblePropertyId(AbstractMarkedWidgetModel.PROP_SHOW_HI);
        addInvisiblePropertyId(AbstractMarkedWidgetModel.PROP_SHOW_HIHI);
        addInvisiblePropertyId(AbstractMarkedWidgetModel.PROP_SHOW_LO);
        addInvisiblePropertyId(AbstractMarkedWidgetModel.PROP_SHOW_LOLO);
        addInvisiblePropertyId(AbstractMarkedWidgetModel.PROP_VALUE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInitialize(final ThermometerModel widget) {
        super.doInitialize(widget);
        _defFillColor = widget.getColor(ThermometerModel.PROP_FILL_COLOR);
        _defFillBackColor = widget.getColor(ThermometerModel.PROP_FILLBACKGROUND_COLOR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcessValueChange(final ThermometerModel model, final AnyData anyData) {
        super.doProcessValueChange(model, anyData);
        // .. fill level (influenced by current value)
        model.setPropertyValue(AbstractMarkedWidgetModel.PROP_VALUE, anyData.numberValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcessConnectionStateChange(final ThermometerModel widget, final ConnectionState connectionState) {
//        super.doProcessConnectionStateChange(widget, connectionState);
        String fillBackColor = (connectionState==ConnectionState.CONNECTED)?_defFillBackColor  : determineBackgroundColor(connectionState);
        widget.setPropertyValue(ThermometerModel.PROP_FILLBACKGROUND_COLOR, fillBackColor);
        String fillColor = (connectionState==ConnectionState.CONNECTED)?_defFillColor  : determineBackgroundColor(connectionState);
        widget.setPropertyValue(ThermometerModel.PROP_FILL_COLOR, fillColor);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcessMetaDataChange(final ThermometerModel widget, final MetaData meta) {
        if (meta != null) {
            // .. limits
            widget.setPropertyValue(AbstractMarkedWidgetModel.PROP_MIN, meta.getDisplayLow());
            widget.setPropertyValue(AbstractMarkedWidgetModel.PROP_MAX, meta.getDisplayHigh());

            widget.setPropertyValue(AbstractMarkedWidgetModel.PROP_HIHI_LEVEL, meta.getAlarmHigh());
            widget.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_HIHI, !Double.isNaN(meta.getAlarmHigh()));

            widget.setPropertyValue(AbstractMarkedWidgetModel.PROP_HI_LEVEL, meta.getWarnHigh());
            widget.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_HI, !Double.isNaN(meta.getWarnHigh()));

            widget.setPropertyValue(AbstractMarkedWidgetModel.PROP_LOLO_LEVEL, meta.getAlarmLow());
            widget.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_LOLO, !Double.isNaN(meta.getAlarmLow()));

            widget.setPropertyValue(AbstractMarkedWidgetModel.PROP_LO_LEVEL, meta.getWarnLow());
            widget.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_LO, !Double.isNaN(meta.getWarnLow()));
        }
    }

    @Override
    protected String[] doGetSettablePropertyIds() {
        return new String[] { AbstractMarkedWidgetModel.PROP_VALUE };
    }

}
