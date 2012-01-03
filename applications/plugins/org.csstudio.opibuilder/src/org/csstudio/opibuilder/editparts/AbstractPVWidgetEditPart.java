/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editparts;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.IValue;
import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.dnd.DropPVtoPVWidgetEditPolicy;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.util.AlarmRepresentationScheme;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.OPITimer;
import org.csstudio.opibuilder.visualparts.BorderFactory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.thread.UIBundlingThread;
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
public abstract class AbstractPVWidgetEditPart extends AbstractWidgetEditPart{


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
				writeAccessMarked = true;
				if(pv.isWriteAllowed()){
					UIBundlingThread.getInstance().addRunnable(
							getViewer().getControl().getDisplay(),new Runnable(){
						public void run() {
							if(figure.getCursor() == Cursors.NO)
								figure.setCursor(savedCursor);
							figure.setEnabled(widgetModel.isEnabled());	
						}
					});
				}else{
					UIBundlingThread.getInstance().addRunnable(
							getViewer().getControl().getDisplay(),new Runnable(){
						public void run() {
							if(figure.getCursor() != Cursors.NO)
								savedCursor = figure.getCursor();
							figure.setEnabled(false);
							figure.setCursor(Cursors.NO);							
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
                        OPIBuilderPlugin.getLogger().log(Level.WARNING,
                                "Unable to connect to PV:" + (String)sp.getPropertyValue(), e); //$NON-NLS-1$
					}
				}

				doActivate();

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
		}
	};

	@Override
	protected ConnectionHandler createConnectionHandler() {
		return new PVWidgetConnectionHandler(this);
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(DropPVtoPVWidgetEditPolicy.DROP_PV_ROLE,
				new DropPVtoPVWidgetEditPolicy());
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


	/**
	 * @return the control PV. null if no control PV on this widget.
	 */
	public PV getControlPV(){
		if(controlPVPropId != null)
			return pvMap.get(controlPVPropId);
		return null;
	}
	public String getName() {
		 if(getWidgetModel().getPVMap().isEmpty())
			 return "";
		return (String)((StringProperty)getWidgetModel().getPVMap().keySet().toArray()[0])
			.getPropertyValue();
	}
	
	/**Get the PV corresponding to the <code>PV Name</code> property. 
	 * It is same as calling <code>getPV("pv_name")</code>.
	 * @return the PV corresponding to the <code>PV Name</code> property. 
	 * null if PV Name is not configured for this widget.
	 */
	public PV getPV(){
		return pvMap.get(AbstractPVWidgetModel.PROP_PVNAME);
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

//	public String getTypeId() {
//		return TYPE_ID;
//	}

	/**
	 * @return the time needed to suppress reading back from PV after writing.
	 * No need to suppress if returned value <=0
	 */
	protected int getUpdateSuppressTime(){
		return UPDATE_SUPPRESS_TIME;
	}

	/**The value of the widget that is in representing.
	 * It is not the value of the attached PV even though they are equals in most cases.
	 * The value type is specified by the widget, for example, boolean for boolean widget,
	 * double for meter and gauge.
	 * @return 	The value of the widget.
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
			setFigureBorder(BORDER_NO_ALARM);
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
		
		IWidgetPropertyChangeHandler borderHandler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				setFigureBorder(calculateBorder());
				return true;
			}
		};

		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_BORDER_ALARMSENSITIVE, borderHandler);
	
		
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
					setFigureBorder(calculateBorder());
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
		for(StringProperty pvNameProperty : getWidgetModel().getPVMap().keySet()){
			if(getExecutionMode() == ExecutionMode.RUN_MODE)
				setPropertyChangeHandler(pvNameProperty.getPropertyID(),
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
		setFigureBorder(calculateBorder());
		figure.setBackgroundColor(saveBackColor);
		figure.setForegroundColor(saveForeColor);
	}

	private void saveFigureOKStatus(IFigure figure) {
		saveForeColor = figure.getForegroundColor();
		saveBackColor = figure.getBackgroundColor();
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

	/**Set the value of the widget. This only takes effect on the visual presentation of the widget and
	 * will not write the value to the PV attached to this widget.
	 * @param value the value to be set. It must be the compatible type for the widget.
	 *  For example, a boolean widget only accept boolean or number.
	 * @throws RuntimeException if the value is not an acceptable type.
	 */	
	public void setValue(Object value){
		throw new RuntimeException("widget.setValue() does not accept " + value.getClass().getSimpleName());
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
		updateSuppressTimer.start(timerTask, getUpdateSuppressTime());
	}

	
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class key) {
		if(key == ProcessVariable.class){			
			return new ProcessVariable(getName());
		}
		return super.getAdapter(key);
	}
	
	@Override
	public Border calculateBorder(){
		isBorderAlarmSensitive = getWidgetModel().isBorderAlarmSensitve();
		if(!isBorderAlarmSensitive)
			return super.calculateBorder();
		else {
			Border alarmBorder;
			if(alarmSeverity.isOK()){
				if(getWidgetModel().getBorderStyle()== BorderStyle.NONE)
					alarmBorder = BORDER_NO_ALARM;
				else 
					alarmBorder = BorderFactory.createBorder(
							getWidgetModel().getBorderStyle(),
							getWidgetModel().getBorderWidth(),
							getWidgetModel().getBorderColor(),
							getWidgetModel().getName());
			}else if(alarmSeverity.isMajor()){
				alarmBorder = AlarmRepresentationScheme.getMajorBorder(getWidgetModel().getBorderStyle());
			}else if(alarmSeverity.isMinor()){
				alarmBorder = AlarmRepresentationScheme.getMinorBorder(getWidgetModel().getBorderStyle());
			}else{
				alarmBorder = AlarmRepresentationScheme.getInvalidBorder(getWidgetModel().getBorderStyle());
			}
			return alarmBorder;
		}
	}
}
