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
 package org.csstudio.sds.ui.internal.adapters;

import org.csstudio.auth.security.IActivationAdapter;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * Adapter factory for display widget models to switch their visibility.
 * @author Kai Meyer
 *
 */
public final class SdsActivationAdapterFactory implements IAdapterFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Object adaptableObject, final Class adapterType) {
        assert adaptableObject != null;
        assert adapterType != null;
        assert adaptableObject instanceof AbstractWidgetModel : "adaptableObject instanceof AbstractWidgetModel"; //$NON-NLS-1$

        if (adapterType == IActivationAdapter.class) {
            return new SdsWidgetEnabledAdapter();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class[] getAdapterList() {
        return new Class[] { IActivationAdapter.class };
    }

    /**
     * An IWidgetAdapter for AbstractWidgetModels, which sets the visibility.
     * @author Kai Meyer
     */
    private final class SdsWidgetEnabledAdapter implements IActivationAdapter {

        /**
         * {@inheritDoc}
         */
        @Override
        public void activate(final Object o, final boolean activate) {
            assert o instanceof AbstractWidgetModel : "adaptableObject instanceof AbstractWidgetModel"; //$NON-NLS-1$
            AbstractWidgetModel model = (AbstractWidgetModel) o;
            model.grantAccess(activate);
        }

    }

}
