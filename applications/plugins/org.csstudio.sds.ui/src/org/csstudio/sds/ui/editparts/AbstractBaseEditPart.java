/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, Member of the Helmholtz Association,
 * (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.ui.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal.simple.ConnectionParameters;
import org.csstudio.dal.simple.SimpleDALBroker;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.IProcessVariableAdressProvider;
import org.csstudio.sds.cursorservice.CursorService;
import org.csstudio.sds.internal.connection.ConnectionUtilNew;
import org.csstudio.sds.internal.connection.IListenerRegistry;
import org.csstudio.sds.internal.preferences.PreferenceConstants;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.RuntimeContext;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.cursors.internal.CursorHelper;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.ICrossedFigure;
import org.csstudio.sds.ui.figures.IRhombusEquippedWidget;
import org.csstudio.sds.ui.figures.ToolTipFigure;
import org.csstudio.sds.ui.internal.editparts.WidgetPropertyChangeListener;
import org.csstudio.sds.ui.internal.properties.view.IPropertySource;
import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;
import org.csstudio.sds.util.ExecutionService;
import org.csstudio.sds.util.TooltipResolver;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.progress.UIJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cosylab.util.CommonException;

/**
 * This is the base class for all controllers of SDS widgets. In the GEF
 * model-view-controller architecture, subclasses of this class are the
 * controllers.
 * 
 * @author Sven Wende
 * @version $Revision: 1.98 $
 * 
 */
public abstract class AbstractBaseEditPart extends AbstractGraphicalEditPart
		implements NodeEditPart, PropertyChangeListener,
		IProcessVariableAdressProvider, IListenerRegistry {

	enum ConnectionStatus {
		DISCONNECTED, CONNECTED, CONNECTING, DISCONNECTING
	}

    private static final Logger LOG = LoggerFactory.getLogger(AbstractBaseEditPart.class);

	private ConnectionStatus _connectionStatus = ConnectionStatus.DISCONNECTED;

	private final Semaphore _semaphore;

	private final Map<WidgetProperty, org.csstudio.sds.model.IPropertyChangeListener> _outChannelListeners;

	/**
	 * A map, which takes property ids as key and property change listeners as
	 * values.
	 */
	public HashMap<String, WidgetPropertyChangeListener> _propertyChangeListenersById;

	/**
	 * A map, which takes properties as key and property change listeners as
	 * values.
	 */
	private final HashMap<WidgetProperty, WidgetPropertyChangeListener> _propertyChangeListenersByProperty;

	/**
	 * The execution mode (Run vs. Edit mode).
	 */
	private ExecutionMode _executionMode;

	private MouseMotionListener _motionListener;

	private final IPropertyChangeListener _preferencesListener;

	private int selected;

	private List<SimpleDalListenerInfo> registeredSimpleDalListeners = new ArrayList<SimpleDalListenerInfo>();

	/**
	 * Standard constructor.
	 */
	public AbstractBaseEditPart() {
		_semaphore = new Semaphore(1);
		_executionMode = ExecutionMode.EDIT_MODE;
		_propertyChangeListenersById = new HashMap<String, WidgetPropertyChangeListener>();
		_propertyChangeListenersByProperty = new HashMap<WidgetProperty, WidgetPropertyChangeListener>();
		_preferencesListener = new PreferencesListener();
		_outChannelListeners = new HashMap<WidgetProperty, org.csstudio.sds.model.IPropertyChangeListener>();
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

		// FIXME: Sven Wende: Das Setzen dieser Property und alles, was im
		// Weiteren damit zusammenhängt sollte dringend! einem Refactoring
		// unterworfen werden. Die Property und das Handling in den konkreten
		// Widgets ist nicht abstrakt und könnte viel weiter oben in der
		// Widget-Hierarchie und der Editpart-Hierarchie erledigt werden. Diesen
		// Aufruf hier zu machen, ist ein wilder Hack!!
		getCastedModel().setPropertyValue(
				AbstractWidgetModel.PROP_WRITE_ACCESS_GRANTED,
				!SdsUiPlugin.getCorePreferenceStore().getBoolean(
						PreferenceConstants.PROP_WRITE_ACCESS_DENIED));

		// create figure
		IFigure f = doCreateFigure();

		if (f == null) {
			throw new IllegalArgumentException(
					"Editpart does not provide a figure!"); //$NON-NLS-1$
		}

		// initialize figure

		// ... enabled
		updateFigureEnablementState(f);

		// ... colors
		f.setBackgroundColor(getModelColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND));
		f.setForegroundColor(getModelColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND));

		// ... size
		f.setSize(model.getWidth(), model.getHeight());

		if (f instanceof IAdaptable) {
			ICrossedFigure crossedFigure = (ICrossedFigure) ((IAdaptable) f)
					.getAdapter(ICrossedFigure.class);
			crossedFigure.setCrossedOut(model.isCrossedOut());
		}

		if (f instanceof IAdaptable) {
			// ... borders
			IBorderEquippedWidget borderEquippedWidgetAdapter = (IBorderEquippedWidget) ((IAdaptable) f)
					.getAdapter(IBorderEquippedWidget.class);

			if (borderEquippedWidgetAdapter != null) {
				borderEquippedWidgetAdapter.setBorderWidth(model
						.getBorderWidth());
				borderEquippedWidgetAdapter
						.setBorderColor(getModelColor(AbstractWidgetModel.PROP_BORDER_COLOR));
				borderEquippedWidgetAdapter.setBorderStyle(model
						.getBorderStyle());
				if (model.getPrimaryPV() == null) {
					borderEquippedWidgetAdapter.setBorderText("");
				} else {
					try {
						borderEquippedWidgetAdapter
								.setBorderText(ChannelReferenceValidationUtil
										.createCanonicalName(
												model.getPrimaryPV(),
												model.getAliases()));
					} catch (ChannelReferenceValidationException e) {
						borderEquippedWidgetAdapter.setBorderText(model
								.getPrimaryPV());
					}
				}
			}
			// ... cross out
			ICrossedFigure crossedEquippedWidgetAdapter = (ICrossedFigure) ((IAdaptable) f)
					.getAdapter(ICrossedFigure.class);

			if (crossedEquippedWidgetAdapter != null) {
				crossedEquippedWidgetAdapter
						.setCrossedOut(model.isCrossedOut());
			}
			// ... rhombus
			IRhombusEquippedWidget rhombusEquippedWidgetAdapter = (IRhombusEquippedWidget) ((IAdaptable) f)
					.getAdapter(IRhombusEquippedWidget.class);

			if (rhombusEquippedWidgetAdapter != null) {
				rhombusEquippedWidgetAdapter.setVisible(model.isRhombus());
			}

		}

		// ... cursor
		CursorHelper.applyCursor(f, model.getCursorId());

		// ... visibility
		f.setVisible(model.isLive() ? model.isVisible() : true);

		// f.repaint();

		return f;
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
	 * Resizes the figure. Use {@link AbstractBaseEditPart} to implement more
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

		if (parent != null) {
			parent.setLayoutConstraint(this, refreshableFigure, bounds);
		}
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
	 * Returns runtime context information.
	 * 
	 * @return runtime context information
	 */
	protected RuntimeContext getRuntimeContext() {
		return getCastedModel().getRoot().getRuntimeContext();
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
		for (WidgetProperty property : model.getProperties()) {
			WidgetPropertyChangeListener listener = new WidgetPropertyChangeListener(
					this);
			property.addPropertyChangeListener(listener);
			_propertyChangeListenersById.put(property.getId(), listener);
			_propertyChangeListenersByProperty.put(property, listener);
		}

		// register handlers for standard properties
		registerStandardPropertyChangeHandlers();

		// let subclasses register their property change handlers
		registerPropertyChangeHandlers();

		// connect
		handleLiveState(model.isLive());

		// register a mouse motion listener that updates the tooltip and cursor
		// of the figure lazily (this saves runtime resources and improves
		// performance)
		if (!(model instanceof DisplayModel)) {
			_motionListener = new MouseMotionListener.Stub() {

				@Override
				public void mouseEntered(final MouseEvent me) {
					// initialize cursor states
					CursorService.getInstance().applyCursor(getCastedModel());

					// update the tooltip
					String resolvedTooltipText = TooltipResolver
							.resolveToValue(getWidgetModel().getToolTipText(),
									getWidgetModel());
					getFigure().setToolTip(
							new ToolTipFigure(resolvedTooltipText));
				}
			};

			getFigure().addMouseMotionListener(_motionListener);

		}

		// listen to preference changes
		SdsUiPlugin.getCorePreferenceStore().addPropertyChangeListener(
				_preferencesListener);
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
			@Override
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
			@Override
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

		setPropertyChangeHandler(AbstractWidgetModel.PROP_COLOR_BACKGROUND,
				new ColorChangeHandler<IFigure>() {
					@Override
					protected void doHandle(final IFigure figure,
							final Color color) {
						figure.setBackgroundColor(color);
					}
				});

		setPropertyChangeHandler(AbstractWidgetModel.PROP_COLOR_FOREGROUND,
				new ColorChangeHandler<IFigure>() {
					@Override
					protected void doHandle(final IFigure figure,
							final Color color) {
						figure.setForegroundColor(color);
					}
				});

		IWidgetPropertyChangeHandler borderWidthHandler = new IWidgetPropertyChangeHandler() {
			@Override
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

		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_COLOR,
				new ColorChangeHandler<IFigure>() {
					@Override
					protected void doHandle(final IFigure figure,
							final Color color) {
						if (figure instanceof IAdaptable) {
							IBorderEquippedWidget borderEquippedWidgetAdapter = (IBorderEquippedWidget) ((IAdaptable) figure)
									.getAdapter(IBorderEquippedWidget.class);
							if (borderEquippedWidgetAdapter != null) {
								borderEquippedWidgetAdapter
										.setBorderColor(color);
							}
						}
					}
				});

		IWidgetPropertyChangeHandler borderStyleHandler = new IWidgetPropertyChangeHandler() {
			@Override
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
			@Override
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {

				if (getExecutionMode().equals(ExecutionMode.RUN_MODE)) {
					updateFigureEnablementState(figure);
				} else {
					figure.setEnabled(false);
				}
				return true;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_ENABLED,
				enableHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_ACCESS_GRANTED,
				enableHandler);
		// layer
		IWidgetPropertyChangeHandler layerHandler = new IWidgetPropertyChangeHandler() {
			@Override
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
			@Override
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

		// cursor
		IWidgetPropertyChangeHandler actionDataHandler = new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {

				if (newValue != null) {
					CursorHelper.applyCursor(figure, newValue.toString());
				}

				return true;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_CURSOR,
				actionDataHandler);

		// crossed
		IWidgetPropertyChangeHandler crossedHandler = new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				if (refreshableFigure instanceof IAdaptable) {
					ICrossedFigure crossedFigure = (ICrossedFigure) ((IAdaptable) refreshableFigure)
							.getAdapter(ICrossedFigure.class);
					crossedFigure.setCrossedOut((Boolean) newValue);
					return true;
				}
				return false;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_CROSSED_OUT,
				crossedHandler);
		// rhombus
		IWidgetPropertyChangeHandler rhombusHandler = new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				if (refreshableFigure instanceof IAdaptable) {
					IRhombusEquippedWidget rhombusEquippedWidgetAdapter = (IRhombusEquippedWidget) ((IAdaptable) refreshableFigure)
							.getAdapter(IRhombusEquippedWidget.class);
					if (rhombusEquippedWidgetAdapter != null) {
						rhombusEquippedWidgetAdapter
								.setVisible((Boolean) newValue);
					}
					return true;
				}

				return false;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_RHOMBUS,
				rhombusHandler);
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
		// stop listening to the preferences
		SdsUiPlugin.getCorePreferenceStore().removePropertyChangeListener(
				_preferencesListener);

		// remove
		getWidgetModel().removePropertyChangeListener(this);

		// remove the property change listeners
		for (WidgetProperty property : _propertyChangeListenersByProperty
				.keySet()) {
			property.removePropertyChangeListener(_propertyChangeListenersByProperty
					.get(property));
		}

		if (this._executionMode.equals(ExecutionMode.RUN_MODE)
				&& !(this.getModel() instanceof DisplayModel)) {
			this.getFigure().removeMouseMotionListener(_motionListener);
		}

		// disconnect from control system
		if ((_connectionStatus == ConnectionStatus.CONNECTED)
				|| (_connectionStatus == ConnectionStatus.CONNECTING)) {
			disconnectFromControlSystem();
		}

		// de-register the mouse motion listener that was registered for
		// tooltips and cursor refreshs
		if (_motionListener != null) {
			getFigure().removeMouseMotionListener(_motionListener);
		}

		super.deactivate();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ConnectionAnchor getSourceConnectionAnchor(final Request arg0) {
		return createConnectionAnchor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ConnectionAnchor getTargetConnectionAnchor(final Request arg0) {
		return createConnectionAnchor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ConnectionAnchor getSourceConnectionAnchor(
			final ConnectionEditPart arg0) {
		return new ChopboxAnchor(getFigure());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
	@SuppressWarnings("rawtypes")
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
	@Override
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
			if ((_connectionStatus == ConnectionStatus.DISCONNECTED)
					|| (_connectionStatus == ConnectionStatus.DISCONNECTING)) {
				connectToControlSystem();
			}
		} else {
			if ((_connectionStatus == ConnectionStatus.CONNECTED)
					|| (_connectionStatus == ConnectionStatus.CONNECTING)) {
				disconnectFromControlSystem();
			}
		}
	}

	private void disconnectFromControlSystem() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					_semaphore.acquire();

					// unregister listeners explicitly from SimpleDAL (Sven
					// Wende: Note: usually this should not be necessary but due
					// to bugs in
					// SimpleDAL we have to take care of it ourselves)
					for (SimpleDalListenerInfo info : registeredSimpleDalListeners) {
						try {
							SimpleDALBroker broker = getBroker();

							if (broker != null) {
								broker.deregisterListener(info.getParameters(),
										info.getListener());
							}
						} catch (Exception e) {
							// die silently
						}
					}
					registeredSimpleDalListeners.clear();

					_connectionStatus = ConnectionStatus.DISCONNECTING;

					// .. remove all (outgoing) channel listeners
					for (WidgetProperty p : _outChannelListeners.keySet()) {
						p.removePropertyChangeListener(_outChannelListeners
								.get(p));
					}

					_connectionStatus = ConnectionStatus.DISCONNECTED;
				} catch (Exception e) {
					LOG.error(e.toString());
				} finally {
					_semaphore.release();
				}
			}
		};

		ExecutionService.getInstance().executeWithNormalPriority(r);
	}

	private void connectToControlSystem() {
		_connectionStatus = ConnectionStatus.CONNECTING;

		Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					_semaphore.acquire();

					// we only need to connect to the control system if this
					// edit part is still active (maybe the display has already
					// been closed)
					if (AbstractBaseEditPart.this.isActive()) {
						_connectionStatus = ConnectionStatus.CONNECTING;

						AbstractWidgetModel widget = getCastedModel();

						// .. connect to security API
						ConnectionUtilNew.connectToWidgetManagementApi(widget);

						// .. connect single dynamized properties
						List<WidgetProperty> properties = widget
								.getProperties();
						for (WidgetProperty property : properties) {
							if (property.isVisible()) {
								ConnectionUtilNew.connectDynamizedProperties(
										property,
										widget.getAllInheritedAliases(),
										widget.isWriteAccessAllowed(),
										AbstractBaseEditPart.this, getBroker());
							}
						}

						// .. connect behavior
						ConnectionUtilNew.connectToBehavior(widget,
								AbstractBaseEditPart.this);

						// .. initialize cursor states
						CursorService.getInstance().applyCursor(widget);
						_connectionStatus = ConnectionStatus.CONNECTED;
					} else {
						_connectionStatus = ConnectionStatus.DISCONNECTED;
					}

				} catch (InterruptedException e) {
					LOG.error(e.toString());
				} finally {
					_semaphore.release();
				}

			}

		};

		ExecutionService.getInstance().executeWithNormalPriority(r);
	}

	public boolean isSelected() {
		return getSelected() == SELECTED_PRIMARY || getSelected() == SELECTED;
	}

	/**
	 * Overrides default GEF implementation to circumvent an invariant check
	 * (value==0 || isSelectable()) that tries to ensure that only 'selectable'
	 * editparts can be 'selected'. SDS supports a special behavior for
	 * containers (GroupingContainer, LinkingContainer) for which this invariant
	 * may be temporarily hurt.
	 */
	@Override
	public void setSelected(int value) {
		if (getSelected() == value)
			return;
		selected = value;
		fireSelectionChanged();
	}

	/**
	 * Overides default GEF implementation. See comment for
	 * {@link #setSelected(int)}.
	 */
	@Override
	public final int getSelected() {
		return selected;
	}

	/**
	 * Overides default GEF implementation. See comment for
	 * {@link #setSelected(int)}.
	 */
	@Override
	public boolean isSelectable() {
		boolean result = false;

		if (getFigure() != null && getFigure().isShowing()) {
			if (getCastedModel().isLive()) {
				// in run mode, widgets are always selectable
				result = true;
			} else {
				if (getParent() instanceof AbstractContainerEditPart) {
					// check that the parent container allows for child
					// selections
					AbstractContainerEditPart parent = (AbstractContainerEditPart) getParent();
					result = parent.allowsChildSelection();
				} else {
					// fallback which should usually not apply, as all SDS
					// widgets have a container parent
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public List<IProcessVariableAddress> getProcessVariableAdresses() {
		return getCastedModel().getAllPvAdresses();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		try {
			AbstractWidgetModel castedModel = getCastedModel();
			String property = castedModel.getMainPvAdress().getProperty();
			return property;
		} catch (Exception e) {
			return "";
		}
	}

//	/**
//	 * {@inheritDoc}
//	 */
//	public String getTypeId() {
//		return IProcessVariable.TYPE_ID;
//	}
//
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IProcessVariableAddress getPVAdress() {
		return getCastedModel().getMainPvAdress();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void register(final ConnectionParameters parameters,
			final ChannelListener listener) {
		try {
			SimpleDALBroker broker = getBroker();

			if (broker != null) {
				// remember listeners
				registeredSimpleDalListeners.add(new SimpleDalListenerInfo(
						parameters, listener));

				// register listeners
				broker.registerListener(parameters, listener);
			}
		} catch (InstantiationException e) {
			LOG.error("Registering simple DAL listeners failed", e.toString());
		} catch (CommonException e) {
			LOG.error("Registering simple DAL listeners failed", e.toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void register(final WidgetProperty property,
			final org.csstudio.sds.model.IPropertyChangeListener listener) {
		property.addPropertyChangeListener(listener);
		_outChannelListeners.put(property, listener);
	}

	/**
	 * Returns a {@link SimpleDALBroker} instance. Those instance will be
	 * display dependent. There will be 1 broker per display.
	 * 
	 * @return the {@link SimpleDALBroker}
	 */
	protected SimpleDALBroker getBroker() {
		SimpleDALBroker broker = getRuntimeContext().getBroker();
		return broker;
	}

	/**
	 * Updates the figures enabled state. The figure is only enabled when the
	 * current widget and all of its parents are enabled.
	 * 
	 * Additionally we disable all input widgets like sliders and buttons etc.
	 * in edit mode.
	 * 
	 * @param f
	 *            the figure that should be enabled or disabled
	 */
	protected void updateFigureEnablementState(final IFigure f) {
		// when no figure is provided via parameter we query it
		IFigure figure = f != null ? f : getFigure();
		assert figure != null;

		// in run mode the enable state depends on the widget and its parent
		// widgets
		boolean enabled = getWidgetModel().isEnabledRecursive();

		// in edit mode some widgets (e.g. sliders and buttons) are generally
		// disabled to prevent
		// accidental execution of actions and to be able to drag their
		// graphical representation
		if (getExecutionMode() == ExecutionMode.EDIT_MODE) {
			if (forceDisabledInEditMode()) {
				enabled = false;
			}
		}

		// update the figures state
		figure.setEnabled(enabled);

		// force all children to refresh their enable state, too
		for (Object child : getChildren()) {
			if (child instanceof AbstractBaseEditPart) {
				((AbstractBaseEditPart) child)
						.updateFigureEnablementState(null);
			}
		}
	}

	/**
	 * Subclasses should override this method to specify if the figure for this
	 * controller should be disabled in edit mode in general. Default return
	 * value is false.
	 * 
	 * @return true if the figure for this controller should be disabled in edit
	 *         mode in general, false otherwise
	 */
	protected boolean forceDisabledInEditMode() {
		return false;
	}

	/**
	 * Convenience method that returns the real color for the specified model
	 * property (which has to be a color property!). This call does also resolve
	 * color variables. Callers do not need to care about Color.dispose().
	 * 
	 * @param property
	 *            the id of a color property
	 * 
	 * @return the color
	 */
	protected Color getModelColor(final String propertyId) {
		String hexOrVariable = getCastedModel().getColor(propertyId);
		return SdsUiPlugin.getDefault().getColorAndFontService()
				.getColor(hexOrVariable);
	}

	/**
	 * Convenience method that returns the real font for the specified model
	 * property (which has to be a font property!). This call does also resolve
	 * font variables (see {@link IFontService}. Callers do not need to care
	 * about Font.dispose().
	 * 
	 * @param property
	 *            the id of a font property
	 * 
	 * @return the font
	 */
	protected Font getModelFont(final String propertyId) {
		String font = getCastedModel().getFont(propertyId);
		return SdsUiPlugin.getDefault().getColorAndFontService().getFont(font);
	}

	class PreferencesListener implements IPropertyChangeListener {
		@Override
		public void propertyChange(
				final org.eclipse.jface.util.PropertyChangeEvent event) {
			// .. handle preference that switches write access permissions for
			// all open displays
			if (PreferenceConstants.PROP_WRITE_ACCESS_DENIED.equals(event
					.getProperty())) {
				getCastedModel().setPropertyValue(
						AbstractWidgetModel.PROP_WRITE_ACCESS_GRANTED,
						!(Boolean) event.getNewValue());
			}
		}

	}

	/**
	 * Convenience handler class for color properties.
	 * 
	 * @author Sven Wende
	 * 
	 * @param <F>
	 *            the figure type
	 */
	public abstract class ColorChangeHandler<F extends IFigure> implements
			IWidgetPropertyChangeHandler {

		@Override
		@SuppressWarnings("unchecked")
		public boolean handleChange(final Object oldValue,
				final Object newValue, final IFigure refreshableFigure) {
			assert newValue != null;
			assert newValue instanceof String;
			Color color = SdsUiPlugin.getDefault().getColorAndFontService()
					.getColor((String) newValue);

			if (color != null) {
				doHandle((F) figure, color);
			}

			return true;
		}

		protected abstract void doHandle(F figure, Color color);
	}

	/**
	 * Convenience handler class for font properties.
	 * 
	 * @author Sven Wende
	 * 
	 * @param <F>
	 *            the figure type
	 */
	public abstract class FontChangeHandler<F extends IFigure> implements
			IWidgetPropertyChangeHandler {

		@Override
		@SuppressWarnings("unchecked")
		public boolean handleChange(final Object oldValue,
				final Object newValue, final IFigure refreshableFigure) {
			assert newValue instanceof String;

			Font font = SdsUiPlugin.getDefault().getColorAndFontService()
					.getFont((String) newValue);

			if (font != null) {
				doHandle((F) figure, font);
			}

			return true;
		}

		protected abstract void doHandle(F figure, Font font);
	}

	/**
	 * Keeps information for a single SimpleDAL listener that are needed to be
	 * able to unregister that listener from SimpleDAL.
	 * 
	 * @author swende
	 * 
	 */
	private static class SimpleDalListenerInfo {
		private ConnectionParameters parameters;
		private ChannelListener listener;

		public SimpleDalListenerInfo(ConnectionParameters parameters,
				ChannelListener listener) {
			super();
			this.parameters = parameters;
			this.listener = listener;
		}

		public ConnectionParameters getParameters() {
			return parameters;
		}

		public ChannelListener getListener() {
			return listener;
		}

	}
}