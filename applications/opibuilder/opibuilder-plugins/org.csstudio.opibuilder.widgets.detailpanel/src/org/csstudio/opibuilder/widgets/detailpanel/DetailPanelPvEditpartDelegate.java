package org.csstudio.opibuilder.widgets.detailpanel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.editparts.IPVWidgetEditpart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.IPVWidgetModel;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.util.BOYPVFactory;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.OPITimer;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.graphics.Cursor;
import org.diirt.vtype.VType;

// This class is hacked down from PVWidgetEditpartDelegate
// All alarm sensitive handling has been removed.

public class DetailPanelPvEditpartDelegate implements IPVWidgetEditpart {
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

    private int updateSuppressTime = 1000;
    private String controlPVPropId = null;

    private String controlPVValuePropId = null;
    /**
     * In most cases, old pv value in the valueChange() method of {@link IWidgetPropertyChangeHandler}
     * is not useful. Ignore the old pv value will help to reduce memory usage.
     */
    private boolean ignoreOldPVValue =true;

    private Map<String, IPVListener> pvListenerMap = new HashMap<String, IPVListener>();

    private Map<String, IPV> pvMap = new HashMap<String, IPV>();
    private PropertyChangeListener[] pvValueListeners;
    private DetailPanelEditpart editpart;
    private volatile AtomicBoolean lastWriteAccess;
    private Cursor savedCursor;

    //the task which will be executed when the updateSuppressTimer due.
    protected Runnable timerTask;

    //The update from PV will be suppressed for a brief time when writing was performed
    protected OPITimer updateSuppressTimer;
    private IPVWidgetModel widgetModel;
    private boolean isAllValuesBuffered;

    private ListenerList setPVValueListeners;

    /**
     * @param editpart the editpart to be delegated.
     * It must implemented {@link IPVWidgetEditpart}
     */
    public DetailPanelPvEditpartDelegate(DetailPanelEditpart editpart) {
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
            final Map<StringProperty, PVValueProperty> pvPropertyMap = editpart.getWidgetModel().getPVMap();
            for(final StringProperty sp : pvPropertyMap.keySet()){
                if(sp.getPropertyValue() == null ||
                        ((String)sp.getPropertyValue()).trim().length() <=0) {
                    continue;
                }
                try {
                    IPV pv = BOYPVFactory.createPV((String) sp.getPropertyValue(),
                            isAllValuesBuffered);
                    pvMap.put(sp.getPropertyID(), pv);
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

    /**Start all PVs.
     * This should be called as the last step in editpart.activate().
     */
    public void startPVs() {
        //the pv should be started at the last minute
        for(String pvPropId : pvMap.keySet()){
            IPV pv = pvMap.get(pvPropId);
            try {
                pv.start();
            } catch (Exception e) {
                OPIBuilderPlugin.getLogger().log(Level.WARNING,
                        "Unable to connect to PV:" + pv.getName(), e); //$NON-NLS-1$
            }
        }
    }

    public void doDeActivate() {
            for(IPV pv : pvMap.values())
                pv.stop();

            for(String pvPropID : pvListenerMap.keySet()){
                pvMap.get(pvPropID).removeListener(pvListenerMap.get(pvPropID));
            }

            pvMap.clear();
            pvListenerMap.clear();
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
        class PVNamePropertyChangeHandler implements IWidgetPropertyChangeHandler{
            private String pvNamePropID;
            public PVNamePropertyChangeHandler(String pvNamePropID) {
                this.pvNamePropID = pvNamePropID;
            }
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                IPV oldPV = pvMap.get(pvNamePropID);
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

    @Override
    public void setControlEnabled(boolean enabled) {
        // Do nothing for now.
    }

}
