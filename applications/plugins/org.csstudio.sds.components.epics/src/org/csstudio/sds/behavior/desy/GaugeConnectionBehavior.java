/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.components.model.GaugeModel;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;

/**
 *
 * Default DESY-Behavior for the {@link GaugeModel} widget with Connection state
 *
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 20.04.2010
 */
public class GaugeConnectionBehavior extends AbstractDesyConnectionBehavior<GaugeModel> {

    /**
     * Constructor.
     */
    public GaugeConnectionBehavior() {
     // add Invisible Property Id here
        addInvisiblePropertyId(GaugeModel.PROP_MIN);
        addInvisiblePropertyId(GaugeModel.PROP_MAX);
        addInvisiblePropertyId(GaugeModel.PROP_HIHI_LEVEL);
        addInvisiblePropertyId(GaugeModel.PROP_HI_LEVEL);
        addInvisiblePropertyId(GaugeModel.PROP_LOLO_LEVEL);
        addInvisiblePropertyId(GaugeModel.PROP_LO_LEVEL);
        addInvisiblePropertyId(GaugeModel.PROP_TRANSPARENT);
        addInvisiblePropertyId(GaugeModel.PROP_ACTIONDATA);
        addInvisiblePropertyId(GaugeModel.PROP_BORDER_STYLE);
        addInvisiblePropertyId(GaugeModel.PROP_SHOW_HI);
        addInvisiblePropertyId(GaugeModel.PROP_SHOW_HIHI);
        addInvisiblePropertyId(GaugeModel.PROP_SHOW_LO);
        addInvisiblePropertyId(GaugeModel.PROP_SHOW_LOLO);
        addInvisiblePropertyId(GaugeModel.PROP_VALUE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcessValueChange(final GaugeModel model, final AnyData anyData) {
        super.doProcessValueChange(model, anyData);
        // .. fill level (influenced by current value)
        model.setPropertyValue(GaugeModel.PROP_VALUE, anyData.numberValue());
    }

    @Override
    protected void doProcessMetaDataChange(final GaugeModel widget, final MetaData meta) {
        if (meta != null) {
            // .. limits
            widget.setPropertyValue(GaugeModel.PROP_MIN, meta.getDisplayLow());
            widget.setPropertyValue(GaugeModel.PROP_MAX, meta.getDisplayHigh());

            widget.setPropertyValue(GaugeModel.PROP_HIHI_LEVEL, meta.getAlarmHigh());
            widget.setPropertyValue(GaugeModel.PROP_SHOW_HIHI, !Double.isNaN(meta.getAlarmHigh()));

            widget.setPropertyValue(GaugeModel.PROP_HI_LEVEL, meta.getWarnHigh());
            widget.setPropertyValue(GaugeModel.PROP_SHOW_HI, !Double.isNaN(meta.getWarnHigh()));

            widget.setPropertyValue(GaugeModel.PROP_LOLO_LEVEL, meta.getAlarmLow());
            widget.setPropertyValue(GaugeModel.PROP_SHOW_LOLO, !Double.isNaN(meta.getAlarmLow()));

            widget.setPropertyValue(GaugeModel.PROP_LO_LEVEL, meta.getWarnLow());
            widget.setPropertyValue(GaugeModel.PROP_SHOW_LO, !Double.isNaN(meta.getWarnLow()));
        }
    }
}
