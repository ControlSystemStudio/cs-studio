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

import java.util.HashSet;
import java.util.Set;

import org.csstudio.sds.components.model.TextInputModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.Severity;

/**
 * TODO (hrickens) :
 *
 * TODO: Setzen der Cursereigenschaften (CA)
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.9 $
 * @since 19.04.2010
 *
 * @param <W>
 *            The Widget model that have this Behavior
 */
public abstract class AbstractDesyConnectionBehavior<W extends AbstractWidgetModel> extends
        AbstractDesyBehavior<W> {

    private final Set<String> _invisiblePropertyIds = new HashSet<String>();
    private String _userSetColor = "";

    /**
     * Constructor.
     */
    public AbstractDesyConnectionBehavior() {
        addInvisiblePropertyId(AbstractWidgetModel.PROP_NAME);
        // FIXME: How needs to disable PROP_COLOR_BACKGROUND
//        addInvisiblePropertyId(AbstractWidgetModel.PROP_COLOR_BACKGROUND);
        addInvisiblePropertyId(AbstractWidgetModel.PROP_CROSSED_OUT);
        addInvisiblePropertyId(TextInputModel.PROP_CURSOR);
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    protected String[] doGetInvisiblePropertyIds() {
        return _invisiblePropertyIds.toArray(new String[0]);
    }

    /**
     *
     * @param id Invisible Property Id
     */
    protected final void addInvisiblePropertyId( final String id) {
        _invisiblePropertyIds.add(id);
    }

    protected final void removeInvisiblePropertyId( final String id) {
        _invisiblePropertyIds.remove(id);
    }

    @Override
    protected void doInitialize( final W widget) {
        _userSetColor = widget.getColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND);
        widget.setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND,
                                determineBorderColor(ConnectionState.INITIAL));
    }

    @Override
    protected void doProcessConnectionStateChange( final W widget,
                                                  final AnyDataChannel anyDataChannel) {
        final String determineBackgroundColor;
        final ConnectionState connectionState = anyDataChannel.getProperty().getConnectionState();
        if(isConnected(anyDataChannel)) {
            if(hasValue(anyDataChannel)) {
                determineBackgroundColor = _userSetColor;
            } else {
                determineBackgroundColor = "${Invalid}";
            }
        } else {
            determineBackgroundColor = determineBackgroundColor(connectionState);
        }
        widget.setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND, determineBackgroundColor);
        final Severity severity = anyDataChannel.getData().getSeverity();
        if ( severity != null && severity.isInvalid()) {
            widget.setPropertyValue(AbstractWidgetModel.PROP_CROSSED_OUT, true);
        } else {
            widget.setPropertyValue(AbstractWidgetModel.PROP_CROSSED_OUT, false);
        }
    }

    /**
     * @param connectionState
     * @return
     */
    protected boolean isConnected(final AnyDataChannel anyDataChannel) {
        final ConnectionState connectionState = anyDataChannel.getProperty().getConnectionState();
        return connectionState != null && (connectionState == ConnectionState.CONNECTED || connectionState == ConnectionState.OPERATIONAL);
    }

    @Override
    protected void doProcessValueChange( final W model, final AnyData anyData) {
        // Do nothing
    }

    /**
     * @param arguments
     * @return
     */
    protected String getColorFromDigLogColorRule(final AnyData anyData) {
        String color = "${Illegal}";
        if (anyData != null) {
            int arguments = -1;
            if (anyData.numberValue() != null) {
                arguments = anyData.numberValue().intValue();
            }
            if (arguments >= 15 || !hasValue(anyData)) {
                color = "${Invalid}";
            } else if (arguments >= 3) {
                color = "${Minor}";
            } else if (arguments >= 2) {
                color = "${Offen}";
            } else if (arguments >= 1) {
                color = "${Geregelt}";
            } else if (arguments >= 0) {
                color = "${Zu}";
            }
        }
        return color;
    }

    protected String getColorFromAlarmColorRule(final AnyData anyData) {
        String color = "${Illegal}";
        if (anyData != null) {
            int arguments = -1;
            if (anyData.numberValue() != null) {
                arguments = anyData.numberValue().intValue();
            }
            if (!hasValue(anyData)) {
                color = "${Invalid}";
            } else if (arguments > 3) {
                color = "${Illegal}";
            } else if (arguments >= 3) {
                color = "${Invalid}";
            } else if (arguments >= 2) {
                color = "${Major}";
            } else if (arguments >= 1) {
                color = "${Minor}";
            } else if (arguments >= 0) {
                color = "${NoAlarm}";
            }
        }
        return color;
    }

    protected String getColorFromOperatingRule(final AnyData anyData) {
        String color = "${Illegal}";
        int arguments = -1;
        if( anyData != null) {
            final Number numberValue = anyData.numberValue();
            if(numberValue!=null) {
                arguments = numberValue.intValue();
            }
            if(!hasValue(anyData)) {
                color = "${Invalid}";
            } else if(arguments == 1) {
                color = "${Betrieb}";
            } else if(arguments == 0) {
                color = "${KeinBetrieb}";
            }
        }
        return color;
    }



    /**
     * @param anyData
     * @return
     */
    private boolean hasValue(final AnyData anyData) {
        return hasValue(anyData.getParentChannel());
    }

    /**
     * @param anyDataChannel
     * @return
     */
    protected boolean hasValue(final AnyDataChannel anyDataChannel) {
        return anyDataChannel.getProperty().getCondition().containsAllStates(DynamicValueState.HAS_LIVE_DATA);
    }

}
