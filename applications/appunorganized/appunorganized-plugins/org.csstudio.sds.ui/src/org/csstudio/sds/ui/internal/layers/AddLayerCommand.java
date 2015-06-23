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
import org.csstudio.sds.internal.model.LayerSupport;
import org.eclipse.gef.commands.Command;

/**
 * Command that adds a layer.
 *
 * @author swende
 *
 */
final class AddLayerCommand extends Command {
    /**
     * Access object to the layer model.
     */
    private LayerSupport _layerSupport;

    /**
     * The layer that is added.
     */
    private Layer _layer;

    /**
     * The index position of the added layer.
     */
    private int _index;

    /**
     * Constructor.
     *
     * @param layerSupport
     *            acces object to the layer model
     * @param layer
     *            the layer that is about to be added
     * @param index
     *            the index position of the added layer
     */
    public AddLayerCommand(final LayerSupport layerSupport, final Layer layer,
            final int index) {
        assert layerSupport != null;
        assert layer != null;
        setLabel("Add Layer");
        _layerSupport = layerSupport;
        _layer = layer;
        _index = index;
    }

    /**
     * Constructor.
     *
     * @param layerSupport
     *            acces object to the layer model
     * @param layer
     *            the layer that is about to be added
     */
    public AddLayerCommand(final LayerSupport layerSupport, final Layer layer) {
        assert layerSupport != null;
        assert layer != null;
        _layerSupport = layerSupport;
        _layer = layer;
        _index = -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        if (_index > -1) {
            _layerSupport.addLayer(_layer, _index);
        } else {
            _layerSupport.addLayer(_layer);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        _layerSupport.removeLayer(_layer);
    }

}
