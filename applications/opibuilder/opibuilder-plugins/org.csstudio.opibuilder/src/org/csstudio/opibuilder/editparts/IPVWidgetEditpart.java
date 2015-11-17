package org.csstudio.opibuilder.editparts;

import org.csstudio.simplepv.IPV;
import org.diirt.vtype.VType;


public interface IPVWidgetEditpart {

    public interface ISetPVValueListener{
        /**Called before setting PV Value;
         * @param pvPropId
         * @param value
         */
        public void beforeSetPVValue(String pvPropId, Object value);

    }

    /**
     * @return A String array with all PV names from PV properties.
     * It only returns the visible and nonempty PV properties.
     */
    public String[] getAllPVNames();

    /**
     * @return the control PV. null if no control PV on this widget.
     */
    public IPV getControlPV();

    /**
     * @return the major PV.
     */
    public IPV getPV();

    /**
     * @return name of the major PV.
     */
    public String getPVName();

    /**Get the pv by PV property id.
     * @param pvPropId the PV property id.
     * @return the corresponding pv for the pvPropId. null if the pv doesn't exist.
     */
    public IPV getPV(String pvPropId);

    /**Get value from one of the attached PVs.
     * @param pvPropId the property id of the PV. It is "pv_name" for the main PV.
     * @return the value of the PV.
     */
    public VType getPVValue(String pvPropId);

    /**Set PV to given value. Should accept Double, Double[], Integer, String, maybe more.
     * @param pvPropId
     * @param value
     */
    public void setPVValue(String pvPropId, Object value);

    public void addSetPVValueListener(ISetPVValueListener listener);

    public void setControlEnabled(boolean enabled);

    public boolean isPVControlWidget();

}
