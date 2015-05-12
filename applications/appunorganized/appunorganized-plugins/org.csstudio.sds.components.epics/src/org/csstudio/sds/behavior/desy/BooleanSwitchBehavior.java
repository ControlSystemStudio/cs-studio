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

import org.csstudio.sds.components.model.BooleanSwitchModel;
import org.csstudio.sds.cursorservice.CursorService;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.MetaData;

public class BooleanSwitchBehavior extends AbstractDesyConnectionBehavior<BooleanSwitchModel> {

    private String _defOnColor;
    private String _defOffColor;

    @Override
    protected String[] doGetInvisiblePropertyIds() {
        return new String[] {BooleanSwitchModel.PROP_VALUE};
    }

    @Override
    protected void doInitialize(final BooleanSwitchModel widget) {
        _defOnColor = widget.getColor(BooleanSwitchModel.PROP_ON_COLOR);
        _defOffColor = widget.getColor(BooleanSwitchModel.PROP_OFF_COLOR);
    }

    @Override
    protected void doProcessConnectionStateChange(final BooleanSwitchModel widget,
                                                  final AnyDataChannel anyDataChannel) {
        String onColor;
        String offColor;
        if(isConnected(anyDataChannel)) {
            if(hasValue(anyDataChannel)) {
                onColor = _defOnColor;
                offColor = _defOffColor;
            } else {
                onColor = "${Invalid}";
                offColor = "${Invalid}";
            }
        } else {
            final ConnectionState connectionState = anyDataChannel.getProperty().getConnectionState();
            onColor = determineBackgroundColor(connectionState);
            offColor = onColor;
        }
        widget.setPropertyValue(BooleanSwitchModel.PROP_ON_COLOR, onColor);
        widget.setPropertyValue(BooleanSwitchModel.PROP_OFF_COLOR, offColor);
    }

    @Override
    protected void doProcessMetaDataChange(final BooleanSwitchModel widget, final MetaData metaData) {
        if(metaData != null) {
            switch (metaData.getAccessType()) {
                case NONE:
                    widget.setPropertyValue(AbstractWidgetModel.PROP_CURSOR, CursorService
                            .getInstance().availableCursors().get(7));
                    break;
                case READ:
                    widget.setPropertyValue(AbstractWidgetModel.PROP_CURSOR, CursorService
                            .getInstance().availableCursors().get(7));
                    break;
                case READ_WRITE:
                    widget.setPropertyValue(AbstractWidgetModel.PROP_CURSOR, CursorService
                            .getInstance().availableCursors().get(0));
                    break;
                case WRITE:
                    widget.setPropertyValue(AbstractWidgetModel.PROP_CURSOR, CursorService
                            .getInstance().availableCursors().get(0));
                    break;
                default:
                    widget.setPropertyValue(AbstractWidgetModel.PROP_CURSOR, CursorService
                            .getInstance().availableCursors().get(0));
            }
        }
    }

    @Override
    protected void doProcessValueChange(final BooleanSwitchModel model, final AnyData anyData) {
        super.doProcessValueChange(model, anyData);
        // .. value (influenced by current value, depending on onTrue Value)
        final double value = anyData.doubleValue();
        final boolean b = value == model.getDoubleProperty(BooleanSwitchModel.PROP_ON_STATE_VALUE);
        model.setPropertyValue(BooleanSwitchModel.PROP_VALUE, b);
    }

    @Override
    protected Object doConvertOutgoingValue(final BooleanSwitchModel widgetModel,
                                            final String propertyId,
                                            final Object value) {
        if(propertyId.equals(BooleanSwitchModel.PROP_VALUE)) {
            final boolean currentValue = widgetModel.getBooleanProperty(BooleanSwitchModel.PROP_VALUE);
            double outgoingValue = widgetModel
                    .getDoubleProperty(BooleanSwitchModel.PROP_OFF_STATE_VALUE);
            if(currentValue) {
                outgoingValue = widgetModel
                        .getDoubleProperty(BooleanSwitchModel.PROP_ON_STATE_VALUE);
            }
            return outgoingValue;
        } else {
            return super.doConvertOutgoingValue(widgetModel, propertyId, value);
        }
    }

}
