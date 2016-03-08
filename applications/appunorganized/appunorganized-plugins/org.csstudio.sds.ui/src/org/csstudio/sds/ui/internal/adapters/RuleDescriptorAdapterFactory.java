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

import org.csstudio.sds.internal.rules.RuleDescriptor;
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
public final class RuleDescriptorAdapterFactory implements IAdapterFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAdapter(final Object adaptableObject,
            final Class adapterType) {
        assert adaptableObject != null;
        assert adapterType != null;
        assert adaptableObject instanceof RuleDescriptor : "adaptableObject instanceof RuleDescriptor"; //$NON-NLS-1$

        final RuleDescriptor rule = (RuleDescriptor) adaptableObject;

        if (adapterType == IWorkbenchAdapter.class) {
            return new WorkbenchAdapter() {
                @Override
                public String getLabel(final Object o) {
                    String result = rule.getDescription();

                    if (rule.isScriptedRule()) {
                        result += " (ECMA Script)"; //$NON-NLS-1$
                    } else {
                        result += " (Java)"; //$NON-NLS-1$
                    }

                    return result;
                }

                @Override
                public ImageDescriptor getImageDescriptor(final Object object) {
                    // fallback
                    ImageDescriptor result = null;

                    if (rule.isScriptedRule()) {
                        result = CustomMediaFactory.getInstance()
                                .getImageDescriptorFromPlugin(
                                        SdsUiPlugin.PLUGIN_ID,
                                        "icons/rule_script.gif"); //$NON-NLS-1$
                    } else {
                        result = CustomMediaFactory.getInstance()
                                .getImageDescriptorFromPlugin(
                                        SdsUiPlugin.PLUGIN_ID,
                                        "icons/rule_java.gif"); //$NON-NLS-1$

                    }

                    return result;
                }
            };
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class[] getAdapterList() {
        return new Class[] { IWorkbenchAdapter.class };
    }

}
