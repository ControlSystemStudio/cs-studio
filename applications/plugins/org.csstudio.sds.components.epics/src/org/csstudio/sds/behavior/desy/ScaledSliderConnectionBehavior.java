/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.components.model.ScaledSliderModel;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.MetaData;

/**
 * Default DESY-Behavior for the {@link ScaledSliderModel} widget with Connection state.
 *
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
public class ScaledSliderConnectionBehavior extends
        AbstractDesyConnectionBehavior<ScaledSliderModel> {

    /**
     * Constructor.
     */
    public ScaledSliderConnectionBehavior() {
        addInvisiblePropertyId(ScaledSliderModel.PROP_MIN);
        addInvisiblePropertyId(ScaledSliderModel.PROP_MAX);
        addInvisiblePropertyId(ScaledSliderModel.PROP_HIHI_LEVEL);
        addInvisiblePropertyId(ScaledSliderModel.PROP_SHOW_HIHI);
        addInvisiblePropertyId(ScaledSliderModel.PROP_HI_LEVEL);
        addInvisiblePropertyId(ScaledSliderModel.PROP_SHOW_HI);
        addInvisiblePropertyId(ScaledSliderModel.PROP_LOLO_LEVEL);
        addInvisiblePropertyId(ScaledSliderModel.PROP_SHOW_LOLO);
        addInvisiblePropertyId(ScaledSliderModel.PROP_LO_LEVEL);
        addInvisiblePropertyId(ScaledSliderModel.PROP_SHOW_LO);
        // addInvisiblePropertyId(ScaledSliderModel.PROP_DEFAULT_FILL_COLOR);
        addInvisiblePropertyId(ScaledSliderModel.PROP_FILLBACKGROUND_COLOR);
        addInvisiblePropertyId(ScaledSliderModel.PROP_VALUE);
        addInvisiblePropertyId(ScaledSliderModel.PROP_TRANSPARENT);
        addInvisiblePropertyId(ScaledSliderModel.PROP_ACTIONDATA);
        addInvisiblePropertyId(ScaledSliderModel.PROP_BORDER_STYLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcessValueChange(final ScaledSliderModel model, final AnyData anyData) {
        super.doProcessValueChange(model, anyData);
        model.setPropertyValue(ScaledSliderModel.PROP_VALUE, anyData.numberValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcessMetaDataChange(final ScaledSliderModel widget, final MetaData meta) {
        if (meta != null) {
         // .. limits
            widget.setPropertyValue(ScaledSliderModel.PROP_MIN, meta.getDisplayLow());
            widget.setPropertyValue(ScaledSliderModel.PROP_MAX, meta.getDisplayHigh());

            widget.setPropertyValue(ScaledSliderModel.PROP_HIHI_LEVEL, meta.getAlarmHigh());
            widget.setPropertyValue(ScaledSliderModel.PROP_SHOW_HIHI, !Double.isNaN(meta.getAlarmHigh()));

            widget.setPropertyValue(ScaledSliderModel.PROP_HI_LEVEL, meta.getWarnHigh());
            widget.setPropertyValue(ScaledSliderModel.PROP_SHOW_HI, !Double.isNaN(meta.getWarnHigh()));

            widget.setPropertyValue(ScaledSliderModel.PROP_LOLO_LEVEL, meta.getAlarmLow());
            widget.setPropertyValue(ScaledSliderModel.PROP_SHOW_LOLO, !Double.isNaN(meta.getAlarmLow()));

            widget.setPropertyValue(ScaledSliderModel.PROP_LO_LEVEL, meta.getWarnLow());
            widget.setPropertyValue(ScaledSliderModel.PROP_SHOW_LO, !Double.isNaN(meta.getWarnLow()));
        }
    }

}
