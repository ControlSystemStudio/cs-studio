package org.csstudio.sds.model.layers;

/**
 * Layer model listeners can listen to changes of {@link LayerSupport} model.
 * 
 * @author swende
 * 
 */
public interface ILayerModelListener {
	/**
	 * Callback method, which informs the listener that a certain layer has
	 * changed.
	 * 
	 * @param layer
	 *            the layer that changed
	 * @param property
	 *            the event identifier (one of the constants defined in
	 *            {@link LayerSupport}
	 */
	void layerChanged(Layer layer, String property);
}
