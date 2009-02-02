package org.csstudio.diag.pvutil.model;

import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.diag.pvutil.gui.GUI.ItemIndex;
import org.csstudio.diag.pvutil.model.PVUtilListener.ChangeEvent;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

@SuppressWarnings("nls")
public class PVUtilControl {

	final public static String WILDCARD = "%";

	/** ID of the extension point for providing PVUtilDataAPI */
    final public static String DATA_EXT_ID = "org.csstudio.diag.pvutil.pvutildata";//$NON-NLS-1$

    private String startDeviceID, deviceID;
	private String newdeviceID = "";
    private String recFilter = "";
    final private static CopyOnWriteArrayList<PVUtilListener> listenersPVUtil = new CopyOnWriteArrayList<PVUtilListener>();
    private PV pvs[] = new PV[0];
    private FEC fecs[] = new FEC[0];
    private PVUtilThread rdb_reader = null;
    //private PVThread rdb_reader;
    
    class PVUtilThread extends Thread
    {
 		private String schema;

        public PVUtilThread(String schema)
        {
            super("PVUtilThread");
            this.schema = schema;
        }
        
        /* (non-Javadoc)
         * Take the appropriate schema (FEC or PV) and fire the 
         * appropriate change event.
         * @see java.lang.Thread#run()
         */
        @Override
        public void run()
        {
			if ("FEC".equals(schema.toString()))
            {
	            try
	            {
	            	PVUtilDataAPI newPVUtil = getPVUtil();
	            	fecs = newPVUtil.getFECs(deviceID);
	            }
	            catch (Exception e) {
	            	CentralLogger.getInstance().getLogger(this).error("Call To FEC Exception", e);
				}
	            firePVUtilChanged(ChangeEvent.FEC_CHANGE);
	        }
			else if ("PV".equals(schema.toString())) {
		           // adds the "Waiting" text indicating it's working.
		            synchronized (pvs)
		            {
		                pvs = new PV[]
		                {
		             		new PV("Waiting....", "") 
		                };
		            }
	            firePVUtilChanged(ChangeEvent.PV_CHANGE);

	            // now the real RDB calls happen and real data returned.
		            try
		            {
		        		PVUtilDataAPI newPVUtil = getPVUtil();
		        		pvs = newPVUtil.getPVs(newdeviceID,recFilter);
		        	
		            }
		            catch (Exception e) {
		            	CentralLogger.getInstance().getLogger(this).error("Call to PV Data Exception", e);
					}
		            firePVUtilChanged(ChangeEvent.PV_CHANGE);
	        }
        }
    }
    
	/** Used to set the FEC filter and then trigger a FEC list
	 * refresh.
	 * @param deviceID 
	 */
	public void setDeviceIDFilter(String deviceID)
    {
        this.deviceID = deviceID;
        refreshPVUtil("FEC");
    }
	
	/** Used to set the PV filter to the specific selected FEC
	 *  and then trigger a PV table list refresh.
     * @param deviceID
     */
    public void setFECFilter(String deviceID)
    {
        newdeviceID = deviceID;
        refreshPVUtil("PV");
    }

    /* (non-Javadoc)
	 * @see org.csstudio.pvutil.model.IPVModel#setPVFilter(java.lang.String)
	 */
    public void setPVFilter(String recFilter)
    {
        this.recFilter = recFilter;
        refreshPVUtil("PV");
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
            newdeviceID = "";
            break;
        case PV:
            recFilter = "";
            break;
        default:
            newdeviceID = "";
            recFilter = "";
        }
    }

    public int getPVCount()
    {
        synchronized (pvs)
        {
            return pvs.length;
        }
    }
    
    public PV getPV(int row)
	{
		synchronized (this)
		{
			return pvs[row];			
		}
	}

	public int getFECCount()
    {
        synchronized (fecs)
        {
            return fecs.length;
        }
    }

    /* (non-Javadoc)
	 * @see org.csstudio.pvutil.model.IFECModel#getFEC(int)
	 */
    public FEC getFEC(int i)
    {
        synchronized (fecs)
        {
            return fecs[i];
        }
    }

    /** looks to get the default start device filter.
     * SNS starts out filtering the list to ":IOC"
     * This value is returned with the Reset All button too
     * 
     * @return the default start filter.
     */
    public String getStartDeviceID()
    {
    	try
    	{
    	PVUtilDataAPI newPVUtil = getPVUtil();
    	startDeviceID = newPVUtil.getStartDeviceID();
    	}
    	catch (Exception e) {
    		CentralLogger.getInstance().getLogger(this).error("Get Start Device ID Exception", e);
    	}
		return startDeviceID;	
    }
    

    
    /* (non-Javadoc)
	 * @see org.csstudio.pvutil.model.IPVModel#addListener(org.csstudio.pvutil.listeners.ModelPVListener)
	 */
    public void addListener(final PVUtilListener new_listener)
    {
        listenersPVUtil.add(new_listener);
    }

    /* (non-Javadoc)
	 * @see org.csstudio.pvutil.model.IPVModel#removeListener(org.csstudio.pvutil.listeners.ModelPVListener)
	 */
    public void removeListener(final PVUtilListener listener)
    {
        listenersPVUtil.remove(listener);
    }

    /** Create background thread that fetches data from RDB */
    private void refreshPVUtil(final String schema)
    {
        rdb_reader = new PVUtilThread(schema);
        rdb_reader.start();
    }
    
    /** 
     * @param what  enumerated ChangeEvent (either PV_EVENT or FEC_EVENT)
     */
    private void firePVUtilChanged(final ChangeEvent what)
    {
        for (PVUtilListener listener : listenersPVUtil)
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

    /** Registry lookup to checking that expected implementation is found.
     *  
     * @return PVUtilDataAPI interface
     * @throws Exception
     */
    protected PVUtilDataAPI getPVUtil()throws Exception
	{
		final IConfigurationElement[] configs =	Platform.getExtensionRegistry()
			.getConfigurationElementsFor(DATA_EXT_ID);
		if (configs.length != 1)
			throw new Exception("Expected 1 implementation of " + DATA_EXT_ID
					+ ", got " + configs.length);
		
		final PVUtilDataAPI data = (PVUtilDataAPI)
			configs[0].createExecutableExtension("class");
		return data;
	}

}
