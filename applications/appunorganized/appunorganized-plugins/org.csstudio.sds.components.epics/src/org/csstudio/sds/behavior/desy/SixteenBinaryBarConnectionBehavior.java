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

import org.csstudio.sds.components.model.SixteenBinaryBarModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.MetaData;

/**
 *
 * Default DESY-Behavior for the {@link SixteenBinaryBarModel} widget with Connection state
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 20.04.2010
 */
public class SixteenBinaryBarConnectionBehavior extends
        AbstractDesyConnectionBehavior<SixteenBinaryBarModel> {

    private String _defOnColor;
    private String _defOffColor;

    /**
     * Constructor.
     */
    public SixteenBinaryBarConnectionBehavior() {
        // add Invisible Property Id here
        addInvisiblePropertyId(AbstractWidgetModel.PROP_ACTIONDATA);
        addInvisiblePropertyId(SixteenBinaryBarModel.PROP_VALUE);
        addInvisiblePropertyId(AbstractWidgetModel.PROP_COLOR_FOREGROUND);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInitialize(final SixteenBinaryBarModel widget) {
        _defOnColor = widget.getColor(SixteenBinaryBarModel.PROP_ON_COLOR);
        _defOffColor = widget.getColor(SixteenBinaryBarModel.PROP_OFF_COLOR);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcessValueChange(final SixteenBinaryBarModel model, final AnyData anyData) {
        super.doProcessValueChange(model, anyData);
        model.setPropertyValue(SixteenBinaryBarModel.PROP_VALUE, anyData.numberValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcessConnectionStateChange(final SixteenBinaryBarModel widget,
                                                  final AnyDataChannel anyDataChannel) {
        String onColor;
        String offColor;
        final ConnectionState connectionState = anyDataChannel.getProperty().getConnectionState();
        if(isConnected(anyDataChannel)) {
            if(hasValue(anyDataChannel)) {
                offColor = _defOffColor;
                onColor = _defOnColor;
            } else {
                offColor = "${Invalid}";
                onColor = "${Invalid}";
            }
        } else {
            offColor = determineBackgroundColor(connectionState);
            onColor = offColor;
        }
        widget.setPropertyValue(SixteenBinaryBarModel.PROP_ON_COLOR, onColor);
        widget.setPropertyValue(SixteenBinaryBarModel.PROP_OFF_COLOR, offColor);
    }

    @Override
    protected void doProcessMetaDataChange(final SixteenBinaryBarModel widget,
                                           final MetaData metaData) {

    }
}
