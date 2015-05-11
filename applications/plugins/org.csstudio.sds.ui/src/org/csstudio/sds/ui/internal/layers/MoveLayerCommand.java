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
 * A Command to move a Layer.
 *
 * @author Kai Meyer
 */
public final class MoveLayerCommand extends Command {

    /**
     * Access object to the layer model.
     */
    private LayerSupport _layerSupport;

    /**
     * The layer that is removed.
     */
    private Layer _layer;

    /**
     * The former index position of the removed layer.
     */
    private int _oldIndex;

    /**
     * The new index position of the removed layer.
     */
    private int _newIndex;

    /**
     * Constructor.
     *
     * @param layerSupport
     *            access object to the layer model
     * @param layer
     *            the layer that is about to be removed
     * @param newIndex
     *               the new index for the layer
     */
    public MoveLayerCommand(final LayerSupport layerSupport, final Layer layer, final int newIndex) {
        assert layerSupport != null;
        assert layer != null;
        setLabel("Move Layer '"+layer.getDescription()+"'");
        _layerSupport = layerSupport;
        _layer = layer;
        _newIndex = newIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        _oldIndex = _layerSupport.getLayerIndex(_layer);
        _layerSupport.changeLayerPosition(_layer, _newIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        _layerSupport.changeLayerPosition(_layer, _oldIndex);
    }

}
