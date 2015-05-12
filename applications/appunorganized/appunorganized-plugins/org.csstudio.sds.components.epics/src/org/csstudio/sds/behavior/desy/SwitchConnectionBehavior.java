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

import org.csstudio.sds.components.model.SwitchModel;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.MetaData;

/**
 *
 * Default DESY-Behavior for the {@link SwitchModel} widget with Connection state
 *
 * @author hrickens
 * @author $Author: jhatje $
 * @version $Revision: 1.1 $
 * @since 20.04.2010
 */
public class SwitchConnectionBehavior extends AbstractDesyConnectionBehavior<SwitchModel> {

    private boolean _defTransparent;

    /**
     * Constructor.
     */
    public SwitchConnectionBehavior() {
        // add Invisible Property Id here
        // addInvisiblePropertyId
        addInvisiblePropertyId(SwitchModel.PROP_STATE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInitialize(SwitchModel widget) {
        _defTransparent = widget.getBooleanProperty(SwitchModel.PROP_TRANSPARENT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcessValueChange(SwitchModel model, AnyData anyData) {
        super.doProcessValueChange(model, anyData);
        long value = anyData.longValue();
        model.setPropertyValue(SwitchModel.PROP_STATE, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcessConnectionStateChange(SwitchModel widget, AnyDataChannel anyDataChannel) {
        super.doProcessConnectionStateChange(widget, anyDataChannel);
        boolean isTransparent = isConnected(anyDataChannel)&&_defTransparent;
        widget.setPropertyValue(SwitchModel.PROP_TRANSPARENT, isTransparent);

    }

    @Override
    protected void doProcessMetaDataChange(final SwitchModel widget, final MetaData metaData) {
        // do noting
    }
}
