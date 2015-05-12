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

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.Severity;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.6 $
 * @since 19.04.2010
 *
 * @param <W>
 *            The Widget model that have this Behavior
 */
public abstract class AbstractDesyAlarmBehavior<W extends AbstractWidgetModel> extends
        AbstractDesyConnectionBehavior<W> {

    private String _defColor;

    /**
     * Constructor.
     */
    public AbstractDesyAlarmBehavior() {
        addInvisiblePropertyId(AbstractWidgetModel.PROP_BORDER_COLOR);
        addInvisiblePropertyId(AbstractWidgetModel.PROP_BORDER_STYLE);
        addInvisiblePropertyId(AbstractWidgetModel.PROP_BORDER_WIDTH);
    }

    @Override
    protected void doInitialize(final W widget) {
        super.doInitialize(widget);
    }

    @Override
    protected void doProcessConnectionStateChange(final W widget,
                                                  final AnyDataChannel anyDataChannel) {
        super.doProcessConnectionStateChange(widget, anyDataChannel);
        final AnyData anyData = anyDataChannel.getData();
        final Severity severity = anyData.getSeverity();
        if(severity != null) {
            widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_COLOR,
                                    determineColorBySeverity(severity, _defColor));
            widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_STYLE,
                                    determineBorderStyleBySeverity(severity).getIndex());
            widget.setPropertyValue(AbstractWidgetModel.PROP_BORDER_WIDTH,
                                    determineBorderWidthBySeverity(severity));
        }
    }

}