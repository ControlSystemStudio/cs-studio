/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.sns;

import org.csstudio.data.values.ValueUtil;
import org.csstudio.diag.pvfields.model.PVInfo;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

/** PVField with 'current' value from EPICS
 *
 * @author Dave Purcell
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSPVField extends PVInfo implements PVListener
{
	private final String pv_name, field_name, orig_value;

    /** PV used to subscribe to 'live' value */
    private volatile PV pv = null;

    /** Most recent 'live' value */
    private volatile String current_value = "";


    public SNSPVField(final String pv_name, final String pv_type, final String fec,
            final String date, final String file_name, final String field_name,
            final String field_type, final String orig_value)
    {
        super(pv_name, pv_type,fec, date, file_name, field_name, field_type, orig_value);
        this.pv_name = pv_name;
        this.field_name = field_name;
        this.orig_value = orig_value;
        }

    @Override
    public String getCurrentValue()
    {
        return current_value;
    }

    @Override
    public String getName()
    {
        final String value;

        if (current_value.length() > 0)
            value = current_value;
        else
            value = orig_value;

        if (value == null) return pv_name;

        if (value.endsWith(" CCP") || value.endsWith(" MS")
                || value.endsWith(" PP")
                || value.endsWith(" NMS")
                || value.endsWith(" .PP")
                || value.endsWith(" .NMS")
                || value.endsWith(" CP")
                || value.endsWith(" CA"))
        {

        	if (value.contains(".VAL") )
            {
                // return Link minus the .VAL
                return value.substring(0, value.indexOf("."));
            }

        	// return value stripped of above codes.
            return value.substring(0, value.indexOf(" "));

        }
        else if (field_name.equals("VAL") )
        {
            // return PV name
            return pv_name;
        }
        else if (field_name.equals("FLNK") )
        {
            // return PV name
            return value;
        }
        else // return pv-dot-field
            return pv_name + "." + field_name;
    }

    @Override
    public void start() throws Exception
    {
        pv = PVFactory.createPV(pv_name + "." + field_name);
        pv.addListener(this);
        pv.start();
    }

    @Override
    public void stop()
    {
        if (pv != null)
            pv.stop();
        pv = null;
    }

    // PVListener
    @Override
    public void pvDisconnected(final PV pv)
    {
    	current_value = "";
        fireModelUpdate();
    }

    // PVListener
    @Override
    public void pvValueUpdate(final PV pv)
    {
        current_value = ValueUtil.getString(pv.getValue());
        fireModelUpdate();
    }

    /** @return String representation for debugging */
    @Override
    public String toString()
    {
        return super.toString() + ", current value '" + current_value + "'" +
           " links to " + getName();
    }
}
