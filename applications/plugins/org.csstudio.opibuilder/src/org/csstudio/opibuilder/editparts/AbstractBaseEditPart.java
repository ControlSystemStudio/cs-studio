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
package org.csstudio.opibuilder.editparts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editpolicies.WidgetComponentEditPolicy;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.properties.WidgetPropertyChangeListener;
import org.csstudio.opibuilder.script.PVTuple;
import org.csstudio.opibuilder.script.RuleData;
import org.csstudio.opibuilder.script.ScriptData;
import org.csstudio.opibuilder.script.ScriptService;
import org.csstudio.opibuilder.script.ScriptsInput;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.csstudio.opibuilder.visualparts.BorderFactory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.opibuilder.visualparts.TooltipLabel;
import org.csstudio.opibuilder.widgetActions.AbstractOpenOPIAction;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.InputEvent;
import org.eclipse.draw2d.LabeledBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IActionFilter;
import org.eclipse.ui.progress.UIJob;

/**
 * The editpart for {@link AbstractWidgetModel}
 * 
 * @author Sven Wende (similar class in SDS)
 * @author Xihui Chen
 * 
 */
public abstract class AbstractBaseEditPart extends AbstractGraphicalEditPart {

	private boolean isSelectable = true;

	protected Map<String, WidgetPropertyChangeListener> propertyListenerMap;

	private ExecutionMode executionMode;

	private TooltipLabel tooltipLabel;

	private Map<String, Object> externalObjectsMap;
	
	private Runnable displayDisposeListener;

	public AbstractBaseEditPart() {
		propertyListenerMap = new HashMap<String, WidgetPropertyChangeListener>();

	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new WidgetComponentEditPolicy());
	}

	@Override
	protected IFigure createFigure() {
		IFigure figure = doCreateFigure();
		return figure;
	}

	/**
	 * initialize the figure
	 * 
	 * @param figure
	 */
	protected void initFigure(final IFigure figure) {
		if (figure == null)
			throw new IllegalArgumentException(
					"Editpart does not provide a figure!"); //$NON-NLS-1$
		Set<String> allPropIds = getWidgetModel().getAllPropertyIDs();
		if (allPropIds.contains(AbstractWidgetModel.PROP_COLOR_BACKGROUND))
			figure.setBackgroundColor(CustomMediaFactory.getInstance()
					.getColor(getWidgetModel().getBackgroundColor()));

		if (allPropIds.contains(AbstractWidgetModel.PROP_COLOR_FOREGROUND))
			figure.setForegroundColor(CustomMediaFactory.getInstance()
					.getColor(getWidgetModel().getForegroundColor()));

		if (allPropIds.contains(AbstractWidgetModel.PROP_FONT))
			figure.setFont(getWidgetModel().getFont().getSWTFont());

		if (allPropIds.contains(AbstractWidgetModel.PROP_VISIBLE))
			figure.setVisible(getExecutionMode() == ExecutionMode.RUN_MODE ? getWidgetModel()
					.isVisible() : true);

		if (allPropIds.contains(AbstractWidgetModel.PROP_ENABLED))
			figure.setEnabled(getWidgetModel().isEnabled());

		if (allPropIds.contains(AbstractWidgetModel.PROP_WIDTH)
				&& allPropIds.contains(AbstractWidgetModel.PROP_HEIGHT))
			figure.setSize(getWidgetModel().getSize());

		if (allPropIds.contains(AbstractWidgetModel.PROP_BORDER_COLOR)
				&& allPropIds.contains(AbstractWidgetModel.PROP_BORDER_STYLE)
				&& allPropIds.contains(AbstractWidgetModel.PROP_BORDER_WIDTH))
			figure.setBorder(BorderFactory.createBorder(getWidgetModel()
					.getBorderStyle(), getWidgetModel().getBorderWidth(),
					getWidgetModel().getBorderColor(), getWidgetModel()
							.getName()));

		if (allPropIds.contains(AbstractWidgetModel.PROP_TOOLTIP)) {
			if (!getWidgetModel().getTooltip().equals("")) { //$NON-NLS-1$
				tooltipLabel = new TooltipLabel(getWidgetModel());
				figure.setToolTip(tooltipLabel);
			}
		}
	}

	/**
	 * Create and initialize the widget figure with the property values in
	 * model.
	 * 
	 * @return the widget figure
	 */
	protected abstract IFigure doCreateFigure();

	private Map<String, PV> pvMap = new HashMap<String, PV>();

	private ConnectionHandler connectionHandler;

	private List<ScriptData> scriptDataList;

	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			initFigure(getFigure());

			// add listener to all properties.
			for (String id : getWidgetModel().getAllPropertyIDs()) {

				AbstractWidgetProperty property = getWidgetModel().getProperty(
						id);
				if (property != null) {
					WidgetPropertyChangeListener listener = new WidgetPropertyChangeListener(
							this, property);
					property.addPropertyChangeListener(listener);
					propertyListenerMap.put(id, listener);

					property.setExecutionMode(executionMode);
					property.setWidgetModel(getWidgetModel());
				}

			}
			registerBasePropertyChangeHandlers();
			registerPropertyChangeHandlers();

			if (executionMode == ExecutionMode.RUN_MODE) {
				// hook open display action
				Set<String> allPropIds = getWidgetModel().getAllPropertyIDs();
				if (allPropIds.contains(AbstractWidgetModel.PROP_ACTIONS)
						&& allPropIds
								.contains(AbstractWidgetModel.PROP_ENABLED)) {
					hookMouseClickAction();
				}

				// script and rules execution
				pvMap.clear();
				ScriptsInput scriptsInput = getWidgetModel().getScriptsInput();
				scriptDataList = new ArrayList<ScriptData>(
						scriptsInput.getScriptList());
				for (RuleData rd : getWidgetModel().getRulesInput()
						.getRuleDataList()) {
					scriptDataList.add(rd.convertToScriptData());
				}
				for (final ScriptData scriptData : scriptDataList) {
					final PV[] pvArray = new PV[scriptData.getPVList().size()];
					int i = 0;
					for (PVTuple pvTuple : scriptData.getPVList()) {
						String pvName = pvTuple.pvName;
						if (pvMap.containsKey(pvName)) {
							pvArray[i] = pvMap.get(pvName);
						} else {
							try {
								PV pv = PVFactory.createPV(pvName);
								pvMap.put(pvName, pv);
								addToConnectionHandler(pvName, pv);
								pvArray[i] = pv;
							} catch (Exception e) {
								String message = NLS
										.bind("Unable to connect to PV: {0}! \n"
												+ "This may cause error when executing the script.",
												pvName);
								OPIBuilderPlugin.getLogger().log(Level.WARNING,
										message, e);
								ConsoleService.getInstance()
										.writeError(message);
								pvArray[i] = null;
							}
						}
						i++;
					}

					ScriptService.getInstance().registerScript(scriptData,
							AbstractBaseEditPart.this, pvArray);

					UIBundlingThread.getInstance().addRunnable(new Runnable() {
						public void run() {
							for (PV pv : pvArray)
								if (pv != null)
									try {
										pv.start();
									} catch (Exception e) {
										OPIBuilderPlugin
												.getLogger()
												.log(Level.WARNING,
														"Unable to start PV " + pv.getName(), e); //$NON-NLS-1$
									}
						}
					});
				}
			}
		}
		
		//Rap specified code
		displayDisposeListener = new Runnable() {

			public void run() {
				deactivate();
			}
		};
		SingleSourceHelper.rapActivateBaseEditPart(this);
	}

	/**
	 * Hook the default {@link AbstractOpenOPIAction} with mouse click.
	 */
	protected void hookMouseClickAction() {
		final List<AbstractWidgetAction> actions = getHookedActions();
		if (getWidgetModel().isEnabled() && actions != null) {
			figure.setCursor(Cursors.HAND);
			figure.addMouseListener(new MouseListener.Stub() {

				@Override
				public void mousePressed(MouseEvent me) {
					if (me.button != 1)
						return;
					for (AbstractWidgetAction action : actions) {
						if (action instanceof AbstractOpenOPIAction) {
							((AbstractOpenOPIAction) action)
									.setCtrlPressed(false);
							((AbstractOpenOPIAction) action)
									.setShiftPressed(false);
							if (me.getState() == InputEvent.CONTROL) {
								((AbstractOpenOPIAction) action)
										.setCtrlPressed(true);
							} else if (me.getState() == InputEvent.SHIFT) {
								((AbstractOpenOPIAction) action)
										.setShiftPressed(true);
							}
						}
						action.run();
					}
				}
			});
		}
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			// remove listener from all properties.
			for (String id : getWidgetModel().getAllPropertyIDs()) {
				getWidgetModel().getProperty(id)
						.removeAllPropertyChangeListeners();// removePropertyChangeListener(propertyListenerMap.get(id));
			}
			if (executionMode == ExecutionMode.RUN_MODE) {
				// remove script listeners before stopping PV.
				for (ScriptData scriptData : scriptDataList) {
					ScriptService.getInstance().unRegisterScript(scriptData);
				}
				for (PV pv : pvMap.values())
					pv.stop();
			}
			propertyListenerMap.clear();
			// propertyListenerMap = null;
		}

	}

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
		WidgetPropertyChangeListener listener = propertyListenerMap
				.get(propertyId);
		if (listener != null) {
			listener.addHandler(handler);
		}
	}

	/**
	 * Remove all the property change handlers on the specified property.
	 * 
	 * @param propID
	 *            the property id
	 */
	protected final void removeAllPropertyChangeHandlers(final String propID) {
		WidgetPropertyChangeListener listener = propertyListenerMap.get(propID);
		if (listener != null) {
			listener.removeAllHandlers();
		}
	}

	protected void registerBasePropertyChangeHandlers() {
		IWidgetPropertyChangeHandler refreshVisualHandler = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				refreshVisuals();
				return false;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_XPOS,
				refreshVisualHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_YPOS,
				refreshVisualHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_WIDTH,
				refreshVisualHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_HEIGHT,
				refreshVisualHandler);

		IWidgetPropertyChangeHandler backColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				figure.setBackgroundColor(CustomMediaFactory.getInstance()
						.getColor(((OPIColor) newValue).getRGBValue()));
				return true;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_COLOR_BACKGROUND,
				backColorHandler);

		IWidgetPropertyChangeHandler foreColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				figure.setForegroundColor(CustomMediaFactory.getInstance()
						.getColor(((OPIColor) newValue).getRGBValue()));
				return true;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_COLOR_FOREGROUND,
				foreColorHandler);

		IWidgetPropertyChangeHandler fontHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				figure.setFont(((OPIFont) newValue).getSWTFont());
				return false;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_FONT, fontHandler);

		IWidgetPropertyChangeHandler borderStyleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				figure.setBorder(BorderFactory.createBorder(BorderStyle
						.values()[(Integer) newValue], getWidgetModel()
						.getBorderWidth(), getWidgetModel().getBorderColor(),
						getWidgetModel().getName()));
				return true;
			}
		};

		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_STYLE,
				borderStyleHandler);

		IWidgetPropertyChangeHandler borderColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				figure.setBorder(BorderFactory.createBorder(getWidgetModel()
						.getBorderStyle(), getWidgetModel().getBorderWidth(),
						((OPIColor) newValue).getRGBValue(), getWidgetModel()
								.getName()));
				return true;
			}
		};

		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_COLOR,
				borderColorHandler);

		IWidgetPropertyChangeHandler borderWidthHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				figure.setBorder(BorderFactory.createBorder(getWidgetModel()
						.getBorderStyle(), (Integer) newValue, getWidgetModel()
						.getBorderColor(), getWidgetModel().getName()));
				return true;
			}
		};

		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_WIDTH,
				borderWidthHandler);

		IWidgetPropertyChangeHandler nameHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				if (figure.getBorder() instanceof LabeledBorder)
					figure.setBorder(BorderFactory.createBorder(
							getWidgetModel().getBorderStyle(), getWidgetModel()
									.getBorderWidth(), getWidgetModel()
									.getBorderColor(), (String) newValue));
				return true;
			}
		};

		setPropertyChangeHandler(AbstractWidgetModel.PROP_NAME, nameHandler);

		IWidgetPropertyChangeHandler enableHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				figure.setEnabled((Boolean) newValue);
				figure.repaint();
				return true;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_ENABLED,
				enableHandler);

		IWidgetPropertyChangeHandler tooltipHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				if (newValue.toString().equals("")) //$NON-NLS-1$
					figure.setToolTip(null);
				else {
					if (tooltipLabel == null)
						tooltipLabel = new TooltipLabel(getWidgetModel());
					figure.setToolTip(tooltipLabel);
				}
				return false;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_TOOLTIP,
				tooltipHandler);

		IWidgetPropertyChangeHandler visibilityHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				boolean visible = (Boolean) newValue;
				final IFigure figure = getFigure();
				if (getExecutionMode() == ExecutionMode.RUN_MODE) {
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
		setPropertyChangeHandler(AbstractWidgetModel.PROP_VISIBLE,
				visibilityHandler);

	}

	/**
	 * Register the property change handlers. Subclass should register its
	 * property change handlers in this method.
	 */
	protected abstract void registerPropertyChangeHandlers();

	public AbstractWidgetModel getWidgetModel() {
		return (AbstractWidgetModel) getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void refreshVisuals() {
		doRefreshVisuals(getFigure());
	}

	@Override
	public boolean isSelectable() {
		return isSelectable;
	}

	public void setSelectable(boolean isSelectable) {
		this.isSelectable = isSelectable;
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
		GraphicalEditPart parent = (GraphicalEditPart) getParent();
		if (parent != null) {
			parent.setLayoutConstraint(this, refreshableFigure, new Rectangle(
					model.getLocation(), model.getSize()));
		}
	}

	/**
	 * @param executionMode
	 *            the executionMode to set
	 */
	public void setExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
		getWidgetModel().setExecutionMode(executionMode);
		/*
		 * for(String id : getWidgetModel().getAllPropertyIDs()){
		 * AbstractWidgetProperty property = getWidgetModel().getProperty(id);
		 * if(property != null){ property.setExecutionMode(executionMode);
		 * property.setWidgetModel(getWidgetModel()); }
		 * 
		 * }
		 */
	}

	/**
	 * @return the map with all PVs. It is not allowed to change the Map. null
	 *         if no PV on this widget.
	 */
	public Map<String, PV> getAllPVs() {
		if (getConnectionHandler() != null)
			return getConnectionHandler().getAllPVs();
		return null;
	}

	/**
	 * @return the executionMode
	 */
	public ExecutionMode getExecutionMode() {
		return executionMode;
	}

	/**
	 * Add/modify an external object from javascript.
	 * 
	 * @param name
	 *            the name of the object.
	 * @param var
	 *            the object.
	 */
	public void setExternalObject(String name, Object var) {
		if (externalObjectsMap == null)
			externalObjectsMap = new HashMap<String, Object>();
		externalObjectsMap.put(name, var);
	}

	/**
	 * Get the external object by name.
	 * 
	 * @return the external object. null if no such an object was set before.
	 */
	public Object getExternalObject(String name) {
		if (externalObjectsMap != null)
			return externalObjectsMap.get(name);
		return null;
	}

	/**
	 * Set the property value of the widget. If the new value is same as the old
	 * value, it will be ignored.
	 * 
	 * @param prop_id
	 *            the property id.
	 * @param value
	 *            the value.
	 */
	public void setPropertyValue(String prop_id, Object value) {
		getWidgetModel().setPropertyValue(prop_id, value);
	}

	/**
	 * Set the property value of the widget.
	 * 
	 * @param prop_id
	 *            the property id.
	 * @param value
	 *            the value.
	 * @param forceFire
	 *            If true, the property will be set again even if the new value
	 *            is same as old value. If false and the new value is same as
	 *            the old value, it will be ignored.
	 */
	public void setPropertyValue(String prop_id, Object value, boolean forceFire) {
		getWidgetModel().setPropertyValue(prop_id, value, forceFire);
	}

	/**
	 * Get property value of the widget.
	 * 
	 * @param prop_id
	 *            the property id.
	 * @return the property value.
	 */
	public Object getPropertyValue(String prop_id) {
		return getWidgetModel().getPropertyValue(prop_id);
	}

	/**
	 * @return the default {@link AbstractWidgetAction} when mouse click this
	 *         widget.
	 */
	public List<AbstractWidgetAction> getHookedActions() {
		ActionsInput actionsInput = getWidgetModel().getActionsInput();
		if (actionsInput != null
				&& actionsInput.getActionsList().size() > 0
				&& (actionsInput.isFirstActionHookedUpToWidget() || actionsInput
						.isHookUpAllActionsToWidget())) {
			if (actionsInput.isHookUpAllActionsToWidget())
				return getWidgetModel().getActionsInput().getActionsList();
			else
				return getWidgetModel().getActionsInput().getActionsList()
						.subList(0, 1);

		}
		return null;

	}

	/**
	 * Run a widget action which is attached to the widget.
	 * 
	 * @param index
	 *            the index of the action in the actions list.
	 */
	public void executeAction(int index) {
		AbstractWidgetAction action;
		try {
			action = getWidgetModel().getActionsInput().getActionsList()
					.get(index);
			if (action != null)
				action.run();
			else
				throw new IndexOutOfBoundsException();
		} catch (IndexOutOfBoundsException e) {
			ConsoleService.getInstance().writeError(
					NLS.bind("No action at index {0} is configured for {1}",
							index, getWidgetModel().getName()));
		}
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class key) {
		if (key == IActionFilter.class)
			return new IActionFilter() {

				public boolean testAttribute(Object target, String name,
						String value) {
					if (name.equals("executionMode") && //$NON-NLS-1$
							value.equals("EDIT_MODE") && //$NON-NLS-1$
							getExecutionMode() == ExecutionMode.EDIT_MODE)
						return true;
					if (name.equals("executionMode") && //$NON-NLS-1$
							value.equals("RUN_MODE") && //$NON-NLS-1$
							getExecutionMode() == ExecutionMode.RUN_MODE)
						return true;
					if (name.equals("hasPVs") && //$NON-NLS-1$
							value.equals("true")) //$NON-NLS-1$
						return (getAllPVs() != null && getAllPVs().size() > 0);
					return false;
				}

			};
		return super.getAdapter(key);
	}

	protected void addToConnectionHandler(String pvName, PV pv) {
		if (connectionHandler == null)
			connectionHandler = createConnectionHandler();
		connectionHandler.addPV(pvName, pv);
	}

	protected ConnectionHandler createConnectionHandler() {
		return new ConnectionHandler(this);
	}

	protected void removeFromConnectionHandler(String pvName) {
		if (connectionHandler != null)
			connectionHandler.removePV(pvName);
	}

	protected ConnectionHandler getConnectionHandler() {
		return connectionHandler;
	}

	@Override
	public String toString() {
		return getWidgetModel().getName();
	}

	public Runnable getDisplayDisposeListener() {
		return displayDisposeListener;
	}	
}
