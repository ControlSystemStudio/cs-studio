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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.FixedPositionAnchor.AnchorPosition;
import org.csstudio.opibuilder.editpolicies.WidgetComponentEditPolicy;
import org.csstudio.opibuilder.editpolicies.WidgetNodeEditPolicy;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.properties.WidgetPropertyChangeListener;
import org.csstudio.opibuilder.script.PVTuple;
import org.csstudio.opibuilder.script.RuleData;
import org.csstudio.opibuilder.script.ScriptData;
import org.csstudio.opibuilder.script.ScriptService;
import org.csstudio.opibuilder.script.ScriptsInput;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.OPIBuilderMacroUtil;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.csstudio.opibuilder.visualparts.BorderFactory;
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
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LabeledBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IActionFilter;
import org.eclipse.ui.progress.UIJob;

/**
 * The editpart for {@link AbstractWidgetModel}
 * 
 * @author Sven Wende (similar class in SDS)
 * @author Xihui Chen
 * 
 */
public abstract class AbstractBaseEditPart extends AbstractGraphicalEditPart implements NodeEditPart{

	public class BaseEditPartActionFilter implements IActionFilter {
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
	}

	private boolean isSelectable = true;

	protected Map<String, WidgetPropertyChangeListener> propertyListenerMap;

	private ExecutionMode executionMode;

	private TooltipLabel tooltipLabel;

	private Map<String, Object> externalObjectsMap;
	
	private Runnable displayDisposeListener;

	private Map<String, PV> pvMap = new HashMap<String, PV>();

	private ConnectionHandler connectionHandler;

	private List<ScriptData> scriptDataList;
	
	protected Map<String, ConnectionAnchor> anchorMap;

	public AbstractBaseEditPart() {
		propertyListenerMap = new HashMap<String, WidgetPropertyChangeListener>();

	}

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
								if (pv != null && !pv.isRunning())
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
			doActivate();
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
	 * Subclass should do the activate things in this method. 
	 * This method is last called from {@link #activate()}.
	 * If there is code to be called as the first thing in {@link #activate()}, it 
	 * can be put in {@link #doCreateFigure()}.
	 */
	protected void doActivate() {
	}

	/**
	 * Subclass should do the deActivate things in this method.
	 * This is the first called in {@link #deactivate()}.
	 */
	protected void doDeActivate() {
	}
	
	protected void addToConnectionHandler(String pvName, PV pv) {
		if (connectionHandler == null)
			connectionHandler = createConnectionHandler();
		connectionHandler.addPV(pvName, pv);
	}

	/**Calculate the border for the widget with assume that the widget is connected.
	 * @return the border.
	 */
	public Border calculateBorder(){		
		return BorderFactory.createBorder(getWidgetModel().getBorderStyle(),
				getWidgetModel().getBorderWidth(), getWidgetModel().getBorderColor(),
				getWidgetModel().getName());		
	}

	protected ConnectionHandler createConnectionHandler() {
		return new ConnectionHandler(this);
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new WidgetComponentEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new WidgetNodeEditPolicy());
	}

	@Override
	protected IFigure createFigure() {
		IFigure figure = doCreateFigure();
		return figure;
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			doDeActivate();
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
			SingleSourceHelper.rapDeactivateBaseEditPart(this);
		}

	}

	/**
	 * Create and initialize the widget figure with the property values in
	 * model. 	 
	 * 
	 * @return the widget figure
	  */
	protected abstract IFigure doCreateFigure();

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
			return new BaseEditPartActionFilter();
		return super.getAdapter(key);
	}

	/**
	 * @return the map with all PVs. PV name is the key. 
	 * It is not allowed to change the Map by client. null
	 *   if no PV on this widget.
	 */
	public Map<String, PV> getAllPVs() {
		if (getConnectionHandler() != null)
			return getConnectionHandler().getAllPVs();
		return null;
	}
	
	/**Get PV attached to this widget by pv name. It includes the PVs in Rules and Scripts. 
	 * @param pvName name of the PV.
	 * @return the PV. null if no such PV exists.
	 */
	public PV getPVByName(String pvName){
		if (getConnectionHandler() != null)
			return getConnectionHandler().getAllPVs().get(pvName);
		return null;
	}

	protected ConnectionHandler getConnectionHandler() {
		return connectionHandler;
	}

	public Runnable getDisplayDisposeListener() {
		return displayDisposeListener;
	}

	/**
	 * @return the executionMode
	 */
	public ExecutionMode getExecutionMode() {
		return executionMode;
	}

	/**
	 * 
	 * Get the external object by name.
	 * 
	 * @return the external object. null if no such an object was set before.
	 * @deprecated Use {@link #getVar(String)} instead.
	 */
	public synchronized Object getExternalObject(String name) {
		return getVar(name);
	}
	
	/**Get the value of a variable which 
	 * is attached to this widget by {@link #setVar(String, Object)}.
	 * @param varName name of the variable
	 * @return value of the variable. 
	 * null if no variable in this name has been attached to this widget.
	 * @since 2.0.0
	 */
	public synchronized Object getVar(String varName){
		if (externalObjectsMap != null)
			return externalObjectsMap.get(varName);
		return null;
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
	 * Get property value of the widget.
	 * 
	 * @param prop_id
	 *            the property id.
	 * @return the property value.
	 */
	public Object getPropertyValue(String prop_id) {
		return getWidgetModel().getPropertyValue(prop_id);
	}
	
	/**Get macro value from this widget.
	 * @param macroName the name of the macro.
	 * @return the value of the macro.
	 */
	public String getMacroValue(String macroName){
		return OPIBuilderMacroUtil.getWidgetMacroMap(getWidgetModel()).get(macroName);
	}

	public AbstractWidgetModel getWidgetModel() {
		return (AbstractWidgetModel) getModel();
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
							if (me.getState() == SWT.CONTROL) {
								((AbstractOpenOPIAction) action)
										.setCtrlPressed(true);
							} else if (me.getState() == SWT.SHIFT) {
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

	@Override
	public boolean isSelectable() {
		return isSelectable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void refreshVisuals() {
		doRefreshVisuals(getFigure());
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

		//add connection should not be ignored by widget listener.
		getWidgetModel().getProperty(AbstractWidgetModel.PROP_SRC_CONNETIONS)
			.addPropertyChangeListener(new PropertyChangeListener() {				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					refreshSourceConnections();					
				}
			});		
		
		getWidgetModel().getProperty(AbstractWidgetModel.PROP_TGT_CONNETIONS)
			.addPropertyChangeListener(new PropertyChangeListener() {				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					refreshTargetConnections();
				}
			});	
		
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

		IWidgetPropertyChangeHandler borderHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				setFigureBorder(calculateBorder());
				return true;
			}
		};

		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_STYLE,	borderHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_COLOR,	borderHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_WIDTH,	borderHandler);
		

		IWidgetPropertyChangeHandler labelBorderHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				if (figure.getBorder() instanceof LabeledBorder)
					figure.setBorder(BorderFactory.createBorder(
							getWidgetModel().getBorderStyle(), getWidgetModel()
									.getBorderWidth(), getWidgetModel()
									.getBorderColor(), getWidgetModel().getName()));
				return true;
			}
		};

		setPropertyChangeHandler(AbstractWidgetModel.PROP_NAME, labelBorderHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_FONT, labelBorderHandler);

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

	protected void removeFromConnectionHandler(String pvName) {
		if (connectionHandler != null)
			connectionHandler.removePV(pvName);
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
	 * Add/modify an external object from javascript.
	 * 
	 * @param name
	 *            the name of the object.
	 * @param var
	 *            the object.
	 * 
	 * @deprecated use {@link #setVar(String, Object)} instead.
	 *  
	 */
	public synchronized void setExternalObject(String name, Object var) {
		setVar(name, var);
	}
	
	/**Set variable value. If the variable does not exist, it will be added to this widget first.	 * 
	 * @param varName name of the variable.
	 * @param varValue value of the variable, which can be any type.
	 * @since 2.0.0
	 */
	public synchronized void setVar(String varName, Object varValue) {
		if (externalObjectsMap == null)
			externalObjectsMap = new HashMap<String, Object>();
		externalObjectsMap.put(varName, varValue);
	}
	

	/**Set border of the figure. It will consider the connection status.
	 * @param border
	 */
	protected void setFigureBorder(Border border){
		if(getConnectionHandler() != null && !getConnectionHandler().isConnected()){
			return;
		}
		getFigure().setBorder(border);
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
	 *            is same as old value. If false, only property value will be set 
	 *            and no listener will be fired.
	 */
	public void setPropertyValue(String prop_id, Object value, boolean forceFire) {
		getWidgetModel().setPropertyValue(prop_id, value, forceFire);
	}
	
	public void setSelectable(boolean isSelectable) {
		this.isSelectable = isSelectable;
	}
	
	/**Set this widget to be enabled.
	 * @param enable true if the widget should be enabled.
	 */
	public void setEnabled(boolean enable){
		getWidgetModel().setEnabled(enable);
	}
	
	/**Set this widget's visibility.
	 * @param enable true if the widget should be visible.
	 */
	public void setVisible(boolean visible){
		getWidgetModel().setPropertyValue(AbstractWidgetModel.PROP_VISIBLE, visible);
	}
	
	/**Set X position of the widget
	 * @param x x position in pixel which is relative to its parent.
	 * @since 2.0.0
	 */
	public void setX(Number x){
		getWidgetModel().setPropertyValue(AbstractWidgetModel.PROP_XPOS, x);
	}
	
	/**Set Y position of the widget
	 * @param y y position in pixel which is relative to its parent.
	 * @since 2.0.0
	 */
	public void setY(Number y){
		getWidgetModel().setPropertyValue(AbstractWidgetModel.PROP_YPOS, y);
	}
	
	/**Set widget's width
	 * @param width width in pixel.
	 * @since 2.0.0
	 */
	public void setWidth(Number width){
		getWidgetModel().setPropertyValue(AbstractWidgetModel.PROP_WIDTH, width);
	}
	
	/**Set widget's height
	 * @param height height in pixel.
	 * @since 2.0.0
	 */
	public void setHeight(Number height){
		getWidgetModel().setPropertyValue(AbstractWidgetModel.PROP_HEIGHT, height);
	}
	
	@Override
	protected List<ConnectionModel> getModelSourceConnections() {
	  return getWidgetModel().getSourceConnections();
	}
	
	@Override
	protected List<ConnectionModel> getModelTargetConnections() {
		return getWidgetModel().getTargetConnections();
	}
	
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {
		if(anchorMap == null)
			fillAnchorMap();
		ConnectionModel conn = (ConnectionModel) connection.getModel();
		return anchorMap.get(conn.getSourceTerminal());
	}	

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		Point p = new Point(((DropRequest) request).getLocation());
		return getClosestAnchorAt(p);
	}	
	
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		if(anchorMap == null)
			fillAnchorMap();
		ConnectionModel conn = (ConnectionModel) connection.getModel();
		return anchorMap.get(conn.getTargetTerminal());
	}
	
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		Point p = new Point(((DropRequest) request).getLocation());
		return getClosestAnchorAt(p);
	}
	
	/**Get name of the terminal by anchor
	 * @param anchor the anchor
	 * @return terminal name of the anchor. null if no name was found.
	 */
	public String getTerminalNameFromAnchor(ConnectionAnchor anchor){
		if(anchorMap == null)
			fillAnchorMap();
		for(Entry<String, ConnectionAnchor> entry : anchorMap.entrySet()){
			if(entry.getValue().equals(anchor)){
				return entry.getKey();
			}
		}
		return null;
	}
	
	/**
	 * Fill the anchor map with all predefined anchors.
	 */
	protected void fillAnchorMap() {
		anchorMap = new HashMap<String, ConnectionAnchor>(AnchorPosition.values().length);
		for(AnchorPosition pos: AnchorPosition.values()){
			anchorMap.put(pos.name(), new FixedPositionAnchor(getFigure(), pos));
		}
	}
	
	/**Get the anchor map on this widget. Caller should not change the map.
	 * @return all the anchors on this widget as in a anchor map. key is 
	 * the connection terminal name. 
	 */
	public Map<String, ConnectionAnchor> getAnchorMap() {
		if(anchorMap == null)
			fillAnchorMap();	
		return anchorMap;
	}
	
	/**Get the closest anchor to point p.
	 * @param p the reference point
	 * @return the closest anchor to point p
	 */
	protected ConnectionAnchor getClosestAnchorAt(Point p) {
		if(anchorMap == null)
			fillAnchorMap();
		ConnectionAnchor closest = null;
		double min = Long.MAX_VALUE;
		for(ConnectionAnchor anchor : anchorMap.values()){
			Point p2 = anchor.getLocation(null);
			double d=p.getDistance(p2);
			if(d<min){
				min=d;
				closest=anchor;
			}
		}
		return closest;
	}

	@Override
	public String toString() {
		return getWidgetModel().getName();
	}
	
	/**The value of the widget that is in representing.
	 * It is not the value of the attached PV even though they are equals in most cases.
	 * The value type is specified by the widget, for example, boolean for boolean widget,
	 * double for meter and gauge.
	 * @return 	The value of the widget.
	 */
	public Object getValue(){
		return null;
	}
	
	
	/**Set the value of the widget. This only takes effect on the visual presentation of the widget and
	 * will not write the value to the PV attached to this widget. Since setting value to 
	 * a widget usually results in figure repaint, this method should be called in UI thread. To call 
	 * it in non-UI thread, see {@link #setValueInUIThread(Object)}.
	 * @param value the value to be set. It must be the compatible type for the widget.
	 *  For example, a boolean widget only accept boolean or number.
	 * @throws RuntimeException if the value is not an acceptable type.
	 */	
	public void setValue(Object value){
		throw new RuntimeException("widget.setValue() does not accept " + value.getClass().getSimpleName());
	}
	
	/**Call {@link #setValue(Object)} in UI Thread.
	 *  This method can be called in non-UI thread.
	 * @param value
	 * @since 3.1.3
	 */
	public final void setValueInUIThread(final Object value){
		UIBundlingThread.getInstance().addRunnable(
				getViewer().getControl().getDisplay(), new Runnable() {
					
					@Override
					public void run() {
						setValue(value);
					}
				});
	}
}
