/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.model;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.csstudio.diag.pvfields.Activator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/** Data model for the PV Fields.
 *  <p>
 *  Locates one (and only one) implementation of the PVFieldsAPI
 *  in the Eclipse extension point registry and then uses
 *  that to perform queries for PV Fields and their values
 *  in a background thread.
 *
 *  @author Dave Purcell
 */
@SuppressWarnings("nls")
public class PVFieldsModel
{
    /** PVUtilDataAPI that provides actual data, obtained via extension point */
    private final PVFieldsAPI api;

    /** Most recent PV info.
     *  Updated in background thread, synchronize on 'this' for access.
     */
    private PVInfo pvs [] = null;

    /** boolean to dictate column population within Label Provider.
     *  1PV = One PV many Fields
     *  NPVs = many PVs on field
     */
    private boolean fullList  = true;

    /** Listeners */
    final private static CopyOnWriteArrayList<PVFieldsListener> listeners
        = new CopyOnWriteArrayList<PVFieldsListener>();

    public PVFieldsModel() throws Exception
    {
        disconnectCurrentFields();
        final IConfigurationElement[] configs = Platform.getExtensionRegistry()
            .getConfigurationElementsFor(PVFieldsAPI.DATA_EXT_ID);
        if (configs.length != 1)
            throw new Exception("Expected 1 implementation of " +
            		PVFieldsAPI.DATA_EXT_ID +
                    ", got " + configs.length);
        api = (PVFieldsAPI) configs[0].createExecutableExtension("class");
    }

    /** Add model listener */
	public void addListener(final PVFieldsListener new_listener)
    {
        listeners.add(new_listener);
    }

    /** Remove model listener */
    public void removeListener(final PVFieldsListener listener)
    {
        listeners.remove(listener);
    }

    /** Set the PV Filter, a Specific Field, and trigger a Fields table refresh.
	 *  @param pv portion of PV to be applied as filter or <code>null</code> to disconnect model
	 *  @param field specific field to look up or null
	 */
    public void setPV(final String pv, final String field)
    {
        disconnectCurrentFields();
        if (pv.contains("%")) fullList = false;
        else fullList = true;
        startFieldsLookup(pv, field);
    }

    /** Disconnect (stop) all fields, remove fields */
    public void disconnectCurrentFields()
    {
        synchronized (this)
        {
        	if (pvs != null) {
            	for (PVInfo pv: pvs) pv.stop();
            	pvs = new PVInfo []
            	{
            	  new PVInfo("", "", "", "", "","","","")
            	};
        	}
        }
    }

    /** Start background thread for PV based on a filter and a specific field
     *  @param pvs String pv
     *  @param field String field
     */
    private void startFieldsLookup(final String pv_name, final String field)
    {
    	if (pv_name == null)return;

    	// adds the "Waiting" text indicating it's working.
        synchronized (this)
        {
        	pvs = new PVInfo []
        	{
        			new PVInfo("Waiting....", "Waiting....", "Waiting....", "Waiting....", "Waiting....","","","")
        	};
        }
        fireFieldChanged(null);
        // Launch background thread for real RDB call
        final Thread lookup = new Thread("Field Lookup")
        {
            @Override
            public void run()
            {
                {
                    try
                    {
                        // RDB query...
                    	final PVInfo[] result = api.getPVInfo(pv_name,field);
                        // Lock model to update its fields
                        synchronized (PVFieldsModel.this)
                        {
                            pvs = result;
                        }


                        // Start updates
                        for (PVInfo pv: pvs){
                        	pv.setModel(PVFieldsModel.this);
                        	pv.start();}
                    }
                    catch (Exception e)
                    {
                        Activator.getLogger().log(Level.SEVERE, "PV Lookup Exception ", e);
                    }
                    fireFieldChanged(null);
                }
            }
        };
        lookup.setDaemon(true);
        lookup.start();
    }


    /** Notify listeners
     *  @param field Field that changed or <code>null</code> for overall change
     */
    void fireFieldChanged(final PVInfo pvs)
    {
        for (PVFieldsListener listener : listeners)
        {
            try
            {
                listener.fieldChanged(pvs);

            }
            catch (Throwable ex)
            {
                Activator.getLogger().log(Level.SEVERE, "Field change exception", ex);
            }
        }
    }

    public synchronized PVInfo getPVInfoRow(int i)
    {
        return pvs[i];
    }

    public synchronized int getPVInfoListCount()
    {
        return pvs.length;
    }

    public synchronized PVInfo[] getPVInfoAll()
    {
        return pvs;
    }

    public synchronized boolean alterColumnData()
    {
    	return fullList;
    }

    /** @return String representation for debugging */
    @Override
    public synchronized String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("Model:\n");
        for (int i=0; i<pvs.length; ++i)
        {
            final PVInfo pv = pvs[i];
            buf.append(pv.getFirstColumn() + " = " + pv.getOrigValue() + "\n");
        }
        return buf.toString();
    }
}
