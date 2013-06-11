package org.csstudio.opibuilder.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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
import org.csstudio.opibuilder.pvmanager.BOYPVFactory;
import org.csstudio.opibuilder.pvmanager.PVManagerPV;
import org.csstudio.opibuilder.util.AlarmRepresentationScheme;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.OPITimer;
import org.csstudio.opibuilder.visualparts.BorderFactory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.RGB;
import org.epics.pvmanager.PVWriterEvent;
import org.epics.pvmanager.PVWriterListener;

public class PVWidgetEditpartDelegate implements IPVWidgetEditpart {
	private interface AlarmSeverity extends ISeverity{
		public void copy(ISeverity severity);
	}
	private final class WidgetPVListener implements PVListener, PVWriterListener<Object>{
		private String pvPropID;
		private boolean isControlPV;

		public WidgetPVListener(String pvPropID) {
			this.pvPropID = pvPropID;
			isControlPV = pvPropID.equals(controlPVPropId);
		}

		public void pvDisconnected(PV pv) {
			lastWriteAccess = null;
		}

		public void pvValueUpdate(PV pv) {

			final AbstractWidgetModel widgetModel = editpart.getWidgetModel();

			//write access
			if(isControlPV)
				updateWritable(widgetModel, pv);
			
			if (pv.getValue() != null) {
				if (ignoreOldPVValue) {
					widgetModel.getPVMap()
							.get(widgetModel.getProperty(pvPropID))
							.setPropertyValue_IgnoreOldValue(pv.getValue());
				} else
					widgetModel.getPVMap()
							.get(widgetModel.getProperty(pvPropID))
							.setPropertyValue(pv.getValue());
			}
			
		}

		@Override
		public void pvChanged(PVWriterEvent<Object> event) {
			if(isControlPV)
				updateWritable(editpart.getWidgetModel(), pvMap.get(pvPropID));
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
	private boolean isBackColorAlarmSensitive;

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
	private volatile AtomicBoolean lastWriteAccess;
	private Cursor savedCursor;

	private Color saveForeColor, saveBackColor;
	//the task which will be executed when the updateSuppressTimer due.
	protected Runnable timerTask;

	//The update from PV will be suppressed for a brief time when writing was performed
	protected OPITimer updateSuppressTimer;
	private IPVWidgetModel widgetModel;
	private boolean isAllValuesBuffered;
	
	private ListenerList setPVValueListeners;
	private ListenerList alarmSeverityListeners;
	
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
						PV pv = BOYPVFactory.createPV((String) sp.getPropertyValue(), 
								isAllValuesBuffered);
						pvMap.put(sp.getPropertyID(), pv);
						editpart.addToConnectionHandler((String) sp.getPropertyValue(), pv);
						WidgetPVListener pvListener = new WidgetPVListener(sp.getPropertyID());
						pv.addListener(pvListener);
						if(pv instanceof PVManagerPV){
							((PVManagerPV)pv).addPVWriterListener(pvListener);
						}
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
		isBackColorAlarmSensitive = getWidgetModel().isBackColorAlarmSensitve();
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
				// No valid value is given. Do nothing.
				if (newValue == null || !(newValue instanceof IValue))
					return false;
				
				ISeverity newSeverity = ((IValue)newValue).getSeverity();
				alarmSeverity.copy(newSeverity);
				
				// Old value is not set. Force triggering listeners.
				if (oldValue == null || !(oldValue instanceof IValue)) {
					fireAlarmSeverityChanged(newSeverity, figure);
					return true;
				}

				// Compare the old severity with the new severity.
				// Trigger listeners only when they are different. 
				ISeverity oldSeverity = ((IValue)oldValue).getSeverity();
				if (oldSeverity.isOK() == newSeverity.isOK() &&
					oldSeverity.isMinor() == newSeverity.isMinor() &&
					oldSeverity.isMajor() == newSeverity.isMajor() &&
					oldSeverity.isInvalid() == newSeverity.isInvalid())
					return false;
					
				fireAlarmSeverityChanged(newSeverity, figure);
				return true;
			}
		};
		editpart.setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, valueHandler);
		
		// Border Alarm Sensitive
		addAlarmSeverityListener(new AlarmSeverityListener() {
			@Override
			public boolean severityChanged(ISeverity severity, IFigure figure) {
				if (!isBorderAlarmSensitive)
					return false;
				
				editpart.setFigureBorder(editpart.calculateBorder());
				return true;
			}
		});
		
		// BackColor Alarm Sensitive
		addAlarmSeverityListener(new AlarmSeverityListener() {
			@Override
			public boolean severityChanged(ISeverity severity, IFigure figure) {
				if (!isBackColorAlarmSensitive)
					return false;
				figure.setBackgroundColor(calculateBackColor());
				return true;
			}
		});
		
		// ForeColor Alarm Sensitive
		addAlarmSeverityListener(new AlarmSeverityListener() {
			@Override
			public boolean severityChanged(ISeverity severity, IFigure figure) {
				if (!isForeColorAlarmSensitive)
					return false;
				figure.setForegroundColor(calculateForeColor());
				return true;
			}
		});

		class PVNamePropertyChangeHandler implements IWidgetPropertyChangeHandler{
			private String pvNamePropID;
			public PVNamePropertyChangeHandler(String pvNamePropID) {
				this.pvNamePropID = pvNamePropID;
			}
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				PV oldPV = pvMap.get(pvNamePropID);
				editpart.removeFromConnectionHandler((String)oldValue);
				if(oldPV != null){
					oldPV.stop();
					oldPV.removeListener(pvListenerMap.get(pvNamePropID));
				}
				pvMap.remove(pvNamePropID);
				String newPVName = ((String)newValue).trim();	
				if(newPVName.length() <= 0)
					return false;
				try {
					lastWriteAccess = null;
					PV newPV = BOYPVFactory.createPV(newPVName, isAllValuesBuffered);					
					WidgetPVListener pvListener = new WidgetPVListener(pvNamePropID);
					newPV.addListener(pvListener);
					if(newPV instanceof PVManagerPV){
						((PVManagerPV)newPV).addPVWriterListener(pvListener);
					}
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
		
		if(editpart.getExecutionMode() ==  ExecutionMode.EDIT_MODE)
			editpart.getWidgetModel().getProperty(IPVWidgetModel.PROP_PVNAME).addPropertyChangeListener(new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					//reselect the widget to update feedback.
					int selected = editpart.getSelected();		
					if(selected != EditPart.SELECTED_NONE){
						editpart.setSelected(EditPart.SELECTED_NONE);
						editpart.setSelected(selected);
					}					
				}
			});
		
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
				isBackColorAlarmSensitive = (Boolean)newValue;
				figure.setBackgroundColor(calculateBackColor());
				return true;
			}
		};
		editpart.setPropertyChangeHandler(AbstractPVWidgetModel.PROP_BACKCOLOR_ALARMSENSITIVE, backColorAlarmSensitiveHandler);

		IWidgetPropertyChangeHandler foreColorAlarmSensitiveHandler = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				isForeColorAlarmSensitive = (Boolean)newValue;
				figure.setForegroundColor(calculateForeColor());
				return true;
			}
		};

		editpart.setPropertyChangeHandler(AbstractPVWidgetModel.PROP_FORECOLOR_ALARMSENSITIVE, foreColorAlarmSensitiveHandler);

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
	
	public Color calculateBackColor() {
		return calculateAlarmColor(isBackColorAlarmSensitive, saveBackColor);
	}
	
	public Color calculateForeColor() {
		return calculateAlarmColor(isForeColorAlarmSensitive, saveForeColor);
	}
	
	public Color calculateAlarmColor(boolean isSensitive, Color saveColor) {
		if (!isSensitive) {
			return saveColor;
		} else {
			RGB alarmColor = AlarmRepresentationScheme.getAlarmColor(alarmSeverity);
			if (alarmColor != null) {
				// Alarm severity is either "Major", "Minor" or "Invalid.
				return CustomMediaFactory.getInstance().getColor(alarmColor);
			} else {
				// Alarm severity is "OK".
				return saveColor;
			}
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
		Set<String> result = new HashSet<String>();

		for(StringProperty sp : editpart.getWidgetModel().getPVMap().keySet()){
			if(sp.isVisibleInPropSheet() && !((String)sp.getPropertyValue()).trim().isEmpty())
				result.add((String) sp.getPropertyValue());
		}
		return result.toArray(new String[result.size()]);
	}


	@Override
	public String getPVName() {
		if(getPV() != null)
			return getPV().getName();
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

	public boolean isAllValuesBuffered() {
		return isAllValuesBuffered;
	}

	public void setAllValuesBuffered(boolean isAllValuesBuffered) {
		this.isAllValuesBuffered = isAllValuesBuffered;
	}

	private void updateWritable(final AbstractWidgetModel widgetModel, PV pv) {
		if(lastWriteAccess == null || lastWriteAccess.get() != pv.isWriteAllowed()){
			if(lastWriteAccess == null)
				lastWriteAccess= new AtomicBoolean();
			lastWriteAccess.set(pv.isWriteAllowed());
			if(lastWriteAccess.get()){
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
	}

	public void addAlarmSeverityListener(AlarmSeverityListener listener) {
		if(alarmSeverityListeners == null){
			alarmSeverityListeners = new ListenerList();
		}
		alarmSeverityListeners.add(listener);		
	}
	
	private void fireAlarmSeverityChanged(ISeverity severity, IFigure figure) {
		if(alarmSeverityListeners == null)
			return;
		for(Object listener: alarmSeverityListeners.getListeners()){
			((AlarmSeverityListener)listener).severityChanged(severity, figure);
		}
	}
}
