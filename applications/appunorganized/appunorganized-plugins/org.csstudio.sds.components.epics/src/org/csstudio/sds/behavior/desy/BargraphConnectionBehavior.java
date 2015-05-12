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

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.MetaData;

/**
 *
 * Default DESY-Behavior for the {@link BargraphModel} widget with Connection state.
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.4 $
 * @since 20.04.2010
 */
public class BargraphConnectionBehavior extends AbstractDesyConnectionBehavior<BargraphModel> {

    private final Map<ConnectionState, Boolean> _transparencyByConnectionState;

    /**
     * Constructor.
     */
    public BargraphConnectionBehavior() {
        _transparencyByConnectionState = new HashMap<ConnectionState, Boolean>();
        _transparencyByConnectionState.put(ConnectionState.CONNECTED, true);
        _transparencyByConnectionState.put(ConnectionState.OPERATIONAL, true);
        _transparencyByConnectionState.put(ConnectionState.CONNECTION_LOST, false);
        _transparencyByConnectionState.put(ConnectionState.INITIAL, false);
        // add Invisible Property Id here
        addInvisiblePropertyId(BargraphModel.PROP_MIN);
        addInvisiblePropertyId(BargraphModel.PROP_MAX);
        addInvisiblePropertyId(BargraphModel.PROP_HIHI_LEVEL);
        addInvisiblePropertyId(BargraphModel.PROP_HI_LEVEL);
        addInvisiblePropertyId(BargraphModel.PROP_LOLO_LEVEL);
        addInvisiblePropertyId(BargraphModel.PROP_LO_LEVEL);
        addInvisiblePropertyId(BargraphModel.PROP_DEFAULT_FILL_COLOR);
        addInvisiblePropertyId(BargraphModel.PROP_FILLBACKGROUND_COLOR);
        addInvisiblePropertyId(BargraphModel.PROP_FILL);
        addInvisiblePropertyId(BargraphModel.PROP_TRANSPARENT);
        addInvisiblePropertyId(AbstractWidgetModel.PROP_ACTIONDATA);
        addInvisiblePropertyId(AbstractWidgetModel.PROP_BORDER_STYLE);

    }

    @Override
    protected void doInitialize(final BargraphModel widget) {
        // .. border
        widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE,
                                determineBorderStyle(ConnectionState.INITIAL));
        widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH,
                                determineBorderWidth(ConnectionState.INITIAL));
        widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR,
                                determineBorderColor(ConnectionState.INITIAL));
    }

    @Override
    protected void doProcessValueChange(final BargraphModel widget, final AnyData anyData) {
        super.doProcessValueChange(widget, anyData);
        // .. fill level (influenced by current value)
        widget.setPropertyValue(BargraphModel.PROP_FILL, anyData.doubleValue());



//        // TODO (hrickens): Is only for a simple test environment. Bad style!
//        final ConnectionState connectionState = anyData.getParentProperty().getConnectionState();
//        // .. border
//        widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE,
//                determineBorderStyle(connectionState));
//        widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH,
//                determineBorderWidth(connectionState));
//        widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR,
//                determineBorderColor(connectionState));
//        // .. background colors
//        final String determineBackgroundColor;
//        if(isConnected(anyData.getParentChannel())) {
//            if(hasValue(anyData.getParentChannel())) {
//                determineBackgroundColor = widget.getColor(BargraphModel.PROP_FILLBACKGROUND_COLOR);
//            } else {
//                determineBackgroundColor = "${Invalid}";
//            }
//        } else {
//            determineBackgroundColor = determineBackgroundColor(connectionState);
//        }
//        widget.setPropertyValue(BargraphModel.PROP_FILLBACKGROUND_COLOR,
//                determineBackgroundColor);
//        widget.setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND,
//                determineBackgroundColor);
//
//        // .. transparency
//        final Boolean transparent = getTransperancyFromConnectionState(anyData.getParentChannel());
//
//        if (transparent != null) {
//            widget.setPropertyValue(BargraphModel.PROP_TRANSPARENT, transparent);
//        }

    }

    @Override
    protected void doProcessConnectionStateChange(final BargraphModel widget,
            final AnyDataChannel anyDataChannel) {
        final ConnectionState connectionState = anyDataChannel.getProperty().getConnectionState();
        // .. border
        widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE,
                determineBorderStyle(connectionState));
        widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH,
                determineBorderWidth(connectionState));
        widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR,
                determineBorderColor(connectionState));

        // .. background colors
        final String determineBackgroundColor;
        if(isConnected(anyDataChannel)) {
            if(hasValue(anyDataChannel)) {
                determineBackgroundColor = widget.getColor(BargraphModel.PROP_FILLBACKGROUND_COLOR);
            } else {
                determineBackgroundColor = "${Invalid}";
            }
        } else {
            determineBackgroundColor = determineBackgroundColor(connectionState);
        }
        widget.setPropertyValue(BargraphModel.PROP_FILLBACKGROUND_COLOR,
                determineBackgroundColor);
        widget.setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND,
                determineBackgroundColor);

        // .. transparency
        final Boolean transparent = getTransperancyFromConnectionState(anyDataChannel);

        if (transparent != null) {
            widget.setPropertyValue(BargraphModel.PROP_TRANSPARENT, transparent);
        }
    }

    /**
     * @param anyDataChannel
     * @return
     */
    private Boolean getTransperancyFromConnectionState(final AnyDataChannel anyDataChannel) {
        Boolean isTranc = _transparencyByConnectionState.get(anyDataChannel.getProperty().getConnectionState());
        if(isTranc==null) {
            isTranc=false;
        }
        isTranc &= hasValue(anyDataChannel);
        return isTranc;
    }

    @Override
    protected void doProcessMetaDataChange(final BargraphModel widget, final MetaData meta) {
        if (meta != null) {
            // .. limits
            widget.setPropertyValue(BargraphModel.PROP_MIN, meta.getDisplayLow());
            widget.setPropertyValue(BargraphModel.PROP_MAX, meta.getDisplayHigh());
            widget.setPropertyValue(BargraphModel.PROP_HIHI_LEVEL, meta.getAlarmHigh());
            widget.setPropertyValue(BargraphModel.PROP_HI_LEVEL, meta.getWarnHigh());
            widget.setPropertyValue(BargraphModel.PROP_LOLO_LEVEL, meta.getAlarmLow());
            widget.setPropertyValue(BargraphModel.PROP_LO_LEVEL, meta.getWarnLow());
        }
    }}
