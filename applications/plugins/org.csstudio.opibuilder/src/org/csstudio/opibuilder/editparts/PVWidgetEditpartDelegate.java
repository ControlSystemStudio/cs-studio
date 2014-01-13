package org.csstudio.opibuilder.editparts;

import static org.epics.pvmanager.ExpressionLanguage.*;
import static org.epics.pvmanager.vtype.ExpressionLanguage.*;
import static org.epics.pvmanager.formula.ExpressionLanguage.*;
import static org.epics.util.time.TimeDuration.ofMillis;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.IPVWidgetModel;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.util.AlarmRepresentationScheme;
import org.csstudio.opibuilder.util.BOYPVFactory;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.OPITimer;
import org.csstudio.opibuilder.visualparts.BorderFactory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.csstudio.simplepv.VTypeHelper;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
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
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReaderConfiguration;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.PVWriterEvent;
import org.epics.pvmanager.PVWriterListener;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.DesiredRateReadWriteExpression;
import org.epics.pvmanager.expression.DesiredRateReadWriteExpressionImpl;
import org.epics.pvmanager.expression.WriteExpression;
import org.epics.util.time.TimeDuration;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VType;

public class PVWidgetEditpartDelegate implements IPVWidgetEditpart {
//	private interface AlarmSeverity extends ISeverity{
//		public void copy(ISeverity severity);
//	}
	private final class WidgetPVListener implements PVReaderListener<Object>, PVWriterListener<Object> {
		private String pvPropID;
		private boolean isControlPV;

		public WidgetPVListener(String pvPropID) {
			this.pvPropID = pvPropID;
			isControlPV = pvPropID.equals(controlPVPropId);
		}


		@Override
		public void pvChanged(PVWriterEvent<Object> event) {
			if (event.isConnectionChanged()) {
				if(isControlPV)
					updateWritable(editpart.getWidgetModel(), pvMap.get(pvPropID));
			}
		}

		@Override
		public void pvChanged(PVReaderEvent<Object> event) {
			if (event.isConnectionChanged()) {
				if (!event.getPvReader().isConnected()) {
					lastWriteAccess = null;
				}
				if(isControlPV)
					updateWritable(editpart.getWidgetModel(), pvMap.get(pvPropID));
			}
			if (event.isValueChanged()) {
				final AbstractWidgetModel widgetModel = editpart.getWidgetModel();

				if (event.getPvReader().getValue() != null) {
					Object value = event.getPvReader().getValue();
					if (value instanceof List) {
						List<?> list = (List<?>) value;
						if (!list.isEmpty()) {
							value = list.get(list.size() - 1);
						} else {
							value = null;
						}
					}
					if (ignoreOldPVValue) {
						widgetModel.getPVMap()
								.get(widgetModel.getProperty(pvPropID))
								.setPropertyValue_IgnoreOldValue(value);
					} else
						widgetModel.getPVMap()
								.get(widgetModel.getProperty(pvPropID))
								.setPropertyValue(value);
				}
				
				processValueEvent(event);
			}
		}
	}
	
	protected void processValueEvent(PVReaderEvent<Object> event) {
		// Does nothing
		// Can be overridden
		if (editpart instanceof AbstractPVWidgetEditPart) {
			((AbstractPVWidgetEditPart) editpart).processValueEvent(event);
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
	private AlarmSeverity alarmSeverity = AlarmSeverity.NONE;

	private Map<String, PV<Object, Object>> pvMap = new HashMap<>();
	private PropertyChangeListener[] pvValueListeners;
	private AbstractBaseEditPart editpart;
	private Boolean lastWriteAccess;
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
						PV pv = createPV((String) sp.getPropertyValue(), 
								isAllValuesBuffered, sp.getPropertyID());
						pvMap.put(sp.getPropertyID(), pv);
						//editpart.addToConnectionHandler((String) sp.getPropertyValue(), pv);
					} catch (Exception e) {
                        OPIBuilderPlugin.getLogger().log(Level.WARNING,
                                "Unable to connect to PV:" + (String)sp.getPropertyValue(), e); //$NON-NLS-1$
					}
				}
			}
	}
	
	private PV<Object, Object> createPV(String name, boolean valueBuffered, String propertyId) {
		String singleChannel = channelFromFormula(name); // null means formula
		boolean isFormula = singleChannel == null;
		if (isFormula) {
			valueBuffered = false; // the value from a formula cannot be
									// buffered.
		} else {
			name = singleChannel;
		}
		
		DesiredRateExpression<?> readExpression = null;
		WriteExpression<?> writeExpression = null;
		if (valueBuffered) {
			readExpression = newValuesOf(vType(name));
			// TODO: if it's read-only this may not be created
			writeExpression = vType(name);
		} else {
			DesiredRateReadWriteExpression<?, Object> formulaExpression = formula(name);
			readExpression = formulaExpression;
			writeExpression = formulaExpression;
		}
		
		@SuppressWarnings("unchecked")
		DesiredRateReadWriteExpression<Object, Object> finalExpression =
				new DesiredRateReadWriteExpressionImpl<Object, Object>(
						(DesiredRateExpression<Object>) readExpression, (WriteExpression<Object>) writeExpression); 
		
		WidgetPVListener pvListener = new WidgetPVListener(propertyId);
		return PVManager.readAndWrite(finalExpression).notifyOn(SWTUtil.swtThread(editpart.getViewer().getControl().getDisplay()))
				.readListener(pvListener)
				.writeListener(pvListener)
				.asynchWriteAndMaxReadRate(TimeDuration.ofMillis(PreferencesHelper.getGUIRefreshCycle()));
	}

	/**Start all PVs.
	 * This should be called as the last step in editpart.activate().
	 */
	public void startPVs() {
	}
	
	public void doDeActivate() {
			for(PV pv : pvMap.values())
				pv.close();

			pvMap.clear();
	}
	
	public void pause() {
		for (PV pv : pvMap.values()) {
			pv.setPaused(true);
		}
	}
	
	public void resume() {
		for (PV pv : pvMap.values()) {
			pv.setPaused(false);
		}
	}
	
	public PV<Object, Object> getControlPV(){
		if(controlPVPropId != null)
			return pvMap.get(controlPVPropId);
		return null;
	}
	
	
	/**Get the PV corresponding to the <code>PV Name</code> property. 
	 * It is same as calling <code>getPV("pv_name")</code>.
	 * @return the PV corresponding to the <code>PV Name</code> property. 
	 * null if PV Name is not configured for this widget.
	 */
	public PV<Object, Object> getPV(){
		return pvMap.get(IPVWidgetModel.PROP_PVNAME);
	}

	/**Get the pv by PV property id.
	 * @param pvPropId the PV property id.
	 * @return the corresponding pv for the pvPropId. null if the pv doesn't exist.
	 */
	public PV<Object, Object> getPV(String pvPropId){
		return pvMap.get(pvPropId);
	}

	/**Get value from one of the attached PVs.
	 * @param pvPropId the property id of the PV. It is "pv_name" for the main PV.
	 * @return the {@link IValue} of the PV.
	 */
	public VType getPVValue(String pvPropId){
		final PV<Object, Object> pv = pvMap.get(pvPropId);
		if(pv != null){
			return (VType) pv.getValue();
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
				if (newValue == null || !(newValue instanceof VType))
					return false;
				
				AlarmSeverity newSeverity = VTypeHelper.getAlarmSeverity((VType) newValue);
				if(newSeverity == null)
					return false;			
				
				if (newSeverity != alarmSeverity) {
					alarmSeverity = newSeverity;
					fireAlarmSeverityChanged(newSeverity, figure);
				}
				return true;
			}
		};
		editpart.setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, valueHandler);
		
		// Border Alarm Sensitive
		addAlarmSeverityListener(new AlarmSeverityListener() {
			@Override
			public boolean severityChanged(AlarmSeverity severity, IFigure figure) {
				if (!isBorderAlarmSensitive)
					return false;
				
				editpart.setFigureBorder(editpart.calculateBorder());
				return true;
			}
		});
		
		// BackColor Alarm Sensitive
		addAlarmSeverityListener(new AlarmSeverityListener() {
			@Override
			public boolean severityChanged(AlarmSeverity severity, IFigure figure) {
				if (!isBackColorAlarmSensitive)
					return false;
				figure.setBackgroundColor(calculateBackColor());
				return true;
			}
		});
		
		// ForeColor Alarm Sensitive
		addAlarmSeverityListener(new AlarmSeverityListener() {
			@Override
			public boolean severityChanged(AlarmSeverity severity, IFigure figure) {
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
					oldPV.close();
				}
				pvMap.remove(pvNamePropID);
				String newPVName = ((String)newValue).trim();	
				if(newPVName.length() <= 0)
					return false;
				try {
					lastWriteAccess = null;
					PV newPV = createPV(newPVName, isAllValuesBuffered, pvNamePropID);					
//					editpart.addToConnectionHandler(newPVName, newPV);
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
			switch (alarmSeverity) {
			case NONE:
				if(editpart.getWidgetModel().getBorderStyle()== BorderStyle.NONE)
					alarmBorder = BORDER_NO_ALARM;
				else 
					alarmBorder = BorderFactory.createBorder(
							editpart.getWidgetModel().getBorderStyle(),
							editpart.getWidgetModel().getBorderWidth(),
							editpart.getWidgetModel().getBorderColor(),
							editpart.getWidgetModel().getName());
				break;
			case MAJOR:
				alarmBorder = AlarmRepresentationScheme.getMajorBorder(editpart.getWidgetModel().getBorderStyle());
				break;
			case MINOR:
				alarmBorder = AlarmRepresentationScheme.getMinorBorder(editpart.getWidgetModel().getBorderStyle());
				break;
			case INVALID:
			case UNDEFINED:
			default:
				alarmBorder = AlarmRepresentationScheme.getInvalidBorder(editpart.getWidgetModel().getBorderStyle());
				break;
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
				pv.write(value);
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
		if(lastWriteAccess == null || lastWriteAccess != pv.isWriteConnected()){
			if(lastWriteAccess == null)
				lastWriteAccess= false;
			lastWriteAccess = pv.isWriteConnected();
			if(lastWriteAccess){
				UIBundlingThread.getInstance().addRunnable(
						editpart.getViewer().getControl().getDisplay(),new Runnable(){
					public void run() {
						IFigure figure = editpart.getFigure();
						if(figure.getCursor() == Cursors.NO)
							figure.setCursor(savedCursor);
						figure.setEnabled(widgetModel.isEnabled());	
						figure.repaint();
					}
				});
			}else{
				UIBundlingThread.getInstance().addRunnable(
						editpart.getViewer().getControl().getDisplay(),new Runnable(){
					public void run() {
						IFigure figure = editpart.getFigure();
						if(figure.getCursor() != Cursors.NO)
							savedCursor = figure.getCursor();
						figure.setEnabled(false);
						figure.setCursor(Cursors.NO);		
						figure.repaint();
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
	
	private void fireAlarmSeverityChanged(AlarmSeverity severity, IFigure figure) {
		if(alarmSeverityListeners == null)
			return;
		for(Object listener: alarmSeverityListeners.getListeners()){
			((AlarmSeverityListener)listener).severityChanged(severity, figure);
		}
	}
}
