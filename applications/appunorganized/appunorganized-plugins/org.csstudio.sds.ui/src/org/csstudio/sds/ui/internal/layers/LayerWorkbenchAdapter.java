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
 package org.csstudio.sds.ui.internal.layers;

import org.csstudio.sds.internal.model.Layer;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Workbench adapter for layers.
 *
 * @author swende
 *
 */
final class LayerWorkbenchAdapter implements IWorkbenchAdapter {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getChildren(final Object o) {
        return new Object[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageDescriptor getImageDescriptor(final Object object) {
        Layer layer = (Layer) object;

        return CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
                SdsUiPlugin.PLUGIN_ID,
                layer.isVisible() ? "icons/layer_visible.png"
                        : "icons/layer_invisible.png");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabel(final Object o) {
        return ((Layer) o).getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getParent(final Object o) {
        return null;
    }
}
