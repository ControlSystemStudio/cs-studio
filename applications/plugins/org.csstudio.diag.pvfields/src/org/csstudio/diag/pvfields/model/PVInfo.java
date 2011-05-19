/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.model;

/** Information about a PV.
 *  <p>
 *  Meant to be derived, and derived class must implement start(), stop(), getCurrentValue()
 *
 *  @author Dave Purcell
 *  @author Kay Kasemir
 */
public class PVInfo
{
    final private String pv_name, pv_type, field_name, field_type, orig_value;
    final private String fec, date, file_name;
    private PVFieldsModel model = null;

    /** Initialize
     *  @param pv_name PV Name
     *  @param pv_type PV Type
     *  @param fec Front end controller that contains this PV
     *  @param date Date of last PV configuration snapshot or FEC bootup
     *  @param file_name
     */
    public PVInfo(final String pv_name, final String pv_type, final String fec,
            final String date, final String file_name, final String field_name,
            final String field_type, final String orig_value
            )
    {
    	this.pv_name = pv_name;
        this.pv_type = pv_type;
        this.fec = fec;
        this.date = date;
        this.file_name = file_name;
        this.field_name = field_name;
        this.field_type = field_type;
        this.orig_value = orig_value;
    }

    /** At least for EPICS, the value of a field can include the name of another
     *  PV. In other cases, it's just a numeric or string value.
     *
     *  So that we can use these values in eclipse as selection providers
     *  we may need to manipulate them.
     *  Depending on the live value we may want the selection providers to return
     *  a new PV name (a link) or just the PV or the PV.field as a PV itself.
     *  Depends on your needs.
     *  Obviously, it can also just return the name of the control system item.
     *
     *  @return Possibly a PV name that we extracted from the value
	*/
    public String getName()
    {
        return pv_name;
    }

    /** @return PV Name as just string*/
    public String getPVName()
    {
        return pv_name;
    }

    /** @return PV Type */
    public String getType()
    {
        return pv_type;
    }

    /** @return Name of front end controller */
    public String getFEC()
    {
        return fec;
    }

    /** @return Date of last FEC boot-up of PV configuration snapshot */
    public String getDate()
    {
        return date;
    }

    /** @return Name of file that contained original definition */
    public String getFileName()
    {
        return file_name;
    }

    /** @return Name of file that contained original definition */
    public String getFieldName()
    {
        return field_name;
    }

    /** @return Name of file that contained original definition */
    public String getFieldType()
    {
        return field_type;
    }

    /** @return Name of file that contained original definition */
    public String getOrigValue()
    {
        return orig_value;
    }


    /** @param model Model that the field should notify when new values
     *               arrive
     */
    void setModel(final PVFieldsModel model)
    {
        if (this.model != null)
            throw new Error("Model already set"); //$NON-NLS-1$
        this.model = model;
    }

    /** @return Model or <code>null</code> if not set */
    PVFieldsModel getModel()
    {
    	return model;
    }

    /** Connect to control system to update current values */
    public void start() throws Exception
    {
    	// NOP
    }

    /** Disconnect from the control system, stop updating current values */
    public void stop()
    {
    	// NOP
    }

    /** @return String for first Column in table */
    public String getFirstColumn()
    {
        final PVFieldsModel model = getModel();
        if (model != null  && model.alterColumnData())
    		return field_name;
        else return pv_name;
    }

    /** @return Type of this field */
    public String getSecondColumn()
    {
        final PVFieldsModel model = getModel();
        if (model != null  && model.alterColumnData())
        	return field_type;
        return field_name;
    }

    /** @return Current value of the field     */
    public String getCurrentValue()
    {
    	return ""; //$NON-NLS-1$
    }


    /** Called by derived implementation to notify model
     *  about a new 'live' (current) value
     */
    protected void fireModelUpdate()
    {
        final PVFieldsModel model = getModel();
        if (model != null)
            model.fireFieldChanged(this);
    }

    /**
     * @return String representation of field, used for example when printing
     *         fields in JUnit test or when viewing field in debugger
     */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "PV " + pv_name + " field " + field_name + " = " + orig_value;
    }
}
