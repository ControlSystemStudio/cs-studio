package org.csstudio.opibuilder.editparts;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.IValue;
import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.IPVWidgetModel;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.util.AlarmRepresentationScheme;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.OPITimer;
import org.csstudio.opibuilder.visualparts.BorderFactory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.RGB;

public class PVWidgetEditpartDelegate implements IPVWidgetEditpart{
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
			final AbstractWidgetModel widgetModel = editpart.getWidgetModel();

			//write access
			if(controlPVPropId != null &&
					controlPVPropId.equals(pvPropID) &&
					pv.isWriteAllowed() != lastWriteAccess){
				lastWriteAccess = pv.isWriteAllowed();
				if(lastWriteAccess){
					UIBundlingThread.getInstance().addRunnable(
							editpart.getViewer().getControl().getDisplay(),new Runnable(){
						public void run() {
							if(editpart.getFigure().getCursor() == Cursors.NO)
								editpart.getFigure().setCursor(savedCursor);
							editpart.getFigure().setEnabled(widgetModel.isEnabled());	
						}
					});
				}else{
					UIBundlingThread.getInstance().addRunnable(
							editpart.getViewer().getControl().getDisplay(),new Runnable(){
						public void run() {
							if(editpart.getFigure().getCursor() != Cursors.NO)
								savedCursor = editpart.getFigure().getCursor();
							editpart.getFigure().setEnabled(false);
							editpart.getFigure().setCursor(Cursors.NO);							
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
	
	private int updateSuppressTime = 1000;
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
	private AlarmSeverity alarmSeverity = new AlarmSeverity(){

		private static final long serialVersionUID = 1L;
		
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
	private AbstractBaseEditPart editpart;
	private volatile boolean lastWriteAccess = true;
	private Cursor savedCursor;

	private Color saveForeColor, saveBackColor;
	//the task which will be executed when the updateSuppressTimer due.
	protected Runnable timerTask;

	//The update from PV will be suppressed for a brief time when writing was performed
	protected OPITimer updateSuppressTimer;
	private IPVWidgetModel widgetModel;
	
	private ListenerList setPVValueListeners;
	
	/**
	 * @param editpart the editpart to be delegated. 
	 * It must implemented {@link IPVWidgetEditpart}
	 */
	public PVWidgetEditpartDelegate(AbstractBaseEditPart editpart) {
		this.editpart = editpart;		
		

	}
	
	public IPVWidgetModel getWidgetModel() {
		if(widgetModel == null)
			widgetModel = (IPVWidgetModel) editpart.getWidgetModel();
		return widgetModel;
	}
	
	public void doActivate(){
		if(editpart.getExecutionMode() == ExecutionMode.RUN_MODE){
				pvMap.clear();
				saveFigureOKStatus(editpart.getFigure());
				final Map<StringProperty, PVValueProperty> pvPropertyMap = editpart.getWidgetModel().getPVMap();

				for(final StringProperty sp : pvPropertyMap.keySet()){

					if(sp.getPropertyValue() == null ||
							((String)sp.getPropertyValue()).trim().length() <=0)
						continue;

					try {
						PV pv = PVFactory.createPV((String) sp.getPropertyValue());
						pvMap.put(sp.getPropertyID(), pv);
						editpart.addToConnectionHandler((String) sp.getPropertyValue(), pv);
						PVListener pvListener = new WidgetPVListener(sp.getPropertyID());
						pv.addListener(pvListener);
						pvListenerMap.put(sp.getPropertyID(), pvListener);
					} catch (Exception e) {
                        OPIBuilderPlugin.getLogger().log(Level.WARNING,
                                "Unable to connect to PV:" + (String)sp.getPropertyValue(), e); //$NON-NLS-1$
					}
				}
			}
	}

	/**Start all PVs.
	 * This should be called as the last step in editpart.activate().
	 */
	public void startPVs() {
		//the pv should be started at the last minute
		for(String pvPropId : pvMap.keySet()){
			PV pv = pvMap.get(pvPropId);
			try {
				pv.start();
			} catch (Exception e) {
		        OPIBuilderPlugin.getLogger().log(Level.WARNING,
		                "Unable to connect to PV:" + pv.getName(), e); //$NON-NLS-1$
			}
		}
	}
	
	public void doDeActivate() {
			for(PV pv : pvMap.values())
				pv.stop();

			for(String pvPropID : pvListenerMap.keySet()){
				pvMap.get(pvPropID).removeListener(pvListenerMap.get(pvPropID));
			}

			pvMap.clear();
			pvListenerMap.clear();		
	}
	
	public PV getControlPV(){
		if(controlPVPropId != null)
			return pvMap.get(controlPVPropId);
		return null;
	}
	
	
	/**Get the PV corresponding to the <code>PV Name</code> property. 
	 * It is same as calling <code>getPV("pv_name")</code>.
	 * @return the PV corresponding to the <code>PV Name</code> property. 
	 * null if PV Name is not configured for this widget.
	 */
	public PV getPV(){
		return pvMap.get(IPVWidgetModel.PROP_PVNAME);
	}

	/**Get the pv by PV property id.
	 * @param pvPropId the PV property id.
	 * @return the corresponding pv for the pvPropId. null if the pv doesn't exist.
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
	
	/**
	 * @return the time needed to suppress reading back from PV after writing.
	 * No need to suppress if returned value <=0
	 */
	public int getUpdateSuppressTime(){
		return updateSuppressTime;
	}
	
	/**Set the time needed to suppress reading back from PV after writing.
	 * No need to suppress if returned value <=0
	 * @param updateSuppressTime
	 */
	public void setUpdateSuppressTime(int updateSuppressTime) {
		this.updateSuppressTime = updateSuppressTime;
	}
	
	public void initFigure(IFigure figure){		
		//initialize frequent used variables
		isBorderAlarmSensitive = getWidgetModel().isBorderAlarmSensitve();
		isBackColorrAlarmSensitive = getWidgetModel().isBackColorAlarmSensitve();
		isForeColorAlarmSensitive = getWidgetModel().isForeColorAlarmSensitve();

		if(isBorderAlarmSensitive
				&& editpart.getWidgetModel().getBorderStyle()== BorderStyle.NONE){
			editpart.setFigureBorder(BORDER_NO_ALARM);
		}
	}
	
	/**
	 * Initialize the updateSuppressTimer
	 */
	private synchronized void initUpdateSuppressTimer() {
		if(updateSuppressTimer == null)
			updateSuppressTimer = new OPITimer();
		if(timerTask == null)
			timerTask = new Runnable() {
				public void run() {
					AbstractWidgetProperty pvValueProperty =
							editpart.getWidgetModel().getProperty(controlPVValuePropId);
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
	public void markAsControlPV(String pvPropId, String pvValuePropId){
		controlPVPropId  = pvPropId;
		controlPVValuePropId = pvValuePropId;
		initUpdateSuppressTimer();
	}
	
	public boolean isPVControlWidget(){
		return controlPVPropId!=null;
	}
	
	public void registerBasePropertyChangeHandlers() {
		IWidgetPropertyChangeHandler borderHandler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				editpart.setFigureBorder(editpart.calculateBorder());
				return true;
			}
		};

		editpart.setPropertyChangeHandler(IPVWidgetModel.PROP_BORDER_ALARMSENSITIVE, borderHandler);
	
		
		// value
		IWidgetPropertyChangeHandler valueHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure figure) {

				if(!isBorderAlarmSensitive && !isBackColorrAlarmSensitive &&
						!isForeColorAlarmSensitive)
					return false;
				ISeverity newSeverity = ((IValue)newValue).getSeverity();
				if(newSeverity.isOK() && alarmSeverity.isOK())
					return false;
				else if(newSeverity.isOK() && !alarmSeverity.isOK()){
					alarmSeverity.copy(newSeverity);
					restoreFigureToOKStatus(figure);
					return true;
				}
				if(newSeverity.isMajor() && alarmSeverity.isMajor())
					return false;
				if(newSeverity.isMinor() && alarmSeverity.isMinor())
					return false;
				if(newSeverity.isInvalid() && alarmSeverity.isInvalid())
					return false;
				
				alarmSeverity.copy(newSeverity);
				
				RGB alarmColor;
				if(newSeverity.isMajor()){
					alarmColor = AlarmRepresentationScheme.getMajorColor();
				}else if(newSeverity.isMinor()){
					alarmColor = AlarmRepresentationScheme.getMinorColor();
				}else{
					alarmColor = AlarmRepresentationScheme.getInValidColor();
				}
				
				if(isBorderAlarmSensitive){
					editpart.setFigureBorder(editpart.calculateBorder());
				}
				if(isBackColorrAlarmSensitive){
					figure.setBackgroundColor(CustomMediaFactory.getInstance().getColor(alarmColor));
				}
				if(isForeColorAlarmSensitive){
					figure.setForegroundColor(CustomMediaFactory.getInstance().getColor(alarmColor));
				}
				
				return true;
			}


		};
		editpart.setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, valueHandler);

		class PVNamePropertyChangeHandler implements IWidgetPropertyChangeHandler{
			private String pvNamePropID;
			public PVNamePropertyChangeHandler(String pvNamePropID) {
				this.pvNamePropID = pvNamePropID;
			}
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				String newPVName = ((String)newValue).trim();
				if(newPVName.length() <= 0)
					return false;
				PV oldPV = pvMap.get(pvNamePropID);
				editpart.removeFromConnectionHandler((String)oldValue);
				if(oldPV != null){
					oldPV.stop();
					oldPV.removeListener(pvListenerMap.get(pvNamePropID));
				}
				try {
					PV newPV = PVFactory.createPV(newPVName);
					lastWriteAccess = true;
					PVListener pvListener = new WidgetPVListener(pvNamePropID);
					newPV.addListener(pvListener);
					pvMap.put(pvNamePropID, newPV);
					editpart.addToConnectionHandler(newPVName, newPV);
					pvListenerMap.put(pvNamePropID, pvListener);

					newPV.start();
				}
				catch (Exception e) {
				    OPIBuilderPlugin.getLogger().log(Level.WARNING, "Unable to connect to PV:" + //$NON-NLS-1$
							newPVName, e);
				}

				return false;
			}
		}
		//PV name
		for(StringProperty pvNameProperty : editpart.getWidgetModel().getPVMap().keySet()){
			if(editpart.getExecutionMode() == ExecutionMode.RUN_MODE)
				editpart.setPropertyChangeHandler(pvNameProperty.getPropertyID(),
					new PVNamePropertyChangeHandler(pvNameProperty.getPropertyID()));
		}

//		//border alarm sensitive
//		IWidgetPropertyChangeHandler borderAlarmSentiveHandler = new IWidgetPropertyChangeHandler() {
//
//			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
//				isBorderAlarmSensitive = widgetModel.isBorderAlarmSensitve();
//				if(isBorderAlarmSensitive
//						&& getWidgetModel().getBorderStyle()== BorderStyle.NONE){
//					setAlarmBorder(BORDER_NO_ALARM);
//				}else if (!isBorderAlarmSensitive
//						&& getWidgetModel().getBorderStyle()== BorderStyle.NONE)
//					setAlarmBorder(null);
//				return false;
//			}
//		};
//		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_BORDER_ALARMSENSITIVE, borderAlarmSentiveHandler);
////		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_BORDER_STYLE, borderAlarmSentiveHandler);

		IWidgetPropertyChangeHandler backColorAlarmSensitiveHandler = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				isBackColorrAlarmSensitive = (Boolean)newValue;
				return false;
			}
		};

		editpart.setPropertyChangeHandler(AbstractPVWidgetModel.PROP_BACKCOLOR_ALARMSENSITIVE, backColorAlarmSensitiveHandler);

		IWidgetPropertyChangeHandler foreColorAlarmSensitiveHandler = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				isForeColorAlarmSensitive = (Boolean)newValue;
				return false;
			}
		};

		editpart.setPropertyChangeHandler(AbstractPVWidgetModel.PROP_FORECOLOR_ALARMSENSITIVE, foreColorAlarmSensitiveHandler);

	}
	
	private void restoreFigureToOKStatus(IFigure figure) {		
		if(isBorderAlarmSensitive)
			editpart.setFigureBorder(editpart.calculateBorder());
		if(isBackColorrAlarmSensitive)
			figure.setBackgroundColor(saveBackColor);
		if(isForeColorAlarmSensitive)
			figure.setForegroundColor(saveForeColor);
	}

	private void saveFigureOKStatus(IFigure figure) {
		saveForeColor = figure.getForegroundColor();
		saveBackColor = figure.getBackgroundColor();
	}
	
	/**
	 * Start the updateSuppressTimer. All property change listeners of PV_Value property will
	 * temporarily removed until timer is due.
	 */
	protected synchronized void startUpdateSuppressTimer(){
		AbstractWidgetProperty pvValueProperty =
			editpart.getWidgetModel().getProperty(controlPVValuePropId);
		pvValueListeners = pvValueProperty.getAllPropertyChangeListeners();
		pvValueProperty.removeAllPropertyChangeListeners();
		updateSuppressTimer.start(timerTask, getUpdateSuppressTime());
	}
	
	public Border calculateBorder(){
		isBorderAlarmSensitive = getWidgetModel().isBorderAlarmSensitve();
		if(!isBorderAlarmSensitive)
			return null;
		else {
			Border alarmBorder;
			if(alarmSeverity.isOK()){
				if(editpart.getWidgetModel().getBorderStyle()== BorderStyle.NONE)
					alarmBorder = BORDER_NO_ALARM;
				else 
					alarmBorder = BorderFactory.createBorder(
							editpart.getWidgetModel().getBorderStyle(),
							editpart.getWidgetModel().getBorderWidth(),
							editpart.getWidgetModel().getBorderColor(),
							editpart.getWidgetModel().getName());
			}else if(alarmSeverity.isMajor()){
				alarmBorder = AlarmRepresentationScheme.getMajorBorder(editpart.getWidgetModel().getBorderStyle());
			}else if(alarmSeverity.isMinor()){
				alarmBorder = AlarmRepresentationScheme.getMinorBorder(editpart.getWidgetModel().getBorderStyle());
			}else{
				alarmBorder = AlarmRepresentationScheme.getInvalidBorder(editpart.getWidgetModel().getBorderStyle());
			}
			return alarmBorder;
		}
	}
	
	
	/**Set PV to given value. Should accept Double, Double[], Integer, String, maybe more.
	 * @param pvPropId
	 * @param value
	 */
	public void setPVValue(String pvPropId, Object value){
		fireSetPVValue(pvPropId, value);
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
							"Failed to write PV:" + pv.getName();
						ErrorHandlerUtil.handleError(message, e);
					}
				});
			}
		}
	}
	
	public void setIgnoreOldPVValue(boolean ignoreOldValue) {
		this.ignoreOldPVValue = ignoreOldValue;
	}


	@Override
	public String[] getAllPVNames() {
		if(editpart.getWidgetModel().getPVMap().isEmpty())
			 return new String[]{""}; //$NON-NLS-1$
		String[] result = new String[editpart.getWidgetModel().getPVMap().size()];
		int i=0;
		for(StringProperty sp : editpart.getWidgetModel().getPVMap().keySet()){
			result[i++] = (String) sp.getPropertyValue();
		}
		return result;
	}


	@Override
	public String getPVName() {
		return getWidgetModel().getPVName();
	}

	@Override
	public void addSetPVValueListener(ISetPVValueListener listener) {
		if(setPVValueListeners == null){
			setPVValueListeners = new ListenerList();
		}
		setPVValueListeners.add(listener);		
	}
	
	protected void fireSetPVValue(String pvPropId, Object value){
		if(setPVValueListeners == null)
			return;
		for(Object listener: setPVValueListeners.getListeners()){
			((ISetPVValueListener)listener).beforeSetPVValue(pvPropId, value);
		}
	}
	
}
