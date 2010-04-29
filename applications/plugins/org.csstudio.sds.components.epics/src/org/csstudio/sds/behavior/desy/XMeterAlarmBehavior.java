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

import org.csstudio.sds.components.model.XMeterModel;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;

/**
 *
 * Default DESY-Behavior for the {@link XMeterModel} widget with Connection state and Alarms.
 *
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 20.04.2010
 */
public class XMeterAlarmBehavior extends AbstractDesyAlarmBehavior<XMeterModel> {

    /**
     * Constructor.
     */
    public XMeterAlarmBehavior() {
        // add Invisible Property Id here
        addInvisiblePropertyId(XMeterModel.PROP_MIN);
        addInvisiblePropertyId(XMeterModel.PROP_MAX);
        addInvisiblePropertyId(XMeterModel.PROP_HIHI_LEVEL);
        addInvisiblePropertyId(XMeterModel.PROP_HI_LEVEL);
        addInvisiblePropertyId(XMeterModel.PROP_LOLO_LEVEL);
        addInvisiblePropertyId(XMeterModel.PROP_LO_LEVEL);
        addInvisiblePropertyId(XMeterModel.PROP_TRANSPARENT);
        addInvisiblePropertyId(XMeterModel.PROP_ACTIONDATA);
        addInvisiblePropertyId(XMeterModel.PROP_BORDER_STYLE);
        addInvisiblePropertyId(XMeterModel.PROP_SHOW_HI);
        addInvisiblePropertyId(XMeterModel.PROP_SHOW_HIHI);
        addInvisiblePropertyId(XMeterModel.PROP_SHOW_LO);
        addInvisiblePropertyId(XMeterModel.PROP_SHOW_LOLO);
        addInvisiblePropertyId(XMeterModel.PROP_VALUE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcessValueChange(final XMeterModel model, final AnyData anyData) {
        super.doProcessValueChange(model, anyData);
        // .. fill level (influenced by current value)
        model.setPropertyValue(XMeterModel.PROP_VALUE, anyData.numberValue());
    }

    @Override
    protected void doProcessMetaDataChange(final XMeterModel widget, final MetaData meta) {
        if (meta != null) {
            // .. limits
            widget.setPropertyValue(XMeterModel.PROP_MIN, meta.getDisplayLow());
            widget.setPropertyValue(XMeterModel.PROP_MAX, meta.getDisplayHigh());

            widget.setPropertyValue(XMeterModel.PROP_HIHI_LEVEL, meta.getAlarmHigh());
            widget.setPropertyValue(XMeterModel.PROP_SHOW_HIHI, !Double.isNaN(meta.getAlarmHigh()));

            widget.setPropertyValue(XMeterModel.PROP_HI_LEVEL, meta.getWarnHigh());
            widget.setPropertyValue(XMeterModel.PROP_SHOW_HI, !Double.isNaN(meta.getWarnHigh()));

            widget.setPropertyValue(XMeterModel.PROP_LOLO_LEVEL, meta.getAlarmLow());
            widget.setPropertyValue(XMeterModel.PROP_SHOW_LOLO, !Double.isNaN(meta.getAlarmLow()));

            widget.setPropertyValue(XMeterModel.PROP_LO_LEVEL, meta.getWarnLow());
            widget.setPropertyValue(XMeterModel.PROP_SHOW_LO, !Double.isNaN(meta.getWarnLow()));
        }
    }

}
