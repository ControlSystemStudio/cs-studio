/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

import org.csstudio.sds.model.ActionType;
import org.csstudio.sds.model.properties.actions.AbstractWidgetActionModel;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * An adapter factory for Rules.
 *
 * @author Sven Wende
 *
 */
public final class WidgetActionAdapterFactory implements IAdapterFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Object adaptableObject,
            final Class adapterType) {
        assert adaptableObject != null;
        assert adapterType != null;
        assert adaptableObject instanceof AbstractWidgetActionModel : "adaptableObject instanceof WidgetAction"; //$NON-NLS-1$
        if (adapterType == IWorkbenchAdapter.class) {
            return new WorkbenchAdapter() {
                @Override
                public String getLabel(final Object o) {
                    final AbstractWidgetActionModel widgetAction = (AbstractWidgetActionModel)o;
                    String result = widgetAction.getActionLabel();
                    return result;
                }

                @Override
                public ImageDescriptor getImageDescriptor(final Object object) {
                    ActionType type = null;
                    if (object instanceof AbstractWidgetActionModel) {
                        type = ((AbstractWidgetActionModel)object).getType();
                    } else if (object instanceof ActionType) {
                        type = (ActionType) object;
                    }
                    if (type!=null) {
                        return CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
                                SdsUiPlugin.PLUGIN_ID,type.getIcon());
                    }
                    return null;
                }
            };
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class[] getAdapterList() {
        return new Class[] { IWorkbenchAdapter.class };
    }

}
