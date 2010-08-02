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
import org.csstudio.opibuilder.util.AlarmColorScheme;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.OPITimer;
import org.csstudio.opibuilder.visualparts.BorderFactory;
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
	
	
	private Map<String, PV> pvMap = new HashMap<String, PV>(); 
	private Map<String, PVListener> pvListenerMap = new HashMap<String, PVListener>();
	private Map<String, Boolean> pvConnectedStatusMap = new HashMap<String, Boolean>();
	
	private boolean connected = true;
	private boolean preEnableState;
	private boolean writeAccessMarked = false;
	
	private Border preBorder;
	private String currentDisconnectPVName = "";

	private String controlPVPropId = null;
	private String controlPVValuePropId = null;
	
	private Border saveBorder;
	private Color saveForeColor, saveBackColor;
	private Cursor savedCursor;
	
	//The update from PV will be suppressed for a brief time when writing was performed
	protected OPITimer updateSuppressTimer;
	//the task which will be executed when the updateSuppressTimer due.
	protected Runnable timerTask;
	private final static int UPDATE_SUPPRESS_TIME = 1000;
	
	private interface AlarmSeverity extends ISeverity{
		public void copy(ISeverity severity);
	}
	//invisible border for no_alarm state, this can prevent the widget from resizing
	//when alarm turn back to no_alarm state/
	private static final AbstractBorder BORDER_NO_ALARM = new AbstractBorder() {
		
		public void paint(IFigure figure, Graphics graphics, Insets insets) {							
		}
		
		public Insets getInsets(IFigure figure) {
			return new Insets(2);
		}
	};
	
	private AlarmSeverity lastAlarmSeverity = new AlarmSeverity(){
		
		boolean isOK = true;
		boolean isMajor = false;
		boolean isMinor = false;
		boolean isInvalid = false;
		
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

	private PropertyChangeListener[] pvValueListeners; 
	
	
	private void markWidgetAsDisconnected(final String pvName){
		if(!connected)
			return;
		connected = false;
		if(preBorder == null)
			preBorder = figure.getBorder();
		
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				boolean allDisconnected = true;
				for(boolean c : pvConnectedStatusMap.values()){
					allDisconnected &= !c;
				}
				preEnableState = figure.isEnabled();
				figure.setEnabled(preEnableState && !allDisconnected);
				figure.setBorder(BorderFactory.createBorder(
						BorderStyle.TITLE_BAR, 1, AlarmColorScheme.getDisconnectedColor(), 
						allDisconnected ? "Disconnected" : pvName + ": Disconnected"));
				currentDisconnectPVName = pvName;
				figure.repaint();
			}
		});
		
		
	}
	
	private void widgetConnectionRecovered(final String pvName){
		if(connected)
			return;
		
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				boolean allConnected = true;
				String nextDisconnecteName = ""; //$NON-NLS-1$
				
				for(String s : pvConnectedStatusMap.keySet()){
					boolean c = pvConnectedStatusMap.get(s);
					allConnected &= c;
					if(!c)
						nextDisconnecteName = 
							(String) getWidgetModel().getProperty(s).getPropertyValue();					
				}
				figure.setEnabled(preEnableState);
				if(allConnected){
					figure.setBorder(preBorder);
					connected = true;
				}
				else if(currentDisconnectPVName.equals(pvName) || currentDisconnectPVName.equals("")){ //$NON-NLS-1$
					figure.setBorder(BorderFactory.createBorder(
						BorderStyle.TITLE_BAR, 1, AlarmColorScheme.getDisconnectedColor(), 
						nextDisconnecteName + " : Disconnected"));
					currentDisconnectPVName = nextDisconnecteName;
				}
				figure.repaint();
			}
		});		
	}
	
	@Override
	protected void initFigure(IFigure figure) {
		super.initFigure(figure);
		if(getWidgetModel().isBorderAlarmSensitve()
				&& getWidgetModel().getBorderStyle()== BorderStyle.NONE){
			figure.setBorder(BORDER_NO_ALARM);
		}
	}
	class WidgetPVListener implements PVListener{
		private String pvPropID;
		
		public WidgetPVListener(String pvPropID) {
			this.pvPropID = pvPropID;
		}
		
		public void pvDisconnected(PV pv) {
			pvConnectedStatusMap.put(pvPropID, false);
			markWidgetAsDisconnected(pv.getName());
		}

		public synchronized void pvValueUpdate(PV pv) {
			if(pv == null)
				return;
			Boolean connected = pvConnectedStatusMap.get(pvPropID);
			
			//connection status
			if(connected != null && !connected){
				pvConnectedStatusMap.put(pvPropID, true);
				widgetConnectionRecovered(pv.getName());
			}
			
			//write access
			if(controlPVPropId != null && 
					controlPVPropId.equals(pvPropID) && 
					!writeAccessMarked){
				if(pv.isWriteAllowed()){
					UIBundlingThread.getInstance().addRunnable(new Runnable(){
						public void run() {									
							figure.setCursor(savedCursor);
							figure.setEnabled(getWidgetModel().isEnabled());
							preEnableState = getWidgetModel().isEnabled();
							writeAccessMarked = true;
											
						}
					});	
				}else{
					UIBundlingThread.getInstance().addRunnable(new Runnable(){
						public void run() {
							if(figure.getCursor() != Cursors.NO)
								savedCursor = figure.getCursor();
							preEnableState = false;	
							figure.setCursor(Cursors.NO);
							figure.setEnabled(false);								
							writeAccessMarked = true;							
						}
					});	
				}
										
			}
			
			getWidgetModel().getPVMap().get(getWidgetModel().
					getProperty(pvPropID)).setPropertyValue(pv.getValue());		
			
		}
	};
	
	@Override
	protected void createEditPolicies() {		
		super.createEditPolicies();
		installEditPolicy(DropPVtoPVWidgetEditPolicy.DROP_PV_ROLE,
				new DropPVtoPVWidgetEditPolicy());
	}
	
	@Override
	public void activate() {
		if(!isActive()){
			super.activate();
						
			if(getExecutionMode() == ExecutionMode.RUN_MODE){
				pvMap.clear();	
				pvConnectedStatusMap.clear();
				saveFigureOKStatus(getFigure());
				final Map<StringProperty, PVValueProperty> pvPropertyMap = getWidgetModel().getPVMap();
				
				for(final StringProperty sp : pvPropertyMap.keySet()){
					
					if(sp.getPropertyValue() == null || 
							((String)sp.getPropertyValue()).trim().length() <=0) 
						continue;
					
					try {
						PV pv = PVFactory.createPV((String) sp.getPropertyValue());
						pvConnectedStatusMap.put(sp.getPropertyID(), false);						
						
						PVListener pvListener = new WidgetPVListener(sp.getPropertyID());
//						= new PVListener(){
//							public void pvDisconnected(PV pv) {
//								pvConnectedStatusMap.put(sp.getPropertyID(), false);
//								markWidgetAsDisconnected(pv.getName());
//							}
//
//							public synchronized void pvValueUpdate(PV pv) {
//								if(pv == null)
//									return;
//								Boolean connected = pvConnectedStatusMap.get(sp.getPropertyID());
//								
//								//connection status
//								if(connected != null && !connected){
//									pvConnectedStatusMap.put(sp.getPropertyID(), true);
//									widgetConnectionRecovered(pv.getName());
//								}
//								
//								//write access
//								if(controlPVPropId != null && 
//										controlPVPropId.equals((String)sp.getPropertyID()) && 
//										!writeAccessMarked && !pv.isWriteAllowed()){
//									UIBundlingThread.getInstance().addRunnable(new Runnable(){
//										public void run() {
//											if(!writeAccessMarked){
//												figure.setCursor(Cursors.NO);
//												figure.setEnabled(false);
//												preEnableState = false;		
//												writeAccessMarked = true;
//											}
//										}
//									});							
//								}
//								
//								pvPropertyMap.get(sp).setPropertyValue(pv.getValue());		
//								
//							}							
//						};
						
						pv.addListener(pvListener);						
						pvMap.put(sp.getPropertyID(), pv);
						pvListenerMap.put(sp.getPropertyID(), pvListener);
					} catch (Exception e) {
						pvConnectedStatusMap.put(sp.getPropertyID(), false);
						markWidgetAsDisconnected((String) sp.getPropertyValue());
						CentralLogger.getInstance().error(this, "Unable to connect to PV:" +
								(String)sp.getPropertyValue());
					}					
				}
				if(!pvMap.isEmpty())
					markWidgetAsDisconnected("");
				doActivate();
				
				//the pv should be started at the last minute
				for(String pvPropId : pvMap.keySet()){
					PV pv = pvMap.get(pvPropId);
					try {
						pv.start();
					} catch (Exception e) {
						pvConnectedStatusMap.put(pvPropId, false);
						markWidgetAsDisconnected(pv.getName());
						CentralLogger.getInstance().error(this, "Unable to connect to PV:" +
								pv.getName());
					}
				}
			}
		}
	}
	
	/**
	 * Subclass should do the activate things in this method.
	 */
	protected void doActivate() {		
	}
	
	@Override
	protected void registerBasePropertyChangeHandlers() {
		super.registerBasePropertyChangeHandlers();
		// value
		IWidgetPropertyChangeHandler valueHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure figure) {
				AbstractPVWidgetModel model = getWidgetModel();
				if(!model.isBorderAlarmSensitve() && !model.isBackColorAlarmSensitve() &&
						!model.isForeColorAlarmSensitve())
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
				
//				if(lastAlarmSeverity.isOK()){
//					saveFigureOKStatus(figure);
//				}
				
				RGB alarmColor; 
				if(severity.isMajor()){
					alarmColor = AlarmColorScheme.getMajorColor();					
				}else if(severity.isMinor()){
					alarmColor = AlarmColorScheme.getMinorColor();
				}else{
					alarmColor = AlarmColorScheme.getInValidColor();
				}			
								
				if(model.isBorderAlarmSensitve()){
					figure.setBorder(BorderFactory.createBorder(BorderStyle.LINE, 2, alarmColor, ""));
				}
				if(model.isBackColorAlarmSensitve()){
					figure.setBackgroundColor(CustomMediaFactory.getInstance().getColor(alarmColor));
				}
				if(model.isForeColorAlarmSensitve()){
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
				if(oldPV != null){
					oldPV.stop();
					oldPV.removeListener(pvListenerMap.get(pvNamePropID));
				}
				try {
					PV newPV = PVFactory.createPV(newPVName);
					writeAccessMarked = false;
					pvConnectedStatusMap.put(pvNamePropID, false);	
					PVListener pvListener = new WidgetPVListener(pvNamePropID);
					newPV.addListener(pvListener);						
					pvMap.put(pvNamePropID, newPV);
					pvListenerMap.put(pvNamePropID, pvListener);
					markWidgetAsDisconnected(newPVName);
					
					try {
						newPV.start();
					} catch (Exception e) {
						pvConnectedStatusMap.put(pvNamePropID, false);
						markWidgetAsDisconnected(newPVName);
						CentralLogger.getInstance().error(this, "Unable to connect to PV:" +
								newPVName);
					}
					
				} catch (Exception e) {
					pvConnectedStatusMap.put(pvNamePropID, false);
					markWidgetAsDisconnected(newPVName);
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
				if(getWidgetModel().isBorderAlarmSensitve()
						&& getWidgetModel().getBorderStyle()== BorderStyle.NONE){
					figure.setBorder(BORDER_NO_ALARM);
				}else if (!getWidgetModel().isBorderAlarmSensitve()
						&& getWidgetModel().getBorderStyle()== BorderStyle.NONE)
					figure.setBorder(null);
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_BORDER_ALARMSENSITIVE, borderAlarmSentiveHandler);
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_BORDER_STYLE, borderAlarmSentiveHandler);
		
	}


	private void restoreFigureToOKStatus(IFigure figure) {
		figure.setBorder(saveBorder);
		figure.setBackgroundColor(saveBackColor);
		figure.setForegroundColor(saveForeColor);
	}
	private void saveFigureOKStatus(IFigure figure) {		
		saveBorder = figure.getBorder();
		saveForeColor = figure.getForegroundColor();
		saveBackColor = figure.getBackgroundColor();
	}
	
	/**
	 * Subclass should do the deActivate things in this method.
	 */
	protected void doDeActivate() {		
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
			pvConnectedStatusMap.clear();
			super.deactivate();
		}
	}
	
	public String getName() {		
		 if(getWidgetModel().getPVMap().isEmpty())
			 return "";
		return (String)((StringProperty)getWidgetModel().getPVMap().keySet().toArray()[0])
			.getPropertyValue();
	}
	
	public String getTypeId() {
		return TYPE_ID;
	}
	
	/**Set PV to given value. Should accept Double, Double[], Integer, String, maybe more.
	 * @param pvPropId
	 * @param value
	 */
	public synchronized void setPVValue(String pvPropId, Object value){		
		final PV pv = pvMap.get(pvPropId);
		if(pv != null){
			try {				
				if(pvPropId.equals(controlPVPropId) && controlPVValuePropId != null){ //activate suppress timer
					if(updateSuppressTimer == null || timerTask == null)
						initUpdateSuppressTimer();
					if(!updateSuppressTimer.isDue())
						updateSuppressTimer.reset();
					else
						startUpdateSuppressTimer();	
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
	
	/**Get the pv.
	 * @param pvPropId
	 * @return the corresponding pv for the pvPropId. null if the pv desn't exist.
	 */
	public PV getPV(String pvPropId){
		return pvMap.get(pvPropId);
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
	
	/**For PV Control widgets, mark this PV as control PV.
	 * @param pvPropId the propId of the PV.
	 */
	protected void markAsControlPV(String pvPropId, String pvValuePropId){
		controlPVPropId  = pvPropId;
		controlPVValuePropId = pvValuePropId;
		initUpdateSuppressTimer();
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
	
	/**
	 * Start the updateSuppressTimer. All property change listeners of PV_Value property will
	 * temporarily removed until timer is due.
	 */
	protected synchronized void startUpdateSuppressTimer(){
		AbstractWidgetProperty pvValueProperty = 
			getWidgetModel().getProperty(controlPVValuePropId);
		pvValueListeners = pvValueProperty.getAllPropertyChangeListeners();
		pvValueProperty.removeAllPropertyChangeListeners();
		updateSuppressTimer.start(timerTask, UPDATE_SUPPRESS_TIME);
	}
	
	@Override
	public AbstractPVWidgetModel getWidgetModel() {
		return (AbstractPVWidgetModel)getModel();
	}

	/**Set the value of the widget. This only take effect on the visual presentation of the widget and
	 * will not write the value to the PV attached to this widget which can be reached by calling
	 * {@link #setPVValue(String, Object)}.
	 * @param value the value to be set. It must be the compatible type for the widget.
	 *  For example, a boolean widget only accept boolean or double values.
	 */
	public abstract void setValue(Object value);
	
	
	/**Get the value of the widget. 
	 * @return the value of the widget. It is not the value of the attached PV 
	 * even though they are equals in most cases. {@link #getPVValue(String)}  
	 */
	public abstract Object getValue();
	
	
}
