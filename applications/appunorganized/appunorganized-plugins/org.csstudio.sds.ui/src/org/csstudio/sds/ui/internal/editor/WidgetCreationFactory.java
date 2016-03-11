/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.ui.internal.editor;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.eventhandling.EventType;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetModelFactoryService;
import org.eclipse.gef.requests.CreationFactory;

/**
 * {@link CreationFactory} implementation for new widget types.
 *
 * @author Kai Meyer, Sven Wende
 */
public final class WidgetCreationFactory implements CreationFactory {

    private String _widgetType = null;
    private final KeyListenerAdapter _keyAdapter;

    public WidgetCreationFactory(final String widgetType) {
        this(widgetType, null);
    }

    public WidgetCreationFactory(final String widgetType, final KeyListenerAdapter keyAdapter) {
        _widgetType = widgetType;
        _keyAdapter = keyAdapter;
    }

    private boolean skipPreconfiguration() {
        return _keyAdapter != null && _keyAdapter.getPressedKeys().size() == 1 && _keyAdapter.getPressedKeys().get(0) == 'b';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getNewObject() {
        AbstractWidgetModel widgetModel = null;

        if (skipPreconfiguration()) {
            widgetModel = WidgetCreationUtil.createWidgetHeadlessly(_widgetType, false);
            SdsPlugin.getDefault().getWidgetPropertyPostProcessingService().applyForAllProperties(widgetModel, EventType.ON_MANUAL_CHANGE);
        } else {
            widgetModel = WidgetCreationUtil.createAndPreconfigureWidget(_widgetType, null);
        }

        // Note: In case there is no widget (user may have cancelled the dialog)
        // we have to return a dummy object to prevent the configuration dialog
        // from popping up again.
        return widgetModel != null ? widgetModel : new Object();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getObjectType() {
        return WidgetModelFactoryService.getInstance().getWidgetModelType(_widgetType);
    }

}
