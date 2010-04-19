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

import org.csstudio.sds.components.model.TextInputModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.LabelModel;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.Severity;

/**
 * TODO (hrickens) :
 *
 * TODO: Setzen der Cursereigenschaften (CA)
 *
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 19.04.2010
 */
public abstract class AbstractDesyConnectionBehavior<W extends AbstractWidgetModel> extends
        AbstractDesyBehavior<W> {

    private String _normalBackgroundColor;

    /**
     * (@inheritDoc)
     */
    @Override
    protected String[] doGetInvisiblePropertyIds() {
        return new String[] { AbstractWidgetModel.PROP_NAME,
                AbstractWidgetModel.PROP_COLOR_BACKGROUND, AbstractWidgetModel.PROP_CROSSED_OUT,
                TextInputModel.PROP_CURSOR };
    }

    @Override
    protected void doInitialize(final W widget) {
        _normalBackgroundColor = widget.getPropertyInternal(LabelModel.PROP_COLOR_BACKGROUND)
                .getPropertyValue().toString();
    }

    @Override
    protected void doProcessConnectionStateChange(final W widget,
                                                  final ConnectionState connectionState) {
        if ( (connectionState != null) && (connectionState == ConnectionState.CONNECTED)) {
            widget.setPropertyValue(LabelModel.PROP_COLOR_BACKGROUND, _normalBackgroundColor);
        } else {
            widget.setPropertyValue(LabelModel.PROP_COLOR_BACKGROUND,
                                    determineBackgroundColor(connectionState));
        }
    }

    @Override
    protected void doProcessValueChange(final W model, final AnyData anyData) {
        Severity severity = anyData.getSeverity();
        if ( (severity != null) && severity.isInvalid()) {
            model.setPropertyValue(AbstractWidgetModel.PROP_CROSSED_OUT, true);
        } else {
            model.setPropertyValue(AbstractWidgetModel.PROP_CROSSED_OUT, false);
        }
    }

}
