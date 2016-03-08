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

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.ui.internal.properties.view.IPropertySource;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * Adapter factory for display widget models.
 *
 * @author Sven Wende
 *
 */
public final class WidgetModelAdapterFactory implements IAdapterFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAdapter(final Object adaptableObject, final Class adapterType) {
        assert adaptableObject != null;
        assert adapterType != null;
        assert adaptableObject instanceof AbstractWidgetModel : "adaptableObject instanceof AbstractWidgetModel"; //$NON-NLS-1$

        AbstractWidgetModel model = (AbstractWidgetModel) adaptableObject;

        if (adapterType == IPropertySource.class) {
            return new WidgetPropertySourceAdapter(model);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class[] getAdapterList() {
        return new Class[] { IPropertySource.class };
    }

}
