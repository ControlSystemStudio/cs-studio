package org.csstudio.opibuilder.editparts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.properties.WidgetPropertyChangeListener;
import org.csstudio.opibuilder.properties.support.ScriptData;
import org.csstudio.opibuilder.properties.support.ScriptsInput;
import org.csstudio.opibuilder.util.RhinoScriptService;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.csstudio.opibuilder.visualparts.BorderFactory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LabeledBorder;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.internal.handlers.WizardHandler.New;
import org.eclipse.ui.progress.UIJob;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**The editpart for  {@link AbstractWidgetModel}
 * @author Xihui Chen
 *
 */
public abstract class AbstractBaseEditPart extends AbstractGraphicalEditPart{

	protected Map<String, WidgetPropertyChangeListener> propertyListenerMap;
	
	private ExecutionMode executionMode;
	
	public AbstractBaseEditPart() {
		propertyListenerMap = new HashMap<String, WidgetPropertyChangeListener>();	
	}
	
	
	@Override
	protected IFigure createFigure() {
		IFigure figure = doCreateFigure();
		if(figure == null)
			throw new IllegalArgumentException("Editpart does not provide a figure!"); //$NON-NLS-1$
		Set<String> allPropIds = getCastedModel().getAllPropertyIDs();
		if(allPropIds.contains(AbstractWidgetModel.PROP_COLOR_BACKGROUND))
			figure.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				getCastedModel().getBackgroundColor()));

		if(allPropIds.contains(AbstractWidgetModel.PROP_COLOR_FOREGROUND))
			figure.setForegroundColor(CustomMediaFactory.getInstance().getColor(
				getCastedModel().getForegroundColor()));
		
		if(allPropIds.contains(AbstractWidgetModel.PROP_VISIBLE))
			figure.setVisible(getExecutionMode() == ExecutionMode.RUN_MODE ? 
				getCastedModel().isVisible() : true);
		
		if(allPropIds.contains(AbstractWidgetModel.PROP_ENABLED))
			figure.setEnabled(getCastedModel().isEnabled());
		
		if(allPropIds.contains(AbstractWidgetModel.PROP_WIDTH) && 
				allPropIds.contains(AbstractWidgetModel.PROP_HEIGHT))
			figure.setSize(getCastedModel().getSize());
		
		if(allPropIds.contains(AbstractWidgetModel.PROP_BORDER_COLOR) &&
				allPropIds.contains(AbstractWidgetModel.PROP_BORDER_STYLE) &&
				allPropIds.contains(AbstractWidgetModel.PROP_BORDER_WIDTH))
			figure.setBorder(BorderFactory.createBorder(
				getCastedModel().getBorderStyle(), getCastedModel().getBorderWidth(), 
				getCastedModel().getBorderColor(), getCastedModel().getName()));
		
		if(allPropIds.contains(AbstractWidgetModel.PROP_FONT))
			figure.setFont(CustomMediaFactory.getInstance().getFont(getCastedModel().getFont()));
		
		return figure;
	}
	
	protected abstract IFigure doCreateFigure();

	private Map<String, PV> pvMap = new HashMap<String, PV>();
	
	@SuppressWarnings("deprecation")
	@Override
	public void activate() {
		if(!isActive()){
			super.activate();
			//add listener to all properties.
			for(String id : getCastedModel().getAllPropertyIDs()){
				WidgetPropertyChangeListener listener = 
					new WidgetPropertyChangeListener(this);
				getCastedModel().getProperty(id).addPropertyChangeListener(
					listener);
				propertyListenerMap.put(id, listener);
			}
			registerBasePropertyChangeHandlers();
			registerPropertyChangeHandlers();
			
	
			//script execution
			if(executionMode == ExecutionMode.RUN_MODE){
				pvMap.clear();
				ScriptsInput scriptsInput = getCastedModel().getScriptsInput();
				final Context scriptContext = RhinoScriptService.getInstance().getScriptContext();
				final Scriptable scriptScope = new ImporterTopLevel(scriptContext);					
				
				for(ScriptData scriptData : scriptsInput.getScriptList()){					
					IFile[] files = 
						ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(
								ResourcesPlugin.getWorkspace().getRoot().getLocation().append(scriptData.getPath()));
					
					if(files.length != 1)
						continue;
					
					
					try {
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(files[0].getContents()));	
						
						//compile and executes
						final Script script = scriptContext.compileReader(reader, "script", 1, null);
						reader.close();
						
						Object widgetController = Context.javaToJS(this, scriptScope);
						ScriptableObject.putProperty(scriptScope, "widgetController", widgetController);	
						PV[] pvArray = new PV[scriptData.getPVList().size()];
						int i = 0;
						for(String pvName : scriptData.getPVList()){
							if(pvMap.containsKey(pvName)){
								pvArray[i] = pvMap.get(pvMap);
							}else{
								try {
									pvArray[i] = PVFactory.createPV(pvName);
									pvMap.put(pvName, pvArray[i]);
									pvArray[i].addListener(new PVListener() {
										
										public void pvValueUpdate(PV pv) {
											UIBundlingThread.getInstance().addRunnable(new Runnable() {
												
												public void run() {
												//	script.exec(scriptContext, scriptScope);
												}
											});
										}
										
										public void pvDisconnected(PV pv) {
											
										}
									});
								} catch (Exception e) {
									CentralLogger.getInstance().error(this, "Unable to connect to PV:" +
											pvName);
								}
							}
							i++;							
						}
						
						ScriptableObject.putProperty(scriptScope, "pvArray", pvArray);						
						for(PV pv : pvArray)
							try {
								pv.start();
							} catch (Exception e) {
								CentralLogger.getInstance().error(this, "Unable to connect to PV:" +
										pv.getName());
							}
						script.exec(scriptContext, scriptScope);
					} catch (CoreException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}					
				}
				
				
			}
			
			
			
		}		
	}

	@Override
	public void deactivate() {
		if(isActive()){
			super.deactivate();
			//remove listener from all properties.
			for(String id : getCastedModel().getAllPropertyIDs()){
				getCastedModel().getProperty(id).removeAllPropertyChangeListeners();
				propertyListenerMap.clear();
			}
			//if(executionMode == ExecutionMode.RUN_MODE)
			//	Context.exit();
			
			for(PV pv : pvMap.values())
				pv.stop();
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
	protected final void setPropertyChangeHandler(final String propertyId, final IWidgetPropertyChangeHandler handler) {
		WidgetPropertyChangeListener listener = propertyListenerMap.get(propertyId);
		if (listener != null) {
			listener.addHandler(handler);
		}
	}
	
	protected void registerBasePropertyChangeHandlers(){
		IWidgetPropertyChangeHandler refreshVisualHandler = new IWidgetPropertyChangeHandler(){

				public boolean handleChange(Object oldValue, Object newValue,
						IFigure figure) {
					refreshVisuals();
					return false;
				}			
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_XPOS, refreshVisualHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_YPOS, refreshVisualHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_WIDTH, refreshVisualHandler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_HEIGHT, refreshVisualHandler);
		
		IWidgetPropertyChangeHandler backColorHandler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				figure.setBackgroundColor(CustomMediaFactory.getInstance().getColor((RGB)newValue));				
				return true;
			}
		};		
		setPropertyChangeHandler(AbstractWidgetModel.PROP_COLOR_BACKGROUND, backColorHandler);
		
		IWidgetPropertyChangeHandler foreColorHandler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				figure.setForegroundColor(CustomMediaFactory.getInstance().getColor((RGB)newValue));				
				return true;
			}
		};		
		setPropertyChangeHandler(AbstractWidgetModel.PROP_COLOR_FOREGROUND, foreColorHandler);
		
		IWidgetPropertyChangeHandler borderStyleHandler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				figure.setBorder(
					BorderFactory.createBorder(BorderStyle.values()[(Integer)newValue],
					getCastedModel().getBorderWidth(), getCastedModel().getBorderColor(),
					getCastedModel().getName()));
				return true;
			}
		};
		
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_STYLE, borderStyleHandler);
		
		
		IWidgetPropertyChangeHandler borderColorHandler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				figure.setBorder(
					BorderFactory.createBorder(getCastedModel().getBorderStyle(),
					getCastedModel().getBorderWidth(), (RGB)newValue,
					getCastedModel().getName()));
				return true;
			}
		};
		
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_COLOR, borderColorHandler);
	
		IWidgetPropertyChangeHandler borderWidthHandler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				figure.setBorder(
					BorderFactory.createBorder(getCastedModel().getBorderStyle(),
					(Integer)newValue, getCastedModel().getBorderColor(),
					getCastedModel().getName()));
				return true;
			}
		};
		
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_WIDTH, borderWidthHandler);
	
		IWidgetPropertyChangeHandler nameHandler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				if(figure.getBorder() instanceof LabeledBorder)
					figure.setBorder(
							BorderFactory.createBorder(getCastedModel().getBorderStyle(),
									getCastedModel().getBorderWidth(), getCastedModel().getBorderColor(),
									(String)newValue));
				return true;
			}
		};
		
		setPropertyChangeHandler(AbstractWidgetModel.PROP_NAME, nameHandler);
		
		IWidgetPropertyChangeHandler enableHandler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				figure.setEnabled((Boolean)newValue);
				return true;
			}
		};		
		setPropertyChangeHandler(AbstractWidgetModel.PROP_ENABLED, enableHandler);
		
		IWidgetPropertyChangeHandler fontHandler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				figure.setFont(CustomMediaFactory.getInstance().getFont((FontData)newValue));
				return true;
			}
		};		
		setPropertyChangeHandler(AbstractWidgetModel.PROP_FONT, fontHandler);
		
		IWidgetPropertyChangeHandler visibilityHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue, final IFigure refreshableFigure) {
				boolean visible = (Boolean) newValue;
				final IFigure figure = getFigure();
				if (getExecutionMode() == ExecutionMode.RUN_MODE) {
					figure.setVisible(visible);
				} else {
					if (!visible) {
						figure.setVisible(false);

						UIJob job = new UIJob("reset") {
							@Override
							public IStatus runInUIThread(final IProgressMonitor monitor) {
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
		setPropertyChangeHandler(AbstractWidgetModel.PROP_VISIBLE, visibilityHandler);

	
	}
	
	/**
	 * Register the property change handlers. Widget's editpart should override
	 * this to register its properties.
	 */
	protected abstract void registerPropertyChangeHandlers();
	
	
	
	public AbstractWidgetModel getCastedModel(){
		return (AbstractWidgetModel)getModel();
	}
	
	
	
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
		AbstractWidgetModel model = getCastedModel();
		GraphicalEditPart parent = (GraphicalEditPart) getParent();
		if(parent != null){
			parent.setLayoutConstraint(this, refreshableFigure, new Rectangle(
					model.getLocation(), model.getSize()));
		}		
	}

	/**
	 * @param executionMode the executionMode to set
	 */
	public void setExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
	}

	/**
	 * @return the executionMode
	 */
	public ExecutionMode getExecutionMode() {
		return executionMode;
	}
}
