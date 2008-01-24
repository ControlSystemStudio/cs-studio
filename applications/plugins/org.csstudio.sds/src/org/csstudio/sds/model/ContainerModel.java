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
package org.csstudio.sds.model;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.model.layers.LayerSupport;
import org.eclipse.core.runtime.IAdaptable;

/**
 * @author Sven Wende
 * 
 * @version $Revision$
 * 
 */
public abstract class ContainerModel extends AbstractWidgetModel implements
		IAdaptable {

	/**
	 * ID for <i>add child</i> events.
	 */
	public static final String PROP_CHILD_ADDED = "PROP_CHILD_ADDED"; //$NON-NLS-1$

	/**
	 * ID for <i>remove child</i> events.
	 */
	public static final String PROP_CHILD_REMOVED = "PROP_CHILD_REMOVED"; //$NON-NLS-1$

	/**
	 * ID for <i>order changed</i> events.
	 */
	public static final String PROP_ORDER_CHANGED = "PROP_ORDER_CHANGED"; //$NON-NLS-1$

	/**
	 * A list that contains all widgets.
	 */
	private List<AbstractWidgetModel> _widgets = new ArrayList<AbstractWidgetModel>();

	/**
	 * The name of the default Layer.
	 */
	public static final String DEFAULT_LAYER = "DEFAULT";

	/**
	 * Flag that signals that the model is currently being loaded.
	 */
	private boolean _loading = false;

	/**
	 * Encapsulats all layer information.
	 */
	private LayerSupport _layerSupport;

	/**
	 * Standard constructor.
	 * Creates a not rotatable widget 
	 */
	public ContainerModel() {
		this(false);
	}
	
	/**
	 * Standard constructor.
	 * @param isRotatable true if this widget is rotatable
	 */
	public ContainerModel(final boolean isRotatable) {
		super(true);
		_layerSupport = new LayerSupport(this);
	}

	/**
	 * Add a widget model to the model.
	 * 
	 * @param widget
	 *            A widget model that is to be added.
	 */
	public final void addWidget(final AbstractWidgetModel widget) {
		assert widget != null;
		doAddWidget(_widgets.size(), widget, false);
		widget.setLive(isLive());
	}
	
	/**
	 * Add a widget model to the model.
	 * 
	 * @param widget
	 *            A widget model that is to be added.
	 * @param selectWidget
	 * 			  Specifies if the {@link AbstractWidgetModel} should be selected
	 */
	public final void addWidget(final AbstractWidgetModel widget,final boolean selectWidget) {
		assert widget != null;
		doAddWidget(_widgets.size(), widget, selectWidget);
		widget.setLive(isLive());
	}
	
	/**
	 * Add a widget model at the given index to the model.
	 * 
	 * @param index
	 *            The index where to insert the widget. Must be >= 0 and <=
	 *            {@link #getWidgets()}.size()
	 * @param widget
	 *            A widget model that is to be added.
	 */
	public final void addWidget(final int index,
			final AbstractWidgetModel widget) {
		assert index >= 0 : "Precondition violated: index >= 0"; //$NON-NLS-1$
		assert index <= getWidgets().size() : "Precondition violated: index <= getWidgets().size()"; //$NON-NLS-1$

		doAddWidget(index, widget, false);
		widget.setLive(isLive());
	}

	/**
	 * Add a widget model at the given index to the model.
	 * 
	 * @param index
	 *            The index where to insert the widget. Must be >= 0 and <=
	 *            {@link #getWidgets()}.size()
	 * @param widget
	 *            A widget model that is to be added.
	 * @param selectWidget
	 * 			  Specifies if the {@link AbstractWidgetModel} should be selected
	 */
	public final void addWidget(final int index,
			final AbstractWidgetModel widget, final boolean selectWidget) {
		assert index >= 0 : "Precondition violated: index >= 0"; //$NON-NLS-1$
		assert index <= getWidgets().size() : "Precondition violated: index <= getWidgets().size()"; //$NON-NLS-1$

		doAddWidget(index, widget, selectWidget);
		widget.setLive(isLive());
	}

	/**
	 * @deprecated
	 * @see #enableParentChecks()
	 */
	private boolean _parentChecks = true;

	/**
	 * FIXME: Dies ist bisher nur ein Workarround, damit Copy & Paste
	 * funktioniert. Benötigt wird eine einfache, generische Möglichkeit,
	 * Widgets zu clonen.
	 * 
	 * @deprecated
	 * 
	 */
	public void enableParentChecks() {
		_parentChecks = true;
	}

	/**
	 * @deprecated
	 * @see #enableParentChecks()
	 */
	public void disableParentChecks() {
		_parentChecks = false;
	}

	/**
	 * Adds a widget model at the given index to the model.
	 * 
	 * @param index
	 *            the index
	 * @param widgetModel
	 *            the widget model
	 * @param selectWidget
	 * 			  Specifies if the {@link AbstractWidgetModel} should be selected
	 */
	protected void doAddWidget(final int index,
			final AbstractWidgetModel widgetModel, final boolean selectWidget) {
		// check parent relationship
		if (_parentChecks) {
			if (widgetModel.getParent() != null) {
				throw new RuntimeException(
						"Widget already has another parent. Remove it from its parent first.");
			} else {
				widgetModel.setParent(this);
			}
		}
		// add widget
		_widgets.add(index, widgetModel);
		
		//check aliases
		for (String key : this.getAliases().keySet()) {
			if (!widgetModel.getAliases().containsKey(key)) {
				widgetModel.addAlias(key, this.getAliases().get(key));
			}
		}

		// fire events
		fireCustomPropertyChangeEvent(PROP_CHILD_ADDED, widgetModel, new Boolean(selectWidget));
	}

	/**
	 * Return the widgets of this model.
	 * 
	 * @return The widgets of this model.
	 */
	public final synchronized List<AbstractWidgetModel> getWidgets() {
		return new ArrayList<AbstractWidgetModel>(_widgets);
	}

	/**
	 * Remove a widget model from the model.
	 * 
	 * @param widget
	 *            The widget model that is to be removed.
	 */
	public final void removeWidget(final AbstractWidgetModel widget) {
		// check parent relationship
		if (_parentChecks) {
			if (widget.getParent() != this) {
				throw new RuntimeException("The widget´s parent is corrupted.");
			} else {
				widget.setParent(null);
			}
		}
		// remove
		_widgets.remove(widget);
		// fire events
		firePropertyChangeEvent(PROP_CHILD_REMOVED, widget, null);
	}

	/**
	 * Gets the index of the specified widget model.
	 * 
	 * @param widget
	 *            a widget model
	 * @return the index of the specified widget model and -1 if the specified
	 *         widget is not part of the model
	 */
	public final int getIndexOf(final AbstractWidgetModel widget) {
		return _widgets.indexOf(widget);
	}
	
	public final int getPreviousLayerIndex(final AbstractWidgetModel widget) {
		int startIndex = this.getIndexOf(widget)-1;
		for (int i = startIndex;i>=0;i--) {
			if (_widgets.get(i).getLayer().equals(widget.getLayer())) {
				return i;
			}
		}
		return 0;
	}
	
	public final int getNextLayerIndex(final AbstractWidgetModel widget) {
		int startIndex = this.getIndexOf(widget)+1;
		for (int i = startIndex;i<_widgets.size();i++) {
			if (_widgets.get(i).getLayer().equals(widget.getLayer())) {
				return i;
			}
		}
		return _widgets.size()-1;
	}

	/**
	 * Sets the connection state for this model.
	 * 
	 * @param isLive
	 *            true, if the model is connected to the control system, false
	 *            otherwise
	 */
	public final void setLive(final boolean isLive) {
		super.setLive(isLive);

		// inform children
		for (AbstractWidgetModel w : getWidgets()) {
			w.setLive(isLive);
		}
	}

	/**
	 * Returns the loading state of this model.
	 * 
	 * @return true, if the model is currently being loaded, false otherwise
	 */
	public final boolean isLoading() {
		return _loading;
	}

	/**
	 * Set the loading state of the model.
	 * 
	 * @param loading
	 *            true, if the model is currently being loaded, false otherwise
	 */
	public final synchronized void setLoading(final boolean loading) {
		_loading = loading;
	}

	/**
	 * Returns the index for the front depending on the given
	 * {@link AbstractWidgetModel}.
	 * 
	 * @param child
	 *            The AbstractWidgetModel
	 * @return int The index for the front
	 */
	public final int getFrontIndex(final AbstractWidgetModel child) {
		for (int i = _widgets.size()-1;i>=0;i--) {
			AbstractWidgetModel widgetModel = _widgets.get(i);
			if (widgetModel.getLayer().equals(child.getLayer())) {
				return i;
			}
		}
		return _widgets.size() - 1;
	}

	/**
	 * Returns the index for the back.
	 * 
	 * @return int The index for the back
	 */
	public final int getBackIndex(final AbstractWidgetModel child) {
		for (int i=0;i<_widgets.size();i++) {
			if (_widgets.get(i).getLayer().equals(child.getLayer())) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * Sets the given AbstractWidgetModel to the given index in the List.
	 * 
	 * @param child
	 *            The AbstractWidgetModel, which index should be changed
	 * @param newIndex
	 *            The new index
	 */
	public final void changeOrder(final AbstractWidgetModel child,
			final int newIndex) {
		if (_widgets.contains(child) && newIndex >= 0
				&& newIndex < _widgets.size()) {
			int oldLength = _widgets.size();
			if (newIndex == _widgets.indexOf(child)) {
				return;
			}
			_widgets.remove(child);
			_widgets.add(newIndex, child);

			assert oldLength == _widgets.size() : "List is corrupted";
			firePropertyChangeEvent(PROP_ORDER_CHANGED, null, null);
		}
	}

	/**
	 * Returns the object that encapsulates all layer information for this
	 * container.
	 * 
	 * @return the object that encapsulates all layer information for this
	 *         container
	 */
	public final LayerSupport getLayerSupport() {
		return _layerSupport;
	}
}
