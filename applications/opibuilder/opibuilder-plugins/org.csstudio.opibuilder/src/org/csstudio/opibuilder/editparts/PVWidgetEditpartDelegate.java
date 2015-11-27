package org.csstudio.opibuilder.editparts;

import static org.diirt.datasource.formula.ExpressionLanguage.formula;
import static org.diirt.util.time.TimeDuration.ofMillis;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.java.thread.ExecutionService;
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
import org.csstudio.opibuilder.util.BeastAlarmSeverityLevel;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.util.OPITimer;
import org.csstudio.opibuilder.visualparts.BorderFactory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.csstudio.simplepv.VTypeHelper;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.thread.UIBundlingThread;
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
import org.eclipse.swt.widgets.Display;
import org.diirt.datasource.PV;
import org.diirt.datasource.PVManager;
import org.diirt.datasource.PVReaderEvent;
import org.diirt.datasource.PVReaderListener;
import org.diirt.datasource.expression.DesiredRateReadWriteExpression;
import org.diirt.util.time.TimeDuration;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VTable;
import org.diirt.vtype.VType;

public class PVWidgetEditpartDelegate implements IPVWidgetEditpart {
//    private interface AlarmSeverity extends ISeverity{
//        public void copy(ISeverity severity);
//    }
    private final class WidgetPVListener extends IPVListener.Stub{
        private String pvPropID;
        private boolean isControlPV;

        public WidgetPVListener(String pvPropID) {
            this.pvPropID = pvPropID;
            isControlPV = pvPropID.equals(controlPVPropId);
        }

        @Override
        public void connectionChanged(IPV pv) {
            if(!pv.isConnected())
                lastWriteAccess = null;
        }

        @Override
        public void valueChanged(IPV pv) {

            final AbstractWidgetModel widgetModel = editpart.getWidgetModel();

            //write access
//            if(isControlPV)
//                updateWritable(widgetModel, pv);

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
        public void writePermissionChanged(IPV pv) {
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

    private static final Logger log = Logger.getLogger("BeastDataSource TESTING");
    
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
    
    private Map<String, IPVListener> pvListenerMap = new HashMap<String, IPVListener>();

    private Map<String, IPV> pvMap = new HashMap<String, IPV>();
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
    private boolean isAlarmPulsing = false;
    private ScheduledFuture<?> scheduledFuture;

    private boolean pvsHaveBeenStarted = false;

    // determines whether a secondary PV is used for alarm-sensitive behaviour 
    private boolean isAlarmPVUsedForAlarmSensitivity;
    // (secondary, alarm) PV on which we listen for Alarm State changes
    private PV<?, Object> alarmPV = null;

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

    private static Executor swtThread(final Display display) {
        return new Executor() {
            @Override
            public void execute(Runnable task) {
                try {
                    if (!display.isDisposed())
                        display.asyncExec(task);
                    else
                    	Logger.getLogger("BeastDataSource TESTING").severe("Display is DISPOSED");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
    
    public void doActivate(){
        saveFigureOKStatus(editpart.getFigure());
        if(editpart.getExecutionMode() == ExecutionMode.RUN_MODE){
            pvMap.clear();
            final Map<StringProperty, PVValueProperty> pvPropertyMap = editpart.getWidgetModel().getPVMap();

            for(final StringProperty sp : pvPropertyMap.keySet()){

                if(sp.getPropertyValue() == null ||
                        ((String)sp.getPropertyValue()).trim().length() <=0)
                    continue;

                try {
                    IPV pv = BOYPVFactory.createPV((String) sp.getPropertyValue(),
                            isAllValuesBuffered);
                    pvMap.put(sp.getPropertyID(), pv);
                    editpart.addToConnectionHandler((String) sp.getPropertyValue(), pv);
                    WidgetPVListener pvListener = new WidgetPVListener(sp.getPropertyID());
                    pv.addListener(pvListener);
                    pvListenerMap.put(sp.getPropertyID(), pvListener);
                } catch (Exception e) {
                    OPIBuilderPlugin.getLogger().log(Level.WARNING,
                            "Unable to connect to PV:" + (String)sp.getPropertyValue(), e); //$NON-NLS-1$
                }
            }
        }
    }

    private void createAlarmPVReader() {
    	if (alarmPV != null) alarmPV.close();
    	
    	DesiredRateReadWriteExpression<?,Object> expr = formula(getWidgetModel().getAlarmPVName());
    	alarmPV = PVManager
        		.readAndWrite(expr)
        		.timeout(ofMillis(1000))
                .notifyOn(swtThread(editpart.getViewer().getControl().getDisplay()))
                .readListener(new PVReaderListener<Object>() {
					@Override
                    public void pvChanged(PVReaderEvent<Object> event) {
                    	log.info("Received new event of type " + event.toString());
                    	if (event.isExceptionChanged()) {
                    		Exception e = event.getPvReader().lastException();
                    		log.severe("Received EXCEPTION: " + e.toString());
                    		e.printStackTrace();
                    	}

                    	if (!event.isValueChanged()) return;
                    	if (!(event.getPvReader().getValue() instanceof VTable)) {
                    		log.severe("BeastDataSource listener: data is not a VTable");
                    		return;
                    	}
                    	
                    	AlarmSeverity newSeverity;
                    	VTable allData = (VTable) event.getPvReader().getValue();
                    	if (allData == null) return;
                    	
                    	@SuppressWarnings("unchecked")
						List<String> data = (List<String>) allData.getColumnData(1);

                    	// TODO: find correct column instead of using hardcoded indexes
                    	if (data.get(6).equalsIgnoreCase("false")){
                    		// Disabled
                    		newSeverity = AlarmSeverity.NONE;
                    	} else {
                    		BeastAlarmSeverityLevel level = BeastAlarmSeverityLevel.parse(data.get(1)); 
                    		newSeverity = level.getAlarmSeverity();
                    	}
                    	
    	                if (newSeverity != alarmSeverity) {
    	                    alarmSeverity = newSeverity;
    	                    fireAlarmSeverityChanged(newSeverity, editpart.getFigure());
        	                if (alarmSeverity != AlarmSeverity.NONE) Display.getDefault().timerExec(500, () -> alarmPV.write("ack"));
    	                }


                	}
                })
              .asynchWriteAndMaxReadRate(TimeDuration.ofHertz(10));
    	
    	if (!alarmPV.isWriteConnected())
    		log.severe("Alarm PV is not Write Connected !");
    }
    
    /**Start all PVs.
     * This should be called as the last step in editpart.activate().
     */
    public void startPVs() {
        pvsHaveBeenStarted = true;
        //the pv should be started at the last minute
        log.info("STARTING PVs");
        for(String pvPropId : pvMap.keySet()){
            IPV pv = pvMap.get(pvPropId);
            try {
                pv.start();
            } catch (Exception e) {
                OPIBuilderPlugin.getLogger().log(Level.WARNING,
                        "Unable to connect to PV:" + pv.getName(), e); //$NON-NLS-1$
            }
        }
        
        if (isAlarmPVUsedForAlarmSensitivity && editpart.getExecutionMode() == ExecutionMode.RUN_MODE)
        {
//        	Display.getDefault().asyncExec(() -> createAlarmPVReader());
        	createAlarmPVReader();
    	}        
    }

    public void doDeActivate() {
        if (pvsHaveBeenStarted) {
            for(IPV pv : pvMap.values())
                pv.stop();
            pvsHaveBeenStarted = false;
        }

        // disconnect the AlarmPV
        if (alarmPV != null) {
        	alarmPV.close();
        	alarmPV = null;
        }

        for(String pvPropID : pvListenerMap.keySet()){
                pvMap.get(pvPropID).removeListener(pvListenerMap.get(pvPropID));
        }

        pvMap.clear();
        pvListenerMap.clear();
        stopPulsing();
    }

    public IPV getControlPV(){
        if(controlPVPropId != null)
            return pvMap.get(controlPVPropId);
        return null;
    }


    /**Get the PV corresponding to the <code>PV Name</code> property.
     * It is same as calling <code>getPV("pv_name")</code>.
     * @return the PV corresponding to the <code>PV Name</code> property.
     * null if PV Name is not configured for this widget.
     */
    public IPV getPV(){
        return pvMap.get(IPVWidgetModel.PROP_PVNAME);
    }

    /**Get the pv by PV property id.
     * @param pvPropId the PV property id.
     * @return the corresponding pv for the pvPropId. null if the pv doesn't exist.
     */
    public IPV getPV(String pvPropId){
        return pvMap.get(pvPropId);
    }

    /**Get value from one of the attached PVs.
     * @param pvPropId the property id of the PV. It is "pv_name" for the main PV.
     * @return the {@link IValue} of the PV.
     */
    public VType getPVValue(String pvPropId){
        final IPV pv = pvMap.get(pvPropId);
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
        isAlarmPulsing = getWidgetModel().isAlarmPulsing();
        isAlarmPVUsedForAlarmSensitivity = getWidgetModel().isAlarmPVEnabled() && (getWidgetModel().getAlarmPVName() != null
        		&& !getWidgetModel().getAlarmPVName().trim().equals(""));

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

                if (!isAlarmPVUsedForAlarmSensitivity) {
	                AlarmSeverity newSeverity = VTypeHelper.getAlarmSeverity((VType) newValue);
	                if(newSeverity == null)
	                    return false;
	
	                if (newSeverity != alarmSeverity) {
	                    alarmSeverity = newSeverity;
	                    fireAlarmSeverityChanged(newSeverity, figure);
	                }
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

        // Pulsing Alarm Sensitive
        addAlarmSeverityListener(new AlarmSeverityListener() {
            @Override
            public boolean severityChanged(AlarmSeverity severity, IFigure figure) {
                if (!isAlarmPulsing)
                    return false;
                if (severity == AlarmSeverity.MAJOR || severity == AlarmSeverity.MINOR) {
                    startPulsing();
                } else {
                    stopPulsing();
                }
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
                IPV oldPV = pvMap.get(pvNamePropID);
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
                    IPV newPV = BOYPVFactory.createPV(newPVName, isAllValuesBuffered);
                    WidgetPVListener pvListener = new WidgetPVListener(pvNamePropID);
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

        IWidgetPropertyChangeHandler backColorHandler = new IWidgetPropertyChangeHandler(){
            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                saveBackColor = ((OPIColor)newValue).getSWTColor();
                return false;
            }
        };
        editpart.setPropertyChangeHandler(AbstractWidgetModel.PROP_COLOR_BACKGROUND, backColorHandler);

        IWidgetPropertyChangeHandler foreColorHandler = new IWidgetPropertyChangeHandler(){
            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                saveForeColor = ((OPIColor)newValue).getSWTColor();
                return false;
            }
        };
        editpart.setPropertyChangeHandler(AbstractWidgetModel.PROP_COLOR_FOREGROUND, foreColorHandler);

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

        IWidgetPropertyChangeHandler alarmPulsingHandler = new IWidgetPropertyChangeHandler() {

            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                isAlarmPulsing = (Boolean)newValue;
                stopPulsing();
                fireAlarmSeverityChanged(alarmSeverity, figure);
                return true;
            }
        };

        editpart.setPropertyChangeHandler(AbstractPVWidgetModel.PROP_ALARM_PULSING, alarmPulsingHandler);


    }

    public synchronized void stopPulsing() {
        if (scheduledFuture != null) {
            // stop the pulsing runnable
            scheduledFuture.cancel(true);
            scheduledFuture = null;
        }
    }

    public synchronized void startPulsing() {
        stopPulsing();
        Runnable pulsingTask = new Runnable() {
            public void run() {
                UIBundlingThread.getInstance().addRunnable(new Runnable() {

                    public void run() {
                        synchronized (PVWidgetEditpartDelegate.this) {
                            // Change the colours of all alarm sensitive components
                            if (isBackColorAlarmSensitive)
                                editpart.getFigure().setBackgroundColor(calculateBackColor());
                            if (isForeColorAlarmSensitive)
                                editpart.getFigure().setForegroundColor(calculateForeColor());
                        }
                    }
                });
            }
        };
        scheduledFuture = ExecutionService
                .getInstance()
                .getScheduledExecutorService()
                .scheduleAtFixedRate(pulsingTask, PreferencesHelper.getGUIRefreshCycle(), PreferencesHelper.getGUIRefreshCycle(),
                        TimeUnit.MILLISECONDS);
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
                if (isAlarmPulsing &&
                        (alarmSeverity == AlarmSeverity.MINOR || alarmSeverity == AlarmSeverity.MAJOR)) {
                    double alpha = 0.3;
                    int period;
                    if (alarmSeverity == AlarmSeverity.MINOR) {
                        period = PreferencesHelper.getPulsingAlarmMinorPeriod();
                    } else {
                        period = PreferencesHelper.getPulsingAlarmMajorPeriod();
                    }
                    alpha += Math.abs(System.currentTimeMillis() % period - period / 2) / (double) period;
                    alarmColor = new RGB(
                            (int) (saveColor.getRed() * alpha + alarmColor.red * (1-alpha)),
                            (int) (saveColor.getGreen() * alpha + alarmColor.green * (1-alpha)),
                            (int) (saveColor.getBlue() * alpha + alarmColor.blue * (1-alpha)));
                }
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
        final IPV pv = pvMap.get(pvPropId);
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

    private void updateWritable(final AbstractWidgetModel widgetModel, IPV pv) {
        if(lastWriteAccess == null || lastWriteAccess.get() != pv.isWriteAllowed()){
            if(lastWriteAccess == null)
                lastWriteAccess= new AtomicBoolean();
            lastWriteAccess.set(pv.isWriteAllowed());
            if(lastWriteAccess.get()){
                UIBundlingThread.getInstance().addRunnable(
                        editpart.getViewer().getControl().getDisplay(),new Runnable(){
                    public void run() {
                        setControlEnabled(true);
                    }
                });
            } else {
                UIBundlingThread.getInstance().addRunnable(
                        editpart.getViewer().getControl().getDisplay(),new Runnable(){
                    public void run() {
                        setControlEnabled(false);
                    }
                });
            }
        }
    }

    /**
     * Set whether the editpart is enabled for PV control.  Disabled
     * editparts have greyed-out figures, and the cursor is set to a cross.
     */
    @Override
    public void setControlEnabled(boolean enabled) {
        if (enabled) {
            IFigure figure = editpart.getFigure();
            if(figure.getCursor() == Cursors.NO)
                figure.setCursor(savedCursor);
            figure.setEnabled(editpart.getWidgetModel().isEnabled());
            figure.repaint();
        } else {
            IFigure figure = editpart.getFigure();
            if(figure.getCursor() != Cursors.NO)
                savedCursor = figure.getCursor();
            figure.setEnabled(false);
            figure.setCursor(Cursors.NO);
            figure.repaint();
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
