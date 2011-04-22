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
import org.csstudio.sds.model.LabelModel;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.AnyDataChannel;
import org.epics.css.dal.simple.Severity;

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

    private String _normalBackgroundColor;
    private final Set<String> _invisiblePropertyIds = new HashSet<String>();

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
        _normalBackgroundColor = widget.getPropertyInternal(LabelModel.PROP_COLOR_BACKGROUND)
                .getPropertyValue().toString();
        widget.setPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND,
                                determineBorderColor(ConnectionState.INITIAL));
    }

    @Override
    protected void doProcessConnectionStateChange( final W widget,
                                                  final AnyDataChannel anyDataChannel) {
        ConnectionState connectionState = anyDataChannel.getProperty().getConnectionState();
        if ( (connectionState != null) && (connectionState == ConnectionState.CONNECTED)) {
            widget.setPropertyValue(LabelModel.PROP_COLOR_BACKGROUND, _normalBackgroundColor);
        } else {
            widget.setPropertyValue(LabelModel.PROP_COLOR_BACKGROUND,
                                    determineBackgroundColor(connectionState));
        }
    }

    @Override
    protected void doProcessValueChange( final W model, final AnyData anyData) {
        Severity severity = anyData.getSeverity();
        if ( (severity != null) && severity.isInvalid()) {
            model.setPropertyValue(AbstractWidgetModel.PROP_CROSSED_OUT, true);
        } else {
            model.setPropertyValue(AbstractWidgetModel.PROP_CROSSED_OUT, false);
        }
    }

}
