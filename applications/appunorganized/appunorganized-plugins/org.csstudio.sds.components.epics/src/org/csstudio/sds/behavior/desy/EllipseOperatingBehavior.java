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

import org.csstudio.sds.components.model.EllipseModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.MetaData;

/**
 *
 * Default DESY-Behavior for the {@link EllipseModel} widget with Connection state and Alarms.
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.3 $
 * @since 20.04.2010
 */
public class EllipseOperatingBehavior extends AbstractDesyAlarmBehavior<AbstractWidgetModel> {

    /**
     * Constructor.
     */
    public EllipseOperatingBehavior() {
        // add Invisible P0roperty Id here
        addInvisiblePropertyId(EllipseModel.PROP_FILL);
        addInvisiblePropertyId(EllipseModel.PROP_ORIENTATION);
        addInvisiblePropertyId(EllipseModel.PROP_TRANSPARENT);
        addInvisiblePropertyId(AbstractWidgetModel.PROP_COLOR_FOREGROUND);
        addInvisiblePropertyId(AbstractWidgetModel.PROP_COLOR_BACKGROUND);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInitialize(final AbstractWidgetModel widget) {
        super.doInitialize(widget);
        widget.setPropertyValue(EllipseModel.PROP_FILL, 100);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcessValueChange(final AbstractWidgetModel model, final AnyData anyData) {
        super.doProcessValueChange(model, anyData);
        model.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, getColorFromOperatingRule(anyData));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcessConnectionStateChange(final AbstractWidgetModel widget,
                                                  final AnyDataChannel anyDataChannel) {
        super.doProcessConnectionStateChange(widget, anyDataChannel);
        final ConnectionState connectionState = anyDataChannel.getProperty().getConnectionState();
        final String determineBackgroundColor = isConnected(anyDataChannel) ? getColorFromOperatingRule(anyDataChannel
                .getData()) : determineBackgroundColor(connectionState);
        widget.setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND, determineBackgroundColor);

    }


    @Override
    protected void doProcessMetaDataChange(final AbstractWidgetModel widget, final MetaData metaData) {
        // do nothing
    }

}
