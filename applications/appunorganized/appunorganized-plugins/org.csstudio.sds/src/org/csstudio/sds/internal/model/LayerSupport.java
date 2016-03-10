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
/**
*
*/
package org.csstudio.sds.internal.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.model.ContainerModel;

/**
 * Encapsulates layer relevant model information.
 *
 * @author swende
 *
 */
public final class LayerSupport implements PropertyChangeListener {
    /**
     * ID for <i>add layer</i> events.
     */
    public static final String PROP_LAYER_ADDED = "PROP_LAYER_ADDED";

    /**
     * ID for <i>remove layer</i> events.
     */
    public static final String PROP_LAYER_REMOVED = "PROP_LAYER_REMOVED";

    /**
     * ID for <i>moved layer</i> events.
     */
    public static final String PROP_LAYER_MOVED = "PROP_LAYER_MOVED";

    /**
     * ID for <i>layer detail</i> events.
     */
    public static final String PROP_LAYER_DETAILS = "PROP_LAYER_DETAILS";

    /**
     * The default name for a Layer.
     */
    public static final String DEFAULT_NAME = "DEFAULT";

    /**
     * The id of the default layer.
     */
    public static final String DEFAULT_LAYER_ID = "DEFAULT_LAYER_ID";

    /**
     * A list that contains all layers.
     */
    private List<Layer> _layers;

    /**
     * Contains all layer model listeners.
     */
    private List<ILayerModelListener> _listeners;

    /**
     * The default layer. (Will always exist and cannot be deleted).
     */
    private Layer _defaultLayer;

    /**
     * The layer that is marked as active layer.
     */
    private Layer _activeLayer;
    /**
     * The {@link ContainerModel} of this {@link LayerSupport}.
     */
    private final ContainerModel _parent;

    /**
     * Constructor.
     * @param parent
     *         The parent for this {@link LayerSupport}
     */
    public LayerSupport(final ContainerModel parent) {
        _parent = parent;
        _layers = new ArrayList<Layer>();
        _listeners = new ArrayList<ILayerModelListener>();

        // add default layer
        _defaultLayer = new Layer(DEFAULT_LAYER_ID, DEFAULT_NAME);
        doAddLayer(_defaultLayer, 0);

        // mark default layer as active layer
        _activeLayer = _defaultLayer;
    }

    /**
     * Returns the parent of this {@link LayerSupport}.
     * @return The parent
     */
    public ContainerModel getParent() {
        return _parent;
    }

    /**
     * Adds a layer model listener.
     *
     * @param listener
     *            the listener
     */
    public void addLayerModelListener(final ILayerModelListener listener) {
        if (!_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

    /**
     * Removes a layer model listener.
     *
     * @param listener
     *            the listener
     */
    public void removeLayerModelListener(final ILayerModelListener listener) {
        if (_listeners.contains(listener)) {
            _listeners.remove(listener);
        }
    }

    /**
     * Adds the specified layer to the layer model.
     *
     * @param layer
     *            the layer
     */
    public void addLayer(final Layer layer) {
        doAddLayer(layer, _layers.size());
    }

    /**
     * Adds the specified layer to the layer model using a certain insert
     * position.
     *
     * @param layer
     *            the layer
     * @param index
     *            the index of the insert position for the new layer
     */
    public void addLayer(final Layer layer, final int index) {
        doAddLayer(layer, index);
    }

    /**
     * Removes the specified layer.
     *
     * @param layer
     *            the layer
     */
    public void removeLayer(final Layer layer) {
        if (_layers.contains(layer) && layer != _defaultLayer) {
            _layers.remove(layer);
            layer.removePropertyChangeListener(this);
            fireLayerModelChangeEvent(layer, PROP_LAYER_REMOVED);
        }
    }

    /**
     * Gets the index of the specified layer.
     *
     * @param layer
     *            the layer
     * @return the index position of the specified layer
     */
    public int getLayerIndex(final Layer layer) {
        return _layers.indexOf(layer);
    }

    /**
     * Returns the default layer.
     *
     * @return the default layer
     */
    public Layer getDefaultLayer() {
        return _defaultLayer;
    }

    /**
     * Sets the active layer.
     *
     * @param layer
     *            the active layer
     */
    public void setActiveLayer(final Layer layer) {
        assert layer != null;
        assert _layers.contains(layer) : "_layers.contains(layer)";
        _activeLayer = layer;
    }

    /**
     * Returns the active layer.
     *
     * @return the active layer
     */
    public Layer getActiveLayer() {
        return _activeLayer;
    }

    /**
     * Returns all layers.
     *
     * @return all layers
     */
    public List<Layer> getLayers() {
        return new ArrayList<Layer>(_layers);
    }

    /**
     * Changes the index position of the specified layer.
     *
     * @param layer
     *            the layer
     * @param newPos
     *            the new index position
     */
    public void changeLayerPosition(final Layer layer, final int newPos) {
        assert layer != null;
        assert _layers.contains(layer) : "_layers.contains(layer)";
        assert newPos >= 0 : "newPos>=0";
        assert newPos < _layers.size() : "newPos<_layers.size()";

        _layers.remove(layer);
        _layers.add(newPos, layer);

        fireLayerModelChangeEvent(layer, PROP_LAYER_MOVED);
    }

    /**
     * Tries to find a layer with the specified id.
     *
     * @param layerId
     *            the layer id
     * @return a layer with that id or null if no layer was found
     */
    public Layer findLayer(final String layerDescription) {
        assert layerDescription != null;
        boolean found = false;
        Layer result = _defaultLayer;
        for (Layer layer : _layers) {
            if (layer.getId().equals(layerDescription)) {
                result = layer;
                found = true;
            }
        }
        if (!found) {
            for (Layer layer : _layers) {
                if (layer.getDescription().equals(layerDescription)) {
                    result = layer;
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        fireLayerModelChangeEvent((Layer) evt.getSource(), evt.getPropertyName());
    }

    /**
     * Adds the specified layer to the model.
     *
     * @param layer
     *            the layer
     * @param index
     *            the insert position
     */
    private void doAddLayer(final Layer layer, final int index) {
        int layerIndex = index;
        if (layer.getId().equals(DEFAULT_LAYER_ID)) {
            //            Layer oldDefaultLayer = _defaultLayer;
            layerIndex = Math.max(getLayerIndex(_defaultLayer), 0);
            removeLayer(_defaultLayer);
            _defaultLayer = layer;
        }
        if (!_layers.contains(layer)) {
            _layers.add(layerIndex, layer);
            layer.addPropertyChangeListener(this);
            fireLayerModelChangeEvent(layer, PROP_LAYER_ADDED);
        }
    }

    /**
     * Fires a layer model change event, which is forwarded to all registered
     * {@link ILayerModelListener}.
     *
     * @param layer
     *            the layer that caused the event
     * @param property
     *            the property name which identifies the event type
     */
    private void fireLayerModelChangeEvent(final Layer layer, final String property) {
        for (ILayerModelListener l : _listeners) {
            l.layerChanged(layer, property);
        }
    }

    public boolean isLayerId(String layerDescription) {
        for (Layer layer : _layers) {
            if (layer.getId().equals(layerDescription)) {
                return true;
            }
        }
        return false;
    }

    public boolean isLayerName(String layerDescription) {
        for (Layer layer : _layers) {
            if (layer.getDescription().equals(layerDescription)) {
                return true;
            }
        }
        return false;
    }
}
