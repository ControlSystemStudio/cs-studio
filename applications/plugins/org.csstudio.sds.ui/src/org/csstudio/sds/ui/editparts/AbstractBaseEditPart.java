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
package org.csstudio.sds.ui.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.IProcessVariableAdressProvider;
import org.csstudio.sds.cursorservice.CursorService;
import org.csstudio.sds.cursorservice.ICursorServiceListener;
import org.csstudio.sds.internal.connection.BundelingThread;
import org.csstudio.sds.internal.connection.ConnectionService;
import org.csstudio.sds.internal.statistics.MeasureCategoriesEnum;
import org.csstudio.sds.internal.statistics.TimeTrackedRunnable;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ConnectionElement;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.ToolTipFigure;
import org.csstudio.sds.ui.internal.editparts.WidgetPropertyChangeListener;
import org.csstudio.sds.ui.internal.properties.view.IPropertySource;
import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.progress.UIJob;

/**
 * This is the base class for all controllers of SDS widgets. In the GEF
 * model-view-controller architecture, subclasses of this class are the
 * controllers.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public abstract class AbstractBaseEditPart extends AbstractGraphicalEditPart
		implements NodeEditPart, PropertyChangeListener,
		IProcessVariableAdressProvider, ICursorServiceListener {

	public List<IProcessVariableAddress> getProcessVariableAdresses() {
		return getCastedModel().getAllPvAdresses();
	}

	public String getName() {
		// String primaryPv = getCastedModel().getPrimaryPV();
		return getCastedModel().getMainPvAdress().getProperty();
	}

	public String getTypeId() {
		return IProcessVariable.TYPE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	public IProcessVariableAddress getPVAdress() {
		return getCastedModel().getMainPvAdress();
	}

	/**
	 * A boolean, representing if this EditPart is connected.
	 */
	private boolean _isConnected;

	/**
	 * A map, which takes property ids as key and property change listeners as
	 * values.
	 */
	private HashMap<String, WidgetPropertyChangeListener> _propertyChangeListenersById;

	/**
	 * A map, which takes properties as key and property change listeners as
	 * values.
	 */
	private HashMap<WidgetProperty, WidgetPropertyChangeListener> _propertyChangeListenersByProperty;

	/**
	 * Flag for the selection behaviour of this controller.
	 */
	private boolean _selectable;

	/**
	 * The execution mode (Run vs. Edit mode).
	 */
	private ExecutionMode _executionMode;

	/**
	 * Standard constructor.
	 */
	public AbstractBaseEditPart() {
		_executionMode = ExecutionMode.EDIT_MODE;
		_propertyChangeListenersById = new HashMap<String, WidgetPropertyChangeListener>();
		_propertyChangeListenersByProperty = new HashMap<WidgetProperty, WidgetPropertyChangeListener>();
		_selectable = true;
	}

	/**
	 * Sets the execution mode. The execution mode is one of
	 * {@link ExecutionMode#EDIT_MODE} or {@link ExecutionMode#RUN_MODE}.
	 * 
	 * @param executionMode
	 *            The new execution mode
	 */
	public final void setExecutionMode(final ExecutionMode executionMode) {
		_executionMode = executionMode;
	}

	/**
	 * Returns the current execution mode. The execution mode is one of
	 * {@link ExecutionMode#EDIT_MODE} or {@link ExecutionMode#RUN_MODE}.
	 * 
	 * @return The current execution mode
	 */
	public final ExecutionMode getExecutionMode() {
		return _executionMode;
	}

	/**
	 * Returns the model as {@link AbstractWidgetModel}.
	 * 
	 * @return the model of this {@link AbstractBaseEditPart}
	 */
	protected AbstractWidgetModel getCastedModel() {
		return (AbstractWidgetModel) getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final IFigure createFigure() {
		AbstractWidgetModel model = getWidgetModel();

		// create figure
		IFigure f = doCreateFigure();

		if (f == null) {
			throw new IllegalArgumentException(
					"Editpart does not provide a figure!"); //$NON-NLS-1$
		}

		// initialize figure
		f.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				model.getBackgroundColor()));

		f.setForegroundColor(CustomMediaFactory.getInstance().getColor(
				model.getForegroundColor()));

		f.setSize(model.getWidth(), model.getHeight());

		if (f instanceof IAdaptable) {
			IBorderEquippedWidget borderEquippedWidgetAdapter = (IBorderEquippedWidget) ((IAdaptable) f)
					.getAdapter(IBorderEquippedWidget.class);

			if (borderEquippedWidgetAdapter != null) {
				borderEquippedWidgetAdapter.setBorderWidth(model
						.getBorderWidth());
				borderEquippedWidgetAdapter.setBorderColor(CustomMediaFactory
						.getInstance().getColor(model.getBorderColor()));
				borderEquippedWidgetAdapter.setBorderStyle(model
						.getBorderStyle());
				if (model.getPrimaryPV() == null) {
					borderEquippedWidgetAdapter.setBorderText("");
				} else {
					try {
						borderEquippedWidgetAdapter
								.setBorderText(ChannelReferenceValidationUtil
										.createCanonicalName(model
												.getPrimaryPV(), model
												.getAliases()));
					} catch (ChannelReferenceValidationException e) {
						borderEquippedWidgetAdapter.setBorderText(model
								.getPrimaryPV());
					}
				}
			}
		}

		setCursorForFigure(f, model);

		f.setToolTip(new ToolTipFigure(model));
		f.repaint();

		return f;
	}

	/**
	 * Sets the cursor of a figure.
	 * 
	 * @param figure
	 *            The figure, which cursor should be set.
	 * @param model
	 *            The corresponding model of the figure
	 */
	protected void setCursorForFigure(final IFigure figure,
			final AbstractWidgetModel model) {
		if (this.getExecutionMode().equals(ExecutionMode.RUN_MODE)) {
			if (model.getActionData().getWidgetActions().isEmpty()) {
				figure.setCursor(CursorService.getInstance().getCursor(
						model.getCursor()));
			} else {
				if (this.getCastedModel().isEnabled()) {
					figure.setCursor(CursorService.getInstance()
							.getEnabledActionCursor());
				} else {
					figure.setCursor(CursorService.getInstance()
							.getDisabledActionCursor());
				}
			}
		} else {
			figure.setCursor(CursorService.getInstance().getDefaultCursor());
		}
	}

	/**
	 * Implementors should create and return the figure here.
	 * 
	 * @return the figure
	 */
	protected abstract IFigure doCreateFigure();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void refreshVisuals() {
		doRefreshVisuals(getFigure());
	}

	/**
	 * Resizes the figure. Use {@link AbstractBaseEditPart}} to implement more
	 * complex refreshing behavior.
	 * 
	 * @param refreshableFigure
	 *            the figure
	 */
	protected synchronized void doRefreshVisuals(final IFigure refreshableFigure) {
		super.refreshVisuals();
		AbstractWidgetModel model = getWidgetModel();

		Dimension size = new Dimension(model.getWidth(), model.getHeight());

		Rectangle bounds = new Rectangle(new Point(model.getX(), model.getY()),
				size);

		GraphicalEditPart parent = (GraphicalEditPart) getParent();

		parent.setLayoutConstraint(this, refreshableFigure, bounds);

	}

	/**
	 * Returns the widget model, which is managed by this controller. This is
	 * for convinience only. The method returns the same object as
	 * {@link #getModel()}.
	 * 
	 * @return the casted model
	 */
	public final AbstractWidgetModel getWidgetModel() {
		return (AbstractWidgetModel) getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activate() {
		super.activate();
		final AbstractWidgetModel model = getWidgetModel();

		// add as property change listener to the widget
		model.addPropertyChangeListener(this);

		// add one property change listener to each property of the widget
		for (String propertyId : model.getPropertyNames()) {
			WidgetProperty property = model.getProperty(propertyId);
			WidgetPropertyChangeListener listener = new WidgetPropertyChangeListener(
					this);
			property.addPropertyChangeListener(listener);
			_propertyChangeListenersById.put(propertyId, listener);
			_propertyChangeListenersByProperty.put(property, listener);
		}

		// register handlers for standard properties
		registerStandardPropertyChangeHandlers();

		// let subclasses register their property change handlers
		registerPropertyChangeHandlers();

		// listen to cursor service
		CursorService.getInstance().addCursorServiceListener(this);

		// connect
		handleLiveState(model.isLive());
	}

	/**
	 * Register the standard property change handlers that are in common for all
	 * element types. These are:
	 * <ul>
	 * <li>PROP_VISIBILITY</li>
	 * <li>PROP_POS_X</li>
	 * <li>PROP_POS_Y</li>
	 * <li>PROP_WIDTH</li>
	 * <li>PROP_HEIGHT</li>
	 * <li>PROP_COLOR_BACKGROUND</li>
	 * <li>PROP_COLOR_FOREGROUND</li>
	 * <li>PROP_BORDER_WIDTH</li>
	 * <li>PROP_BORDER_COLOR</li>
	 * <li>PROP_BORDER_STYLE</li>
	 * <li>PROP_LAYER</li>
	 * <li>PROP_ENABLED</li>
	 * </ul>
	 * 
	 */
	private void registerStandardPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler visibilityHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				boolean visible = (Boolean) newValue;
				final IFigure figure = getFigure();
				if (getWidgetModel().isLive()) {
					figure.setVisible(visible);
				} else {
					if (!visible) {
						figure.setVisible(false);

						UIJob job = new UIJob("reset") {
							@Override
							public IStatus runInUIThread(
									final IProgressMonitor monitor) {
								figure.setVisible(true);
								return Status.OK_STATUS;
							}
						};
						job.schedule(2000);
					}
				}
				return true;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_VISIBILITY,
				visibilityHandler);

		IWidgetPropertyChangeHandler fullRefreshHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				AbstractBaseEditPart.this.doRefreshVisuals(refreshableFigure);
				return true;
			}
		};

		setPropertyChangeHandler(AbstractWidgetModel.PROP_POS_X,
				fullRefreshHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_POS_Y,
				fullRefreshHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_WIDTH,
				fullRefreshHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_HEIGHT,
				fullRefreshHandler);

		IWidgetPropertyChangeHandler bgColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				refreshableFigure.setBackgroundColor(CustomMediaFactory
						.getInstance().getColor((RGB) newValue));
				return true;
			}
		};

		setPropertyChangeHandler(AbstractWidgetModel.PROP_COLOR_BACKGROUND,
				bgColorHandler);

		IWidgetPropertyChangeHandler fgColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				refreshableFigure.setForegroundColor(CustomMediaFactory
						.getInstance().getColor((RGB) newValue));
				return true;
			}
		};

		setPropertyChangeHandler(AbstractWidgetModel.PROP_COLOR_FOREGROUND,
				fgColorHandler);

		IWidgetPropertyChangeHandler borderWidthHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				if (figure instanceof IAdaptable) {
					IBorderEquippedWidget borderEquippedWidgetAdapter = (IBorderEquippedWidget) ((IAdaptable) figure)
							.getAdapter(IBorderEquippedWidget.class);
					if (borderEquippedWidgetAdapter != null) {
						borderEquippedWidgetAdapter
								.setBorderWidth((Integer) newValue);

					}
					return true;
				}

				return false;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_WIDTH,
				borderWidthHandler);

		IWidgetPropertyChangeHandler borderColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				if (figure instanceof IAdaptable) {
					IBorderEquippedWidget borderEquippedWidgetAdapter = (IBorderEquippedWidget) ((IAdaptable) figure)
							.getAdapter(IBorderEquippedWidget.class);
					if (borderEquippedWidgetAdapter != null) {
						borderEquippedWidgetAdapter
								.setBorderColor(CustomMediaFactory
										.getInstance().getColor((RGB) newValue));
					}
					return true;
				}
				return false;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_COLOR,
				borderColorHandler);

		IWidgetPropertyChangeHandler borderStyleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				if (figure instanceof IAdaptable) {
					IBorderEquippedWidget borderEquippedWidgetAdapter = (IBorderEquippedWidget) ((IAdaptable) figure)
							.getAdapter(IBorderEquippedWidget.class);
					if (borderEquippedWidgetAdapter != null) {
						borderEquippedWidgetAdapter
								.setBorderStyle((Integer) newValue);
					}
					return true;
				}
				return false;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_STYLE,
				borderStyleHandler);
		// enabled
		IWidgetPropertyChangeHandler enableHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				if (getExecutionMode().equals(ExecutionMode.RUN_MODE)) {
					refreshableFigure.setEnabled((Boolean) newValue);
				} else {
					refreshableFigure.setEnabled(false);
				}
				return true;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_ENABLED,
				enableHandler);
		// layer
		IWidgetPropertyChangeHandler layerHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				if (getParent() instanceof AbstractContainerEditPart) {
					String oldLayerName = getLayerName(oldValue);
					String newLayerName = getLayerName(newValue);

					((AbstractContainerEditPart) getParent())
							.handleLayerChanged(AbstractBaseEditPart.this,
									oldLayerName, newLayerName);
				}
				return true;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_LAYER, layerHandler);

		IWidgetPropertyChangeHandler primaryPVHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				String pv = getCastedModel().getPrimaryPV();
				Map<String, String> aliases = getCastedModel().getAliases();
				if (figure instanceof IAdaptable) {
					IBorderEquippedWidget borderEquippedWidgetAdapter = (IBorderEquippedWidget) ((IAdaptable) refreshableFigure)
							.getAdapter(IBorderEquippedWidget.class);
					if (borderEquippedWidgetAdapter != null) {
						if (pv == null) {
							borderEquippedWidgetAdapter.setBorderText("");
						} else {
							try {
								borderEquippedWidgetAdapter
										.setBorderText(ChannelReferenceValidationUtil
												.createCanonicalName(pv,
														aliases));
							} catch (ChannelReferenceValidationException e) {
								borderEquippedWidgetAdapter.setBorderText(pv);
							}
						}
					}
				}
				return true;
			}

		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_PRIMARY_PV,
				primaryPVHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_ALIASES,
				primaryPVHandler);

		// tooltip
		IWidgetPropertyChangeHandler tooltipHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				if (refreshableFigure.getToolTip() instanceof ToolTipFigure) {
					ToolTipFigure ttFigure = (ToolTipFigure) refreshableFigure
							.getToolTip();
					ttFigure.setToolTipText((String) newValue);
				}
				return true;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_TOOLTIP,
				tooltipHandler);

		// cursor
		IWidgetPropertyChangeHandler actionDataHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				setCursorForFigure(figure, getCastedModel());
				return true;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_ACTIONDATA,
				actionDataHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_CURSOR,
				actionDataHandler);
		// tooltip
		IWidgetPropertyChangeHandler tooltipRefreshHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				if (refreshableFigure.getToolTip() instanceof ToolTipFigure) {
					ToolTipFigure ttFigure = (ToolTipFigure) refreshableFigure
							.getToolTip();
					ttFigure.refresh();
				}
				return true;
			}
		};
		for (String propertyName : getWidgetModel().getPropertyNames()) {
			if (!propertyName.equals(AbstractWidgetModel.PROP_TOOLTIP)) {
				setPropertyChangeHandler(propertyName, tooltipRefreshHandler);
			}
		}
	}

	/**
	 * TODO: nur temporär verwenden! Returns the layer name
	 * 
	 * @param s
	 *            the layerName (may be null or "")
	 * @return The layerName (is not null or "")
	 */
	private String getLayerName(final Object s) {
		if (s == null) {
			return "DEFAULT";
		} else {
			if (s.toString().equals("")) {
				return "DEFAULT";
			} else {
				return s.toString();
			}
		}
	}

	/**
	 * Subclasses should register handlers for property changes here.
	 * 
	 * Each handler can be registered by calling
	 * {@link #setPropertyChangeHandler(String, IWidgetPropertyChangeHandler)}
	 */
	protected abstract void registerPropertyChangeHandlers();

	/**
	 * Registers a property change handler for the specified property id.
	 * 
	 * @param propertyId
	 *            the property id
	 * @param handler
	 *            the property change handler
	 */
	protected final void setPropertyChangeHandler(final String propertyId,
			final IWidgetPropertyChangeHandler handler) {
		WidgetPropertyChangeListener listener = _propertyChangeListenersById
				.get(propertyId);
		if (listener != null) {
			listener.addHandler(handler);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deactivate() {
		// remove
		getWidgetModel().removePropertyChangeListener(this);

		// remove the property change listeners
		for (WidgetProperty property : _propertyChangeListenersByProperty
				.keySet()) {
			property
					.removePropertyChangeListener(_propertyChangeListenersByProperty
							.get(property));
		}

		// disconnect from control system
		if (_isConnected) {
			ConnectionService.getInstance().disConnectWidgetModel(
					getWidgetModel());
			_isConnected = false;
		}

		// disconnect from cursor service
		CursorService.getInstance().removeCursorServiceListener(this);

		super.deactivate();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final List<ConnectionElement> getModelSourceConnections() {
		return getWidgetModel().getSourceConnections();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final List<ConnectionElement> getModelTargetConnections() {
		return getWidgetModel().getTargetConnections();
	}

	/**
	 * {@inheritDoc}
	 */
	public final ConnectionAnchor getSourceConnectionAnchor(final Request arg0) {
		return createConnectionAnchor();
	}

	/**
	 * {@inheritDoc}
	 */
	public final ConnectionAnchor getTargetConnectionAnchor(final Request arg0) {
		return createConnectionAnchor();
	}

	/**
	 * {@inheritDoc}
	 */
	public final ConnectionAnchor getSourceConnectionAnchor(
			final ConnectionEditPart arg0) {
		return new ChopboxAnchor(getFigure());
	}

	/**
	 * {@inheritDoc}
	 */
	public final ConnectionAnchor getTargetConnectionAnchor(
			final ConnectionEditPart arg0) {

		return new ChopboxAnchor(getFigure());
	}

	/**
	 * Creates a connection anchor.
	 * 
	 * @return a connection anchor
	 */
	private ConnectionAnchor createConnectionAnchor() {
		return new ChopboxAnchor(getFigure());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(final Class adapter) {
		if (adapter == IPropertySource.class) {
			return getWidgetModel().getAdapter(adapter);
		}

		return super.getAdapter(adapter);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public synchronized void propertyChange(final PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();

		if (prop.equals(AbstractWidgetModel.PROP_LIVE)) {
			Boolean live = (Boolean) evt.getNewValue();
			handleLiveState(live);
		}
	}

	/**
	 * Configures this EditPart with the given live state.
	 * 
	 * @param live
	 *            The new live state
	 */
	private void handleLiveState(final boolean live) {
		if (live) {
			if (!_isConnected) {
				ConnectionService.getInstance().connectWidgetModel(
						getWidgetModel());
				_isConnected = true;
			}
		} else {
			if (_isConnected) {
				ConnectionService.getInstance().disConnectWidgetModel(
						getWidgetModel());
				_isConnected = false;
			}

			getWidgetModel().setVisible(true);
		}
	}

	/**
	 * Sets, if this EditPart could be selected.
	 * 
	 * @param selectable
	 *            The new selection state
	 */
	public void setSelectable(final boolean selectable) {
		_selectable = selectable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSelectable() {
		return _selectable;
	}

	public void refreshTooltip() {
		IFigure toolTip = getFigure().getToolTip();
		if (toolTip instanceof ToolTipFigure) {
			((ToolTipFigure) toolTip).refresh();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final void cursorChanged() {
		this.setCursorForFigure(this.getFigure(), this.getCastedModel());
	}

}
