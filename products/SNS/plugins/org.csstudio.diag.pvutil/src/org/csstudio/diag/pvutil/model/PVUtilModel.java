package org.csstudio.diag.pvutil.model;

import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.diag.pvutil.gui.GUI.ItemIndex;
import org.csstudio.diag.pvutil.model.PVUtilListener.ChangeEvent;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/** Data model for the PV Util.
 *  <p>
 *  Locates one (and only one) implementation of the PVUtilDataAPI
 *  in the Eclipse extension point registry and then uses
 *  that to perform queries for PVs and their FECs
 *  in a background thread.
 *  
 *  @author Dave Purcell
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVUtilModel
{
	final public static String WILDCARD = "%";

	/** PVUtilDataAPI that provides actual data, obtained via extension point */
    private final PVUtilDataAPI api;
    
    /** Current FEC filter */
    private String fec_filter = "";

    /** Current PV filter */
    private String pv_filter = "";

    /** Most recent list of FECs. Updated in background thread,
     *  synchronize on 'this' for access.
     */
    private FEC fecs[] = new FEC[0];

    /** Most recent list of PVss. Updated in background thread,
     *  synchronize on 'this' for access.
     */
    private PV pvs[] = new PV[0];

    /** Listeners */
    final private static CopyOnWriteArrayList<PVUtilListener> listeners
        = new CopyOnWriteArrayList<PVUtilListener>();

    /** Initialize via registry lookup for implementation of PVUtilDataAPI.
     *  @throws Exception when no implementation found
     */
    public PVUtilModel() throws Exception
    {
        final IConfigurationElement[] configs = Platform.getExtensionRegistry()
            .getConfigurationElementsFor(PVUtilDataAPI.DATA_EXT_ID);
        if (configs.length != 1)
            throw new Exception("Expected 1 implementation of " +
                    PVUtilDataAPI.DATA_EXT_ID +
                    ", got " + configs.length);
        api = (PVUtilDataAPI) configs[0].createExecutableExtension("class");
    }
    
    /** Add model listener */
	public void addListener(final PVUtilListener new_listener)
    {
        listeners.add(new_listener);
    }

    /** Remove model listener */
    public void removeListener(final PVUtilListener listener)
    {
        listeners.remove(listener);
    }

    /** Set the FEC filter and trigger a FEC list refresh.
	 *  @param fec_filter New FEC name filter
	 */
    public void setFECFilter(final String fec_filter)
    {
        this.fec_filter = fec_filter;
        startFECLookup(fec_filter);
    }

    /** Update list of PVs for given FEC
     *  @param fec_name
     */
    public void setFECName(final String fec_name)
    {
        startPVLookup(fec_name, pv_filter);
    }

    /** Set the FEC filter and trigger a FEC list refresh.
     *  @param pv_filter New FEC name filter
     */
    public void setPVFilter(final String pv_filter)
    {
        this.pv_filter = pv_filter;
        startPVLookup(fec_filter, pv_filter);
    }
    
	/** When one of the clear buttons is pressed then that enum value
	 * is passed through here to clear the contents specific to that button.
	 * 
	 * @param what
	 */
	public void setObjectClear(final ItemIndex what)
    {
        switch (what)
        {
        case FEC:
            fec_filter = "";
            break;
        case PV:
            pv_filter = "";
            break;
        default:
            fec_filter = "";
            pv_filter = "";
        }
    }

    public synchronized int getPVCount()
    {
        return pvs.length;
    }
    
    public synchronized PV getPV(final int row)
	{
		return pvs[row];			
	}

	public synchronized int getFECCount()
    {
        return fecs.length;
    }

    public synchronized FEC getFEC(final int i)
    {
        return fecs[i];
    }

    /** looks to get the default start device filter.
     *  SNS starts out filtering the list to ":IOC"
     *  This value is returned with the Reset All button too
     * 
     * @return the default start filter.
     */
    public String getStartDeviceID()
    {
    	try
    	{
    	    return api.getStartDeviceID();
    	}
    	catch (Exception e)
    	{
    		CentralLogger.getInstance().getLogger(this).
    		    error("Get Start Device ID Exception", e);
    	}
		return "";	
    }
    
    /** Start background thread for FEC Lookup
     *  @param deviceID Device pattern
     */
    private void startFECLookup(final String deviceID)
    {
        final Thread lookup = new Thread("FEC Lookup")
        {
            @Override
            public void run()
            {
                try
                {
                    // Run query
                    final FEC[] result = api.getFECs(deviceID);
                    // Lock model to update data
                    synchronized (this)
                    {
                        fecs = result;
                    }
                }
                catch (Exception e)
                {
                    CentralLogger.getInstance().getLogger(this)
                        .error("FEC Lookup Exception " + e.getMessage(), e);
                }
                firePVUtilChanged(ChangeEvent.FEC_CHANGE);
            }
        };
        lookup.setDaemon(true);
        lookup.start();
    }
    
    /** Start background thread for PV lookup
     *  @param deviceID Device pattern
     *  @param pvFilter PV pattern
     */
    private void startPVLookup(final String deviceID, final String pvFilter)
    {
        // adds the "Waiting" text indicating it's working.
        synchronized (this)
        {
            pvs = new PV[]
            {
                new PV("Waiting....", "") 
            };
        }
        firePVUtilChanged(ChangeEvent.PV_CHANGE);
        // Launch background thread for real RDB call
        final Thread lookup = new Thread("PV Lookup")
        {
            @Override
            public void run()
            {
                {
                    try
                    {
                        // RDB query...
                        final PV[] result = api.getPVs(deviceID, pvFilter);
                        // Lock model to update
                        synchronized (this)
                        {
                            pvs = result;
                        }
                    }
                    catch (Exception e)
                    {
                        CentralLogger.getInstance().getLogger(this)
                            .error("PV Lookup Exception " + e.getMessage(), e);
                    }
                    firePVUtilChanged(ChangeEvent.PV_CHANGE);
                }
            }
        };
        lookup.setDaemon(true);
        lookup.start();
    }
    
    /** 
     * @param what  enumerated ChangeEvent (either PV_EVENT or FEC_EVENT)
     */
    private void firePVUtilChanged(final ChangeEvent what)
    {
        for (PVUtilListener listener : listeners)
        {
            try
            {
                listener.pvUtilChanged(what);
            }
            catch (Throwable ex)
            {
            	CentralLogger.getInstance().getLogger(this).error("Exception", ex);
            }
        }
    }
}
