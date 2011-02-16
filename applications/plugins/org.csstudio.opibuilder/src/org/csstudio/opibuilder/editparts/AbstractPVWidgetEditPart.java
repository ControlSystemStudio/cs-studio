package org.csstudio.opibuilder.editparts;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.opibuilder.dnd.DropPVtoPVWidgetEditPolicy;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.util.AlarmRepresentationScheme;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.OPITimer;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.platform.ui.util.UIBundlingThread;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.RGB;

/**The abstract edit part for all PV armed widgets. 
 * Widgets inheritate this class will have the CSS context menu on it.
 * @author Xihui Chen
 *
 */
public abstract class AbstractPVWidgetEditPart extends AbstractWidgetEditPart 
	implements IProcessVariable{
	
	
	private interface AlarmSeverity extends ISeverity{
		public void copy(ISeverity severity);
	} 
	private final class WidgetPVListener implements PVListener{
		private String pvPropID;
		
		public WidgetPVListener(String pvPropID) {
			this.pvPropID = pvPropID;
		}
		
		public void pvDisconnected(PV pv) {
		}

		public void pvValueUpdate(PV pv) {
//			if(pv == null)
//				return;
			final AbstractPVWidgetModel widgetModel = getWidgetModel();
			
			//write access
			if(controlPVPropId != null && 
					controlPVPropId.equals(pvPropID) && 
					!writeAccessMarked){
				if(pv.isWriteAllowed()){
					UIBundlingThread.getInstance().addRunnable(new Runnable(){
						public void run() {									
							figure.setCursor(savedCursor);
							figure.setEnabled(widgetModel.isEnabled());
							writeAccessMarked = true;
											
						}
					});	
				}else{
					UIBundlingThread.getInstance().addRunnable(new Runnable(){
						public void run() {
							if(figure.getCursor() != Cursors.NO)
								savedCursor = figure.getCursor();
							figure.setCursor(Cursors.NO);
							figure.setEnabled(false);								
							writeAccessMarked = true;							
						}
					});	
				}
										
			}
			if(ignoreOldPVValue){
				widgetModel.getPVMap().get(widgetModel.
					getProperty(pvPropID)).setPropertyValue_IgnoreOldValue(pv.getValue());	
			}else
				widgetModel.getPVMap().get(widgetModel.
					getProperty(pvPropID)).setPropertyValue(pv.getValue());		
			
		}
	}
	

	//invisible border for no_alarm state, this can prevent the widget from resizing
	//when alarm turn back to no_alarm state/
	private static final AbstractBorder BORDER_NO_ALARM = new AbstractBorder() {
		
		public Insets getInsets(IFigure figure) {
			return new Insets(2);
		}
		
		public void paint(IFigure figure, Graphics graphics, Insets insets) {							
		}
	};	

	private final static int UPDATE_SUPPRESS_TIME = 1000;
	private String controlPVPropId = null;
	
	private String controlPVValuePropId = null;
	/**
	 * In most cases, old pv value in the valueChange() method of {@link IWidgetPropertyChangeHandler}
	 * is not useful. Ignore the old pv value will help to reduce memory usage.
	 */
	private boolean ignoreOldPVValue =true;
	private boolean isBackColorrAlarmSensitive;
	
	private boolean isBorderAlarmSensitive;
	private boolean isForeColorAlarmSensitive;
	private AlarmSeverity lastAlarmSeverity = new AlarmSeverity(){
		
		boolean isInvalid = false;
		boolean isMajor = false;
		boolean isMinor = false;
		boolean isOK = true;
		
		public void copy(ISeverity severity){
			isOK = severity.isOK();
			isMajor = severity.isMajor();
			isMinor = severity.isMinor();
			isInvalid = severity.isInvalid();
		}
		
		public boolean hasValue() {
			return false;
		}
		public boolean isInvalid() {
			return isInvalid;
		}
		public boolean isMajor() {
			return isMajor;
		}
		public boolean isMinor() {
			return isMinor;
		}
		public boolean isOK() {
			return isOK;
		}		
	};
	private Map<String, PVListener> pvListenerMap = new HashMap<String, PVListener>();
	
	private Map<String, PV> pvMap = new HashMap<String, PV>();
	private PropertyChangeListener[] pvValueListeners;
	private Border saveBorder;
	
	private Cursor savedCursor;
	
	private Color saveForeColor, saveBackColor;
	//the task which will be executed when the updateSuppressTimer due.
	protected Runnable timerTask;
	
	//The update from PV will be suppressed for a brief time when writing was performed
	protected OPITimer updateSuppressTimer;

	private AbstractPVWidgetModel widgetModel; 	
	

	private boolean writeAccessMarked = false;
	@Override
	public void activate() {
		if(!isActive()){
			super.activate();
						
			if(getExecutionMode() == ExecutionMode.RUN_MODE){
				pvMap.clear();	
				saveFigureOKStatus(getFigure());
				final Map<StringProperty, PVValueProperty> pvPropertyMap = getWidgetModel().getPVMap();
				
				for(final StringProperty sp : pvPropertyMap.keySet()){
					
					if(sp.getPropertyValue() == null || 
							((String)sp.getPropertyValue()).trim().length() <=0) 
						continue;
					
					try {
						PV pv = PVFactory.createPV((String) sp.getPropertyValue());									
						pvMap.put(sp.getPropertyID(), pv);
						addToConnectionHandler((String) sp.getPropertyValue(), pv);
						PVListener pvListener = new WidgetPVListener(sp.getPropertyID());	
						pv.addListener(pvListener);		
						pvListenerMap.put(sp.getPropertyID(), pvListener);
					} catch (Exception e) {
						CentralLogger.getInstance().error(this, "Unable to connect to PV:" +
								(String)sp.getPropertyValue());
					}					
				}

				doActivate();
				
				//the pv should be started at the last minute
				for(String pvPropId : pvMap.keySet()){
					PV pv = pvMap.get(pvPropId);
					try {
						pv.start();
					} catch (Exception e) {
						CentralLogger.getInstance().error(this, "Unable to connect to PV:" +
								pv.getName());
					}
				}
			}
		}
	};
	
	@Override
	protected void createEditPolicies() {		
		super.createEditPolicies();
		installEditPolicy(DropPVtoPVWidgetEditPolicy.DROP_PV_ROLE,
				new DropPVtoPVWidgetEditPolicy());
	}
	
	@Override
	protected ConnectionHandler createConnectionHandler() {
		return new PVWidgetConnectionHandler(this);
	}
	
	@Override
	public void deactivate() {
		if(isActive()){	
			doDeActivate();
			for(PV pv : pvMap.values())				
				pv.stop();
			
			for(String pvPropID : pvListenerMap.keySet()){
				pvMap.get(pvPropID).removeListener(pvListenerMap.get(pvPropID));
			}
			
			pvMap.clear();
			pvListenerMap.clear();
			super.deactivate();
		}
	}
	
	/**
	 * Subclass should do the activate things in this method.
	 */
	protected void doActivate() {		
	}
	
	/**
	 * Subclass should do the deActivate things in this method.
	 */
	protected void doDeActivate() {		
	}


	public String getName() {		
		 if(getWidgetModel().getPVMap().isEmpty())
			 return "";
		return (String)((StringProperty)getWidgetModel().getPVMap().keySet().toArray()[0])
			.getPropertyValue();
	}
	/**Get the pv.
	 * @param pvPropId
	 * @return the corresponding pv for the pvPropId. null if the pv desn't exist.
	 */
	public PV getPV(String pvPropId){
		return pvMap.get(pvPropId);
	}
	
	/**
	 * @return the control PV. null if no control PV on this widget.
	 */
	public PV getControlPV(){
		if(controlPVPropId != null)
			return pvMap.get(controlPVPropId);
		return null;
	}
	
	/**Get value from one of the attached PVs.
	 * @param pvPropId the property id of the PV. It is "pv_name" for the main PV.
	 * @return the {@link IValue} of the PV.
	 */
	public IValue getPVValue(String pvPropId){
		final PV pv = pvMap.get(pvPropId);
		if(pv != null){
			return pv.getValue();
		}
		return null;
	}
	
	public String getTypeId() {
		return TYPE_ID;
	}
	
	/**Get the value of the widget. 
	 * @return the value of the widget. It is not the value of the attached PV 
	 * even though they are equals in most cases. {@link #getPVValue(String)}  
	 */
	public abstract Object getValue();
	
	@Override
	public AbstractPVWidgetModel getWidgetModel() {
		return (AbstractPVWidgetModel)getModel();
	}
	
	@Override
	protected void initFigure(IFigure figure) {
		super.initFigure(figure);
		
		//initialize frequent used variables
		widgetModel = getWidgetModel();
		isBorderAlarmSensitive = widgetModel.isBorderAlarmSensitve();
		isBackColorrAlarmSensitive = widgetModel.isBackColorAlarmSensitve();
		isForeColorAlarmSensitive = widgetModel.isForeColorAlarmSensitve();
		
		if(isBorderAlarmSensitive
				&& getWidgetModel().getBorderStyle()== BorderStyle.NONE){
			setAlarmBorder(BORDER_NO_ALARM);
		}
	}
	
	/**
	 * Initialize the updateSuppressTimer
	 */
	protected synchronized void initUpdateSuppressTimer() {
		if(updateSuppressTimer == null)
			updateSuppressTimer = new OPITimer();
		if(timerTask == null)
			timerTask = new Runnable() {				
				public void run() {
					AbstractWidgetProperty pvValueProperty = 
						getWidgetModel().getProperty(controlPVValuePropId);
					//recover update
					if(pvValueListeners != null){
						for(PropertyChangeListener listener: pvValueListeners){
							pvValueProperty.addPropertyChangeListener(listener);
						}
					}						
					//forcefully set PV_Value property again					
					pvValueProperty.setPropertyValue(
							pvValueProperty.getPropertyValue(), true);
				}
			};
	}
	
	/**For PV Control widgets, mark this PV as control PV.
	 * @param pvPropId the propId of the PV.
	 */
	protected void markAsControlPV(String pvPropId, String pvValuePropId){
		controlPVPropId  = pvPropId;
		controlPVValuePropId = pvValuePropId;
		initUpdateSuppressTimer();
	}
	
	@Override
	protected void registerBasePropertyChangeHandlers() {
		super.registerBasePropertyChangeHandlers();
		// value
		IWidgetPropertyChangeHandler valueHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure figure) {
				
				if(!isBorderAlarmSensitive && !isBackColorrAlarmSensitive &&
						!isForeColorAlarmSensitive)
					return false;			
				ISeverity severity = ((IValue)newValue).getSeverity();				
				if(severity.isOK() && lastAlarmSeverity.isOK())
					return false;
				else if(severity.isOK() && !lastAlarmSeverity.isOK()){					
					lastAlarmSeverity.copy(severity);
					restoreFigureToOKStatus(figure);
					return true;
				}
				if(severity.isMajor() && lastAlarmSeverity.isMajor())
					return false;
				if(severity.isMinor() && lastAlarmSeverity.isMinor())
					return false;
				if(severity.isInvalid() && lastAlarmSeverity.isInvalid())
					return false;
				
				RGB alarmColor; 
				Border alarmBorder;
				if(severity.isMajor()){
					alarmColor = AlarmRepresentationScheme.getMajorColor();
					alarmBorder = AlarmRepresentationScheme.getMajorBorder();
				}else if(severity.isMinor()){
					alarmColor = AlarmRepresentationScheme.getMinorColor();
					alarmBorder = AlarmRepresentationScheme.getMinorBorder();
				}else{
					alarmColor = AlarmRepresentationScheme.getInValidColor();
					alarmBorder = AlarmRepresentationScheme.getInvalidBorder();
				}			
								
				if(isBorderAlarmSensitive){
					setAlarmBorder(alarmBorder);
				}
				if(isBackColorrAlarmSensitive){
					figure.setBackgroundColor(CustomMediaFactory.getInstance().getColor(alarmColor));
				}
				if(isForeColorAlarmSensitive){
					figure.setForegroundColor(CustomMediaFactory.getInstance().getColor(alarmColor));
				}				
				lastAlarmSeverity.copy(severity);
				return true;
			}

			
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, valueHandler);
		
		class PVNamePropertyChangeHandler implements IWidgetPropertyChangeHandler{
			private String pvNamePropID;
			public PVNamePropertyChangeHandler(String pvNamePropID) {
				this.pvNamePropID = pvNamePropID;
			}
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				String newPVName = ((String)newValue).trim();
				if(newPVName.length() < 0)
					return false;
				PV oldPV = pvMap.get(pvNamePropID);
				removeFromConnectionHandler((String)oldValue);
				if(oldPV != null){
					oldPV.stop();
					oldPV.removeListener(pvListenerMap.get(pvNamePropID));
				}
				try {
					PV newPV = PVFactory.createPV(newPVName);
					writeAccessMarked = false;
					PVListener pvListener = new WidgetPVListener(pvNamePropID);
					newPV.addListener(pvListener);						
					pvMap.put(pvNamePropID, newPV);
					addToConnectionHandler(newPVName, newPV);
					pvListenerMap.put(pvNamePropID, pvListener);
					
					try {
						newPV.start();
					} catch (Exception e) {
						CentralLogger.getInstance().error(this, "Unable to connect to PV:" +
								newPVName);
					}					
				} catch (Exception e) {
					CentralLogger.getInstance().error(this, "Unable to connect to PV:" +
							newPVName);
				}
				
				return false;
			}
		}
		//PV name
		for(StringProperty pvNameProperty : getWidgetModel().getPVMap().keySet()){
			if(getExecutionMode() == ExecutionMode.RUN_MODE)
				setPropertyChangeHandler(pvNameProperty.getPropertyID(), 
					new PVNamePropertyChangeHandler(pvNameProperty.getPropertyID()));
		}
		
		//border alarm sensitive		
		IWidgetPropertyChangeHandler borderAlarmSentiveHandler = new IWidgetPropertyChangeHandler() {
			
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				isBorderAlarmSensitive = widgetModel.isBorderAlarmSensitve();
				if(isBorderAlarmSensitive
						&& getWidgetModel().getBorderStyle()== BorderStyle.NONE){
					setAlarmBorder(BORDER_NO_ALARM);
				}else if (!isBorderAlarmSensitive
						&& getWidgetModel().getBorderStyle()== BorderStyle.NONE)
					setAlarmBorder(null);
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_BORDER_ALARMSENSITIVE, borderAlarmSentiveHandler);
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_BORDER_STYLE, borderAlarmSentiveHandler);
		
		IWidgetPropertyChangeHandler backColorAlarmSensitiveHandler = new IWidgetPropertyChangeHandler() {
			
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				isBackColorrAlarmSensitive = (Boolean)newValue;
				return false;
			}
		};
		
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_BACKCOLOR_ALARMSENSITIVE, backColorAlarmSensitiveHandler);
		
		IWidgetPropertyChangeHandler foreColorAlarmSensitiveHandler = new IWidgetPropertyChangeHandler() {
			
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				isForeColorAlarmSensitive = (Boolean)newValue;
				return false;
			}
		};
		
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_FORECOLOR_ALARMSENSITIVE, foreColorAlarmSensitiveHandler);
		
		
		
	}

	private void restoreFigureToOKStatus(IFigure figure) {
		setAlarmBorder(saveBorder);
		figure.setBackgroundColor(saveBackColor);
		figure.setForegroundColor(saveForeColor);
	}
	
	private void saveFigureOKStatus(IFigure figure) {		
		saveBorder = figure.getBorder();
		saveForeColor = figure.getForegroundColor();
		saveBackColor = figure.getBackgroundColor();
	}
	
	private void setAlarmBorder(Border alarmBorder){
		if(getConnectionHandler() != null && !getConnectionHandler().isConnected()){
			return;
		}
		getFigure().setBorder(alarmBorder);
	}


	public void setIgnoreOldPVValue(boolean ignoreOldValue) {
		this.ignoreOldPVValue = ignoreOldValue;
	}
	
	
	/**Set PV to given value. Should accept Double, Double[], Integer, String, maybe more.
	 * @param pvPropId
	 * @param value
	 */
	public void setPVValue(String pvPropId, Object value){		
		final PV pv = pvMap.get(pvPropId);
		if(pv != null){
			try {				
				if(pvPropId.equals(controlPVPropId) && controlPVValuePropId != null && getUpdateSuppressTime() >0){ //activate suppress timer
					synchronized (this) {
						if(updateSuppressTimer == null || timerTask == null)
							initUpdateSuppressTimer();
						if(!updateSuppressTimer.isDue())
							updateSuppressTimer.reset();
						else
							startUpdateSuppressTimer();	
					}
					
				}
				pv.setValue(value);
			} catch (final Exception e) {
				UIBundlingThread.getInstance().addRunnable(new Runnable(){
					public void run() {
						String message = 
							"Failed to write PV:" + pv.getName() + "\n" + e.getMessage();
						ConsoleService.getInstance().writeError(message);
					}
				});
			}
		}
	}
	
	/**Set the value of the widget. This only take effect on the visual presentation of the widget and
	 * will not write the value to the PV attached to this widget which can be reached by calling
	 * {@link #setPVValue(String, Object)}.
	 * @param value the value to be set. It must be the compatible type for the widget.
	 *  For example, a boolean widget only accept boolean or double values.
	 */
	public abstract void setValue(Object value);
	
	/**
	 * Start the updateSuppressTimer. All property change listeners of PV_Value property will
	 * temporarily removed until timer is due.
	 */
	protected synchronized void startUpdateSuppressTimer(){
		AbstractWidgetProperty pvValueProperty = 
			getWidgetModel().getProperty(controlPVValuePropId);
		pvValueListeners = pvValueProperty.getAllPropertyChangeListeners();
		pvValueProperty.removeAllPropertyChangeListeners();
		updateSuppressTimer.start(timerTask, getUpdateSuppressTime());
	}
	
	/**
	 * @return the time needed to suppress reading back from PV after writing. 
	 * No need to suppress if returned value <=0 
	 */
	protected int getUpdateSuppressTime(){
		return UPDATE_SUPPRESS_TIME;
	}
	
}
