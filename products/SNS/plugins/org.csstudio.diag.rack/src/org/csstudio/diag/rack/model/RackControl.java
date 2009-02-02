package org.csstudio.diag.rack.model;

import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.diag.rack.listeners.RackUtilListener;
import org.csstudio.diag.rack.listeners.RackUtilListener.ChangeEvent;
import org.csstudio.diag.rack.model.RackList;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;


public class RackControl {
    private String slotPosInd = "F";
    private String rackId;
    private RackUtilThread rdb_reader = null;
    private int rackHeight;
    
    public String racks[] = new String[0];
    private RackList rackDvcList[] = new RackList[0];
	final public static String DATA_EXT_ID = "org.csstudio.diag.rack.rackutildata";//$NON-NLS-1$

	
    //final private ArrayList<RackList> rackDvcList = new ArrayList<RackList>();
    final private static CopyOnWriteArrayList<RackUtilListener> listenersRackUtil = new CopyOnWriteArrayList<RackUtilListener>();

   
    class RackUtilThread extends Thread
    {
 		private String schema;

 		/** ID of the extension point for providing RackDataAPI */
 
        public RackUtilThread(String schema)
        {
            super("RackUtilThread");
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
			if ("RACKLIST".equals(schema.toString())) {
	            try
            	{
	            	RackDataAPI newRackUtil = getRackUtil();
	            	racks = newRackUtil.getRackNames(rackId);
            	}catch (Exception e) {
	            	CentralLogger.getInstance().getLogger(this).error("Call to Rack List Exception", e);
            	}
            	fireRackUtilChanged(ChangeEvent.RACKLIST);
			}
			
			else if ("DVCLIST".equals(schema.toString())) {
    			try
            	{
    				RackDataAPI newRackUtil = getRackUtil();
	            	rackDvcList = newRackUtil.getRackListing(rackId, slotPosInd);
	            	rackId =  newRackUtil.getRackName();
            	
                }catch (Exception e) {
    	          	CentralLogger.getInstance().getLogger(this).error("Call to Rack List Exception", e);
                }	
                fireRackUtilChanged(ChangeEvent.DVCLIST);
			}
    }
 }

    /** Create background thread that fetches data from RDB */
    private void refreshRackUtil(final String schema)
    {
        rdb_reader = new RackUtilThread(schema);
        rdb_reader.start();
    }
   
    
    /** looks to get the default start device filter.
     * SNS starts out filtering the list to ":IOC"
     * This value is returned with the Reset All button too
     * 
     * @return the default start filter.
     */
    public int getRackHeight()
    {
    	try {
    		RackDataAPI newRackUtil = getRackUtil();
    		rackHeight = newRackUtil.getRackHeight();

    	}catch (Exception e) {
    		CentralLogger.getInstance().getLogger(this).error("Call to Rack Height Exception", e);
    	}
   
    	return rackHeight;
    	
    }


    
    
    
    
    
    
    
    
    
    public void addRacksListener(final RackUtilListener new_listener)
    {
    	listenersRackUtil.add(new_listener);
    }
 
    public void removeRacksListener(final RackUtilListener listener)
    {
    	listenersRackUtil.remove(listener);
    }
  
    
    
    
    
    
    public void setRackFilter(String rackId)
    {
        this.rackId = rackId;
        refreshRackUtil("RACKLIST");
 
    }
    
    public void setSelectedRack(String rackDvcId)
        {
            this.rackId = rackDvcId;
            refreshRackUtil("DVCLIST");
        }

    public void setSlotPosIndFilter(String slotPosInd)
    {
        this.slotPosInd = slotPosInd;
        if (this.slotPosInd == "")
            this.slotPosInd = "F";
        refreshRackUtil("DVCLIST");
    }

   
    public String getRack(int i)
    {
        synchronized (racks)
        {
             return racks[i];
        }
    }

    public int getRacksCount()
    {
        synchronized (racks)
        {
            return racks.length;
        }
    }
   
    public RackList getRackListDVC(int i)
    {
        synchronized (rackDvcList)
        {
            return rackDvcList[i];
        }
    }

    public String getRackDvcId()
    {
    	synchronized (rackId)
        {
    	  return rackId;
        }
    }

    public String getSlotPosInd()
    {
    	synchronized (slotPosInd)
        {
            return slotPosInd;
        }
    }
    
    public int getRackDvcListCount()
    {
        synchronized (rackDvcList)
        {
            return rackDvcList.length;
        }
    }

    public RackList[] getRackDvcList()
    {
        return rackDvcList;
    }
    /** 
     * @param what  enumerated ChangeEvent (either RACKLIST, DVCLIST, or PARENT)
     */
    private void fireRackUtilChanged(final ChangeEvent what)
    {
        for (RackUtilListener listener : listenersRackUtil)
        {
            try
            {
                listener.rackUtilChanged(what);
            }
            catch (Throwable ex)
            {
            	CentralLogger.getInstance().getLogger(this).error("Exception", ex);
            }
        }
    }

    /** Registry lookup to checking that expected implementation is found.
     *  
     * @return RackDataAPI interface
     * @throws Exception
     */
    protected RackDataAPI getRackUtil()throws Exception
	{
		final IConfigurationElement[] configs =	Platform.getExtensionRegistry()
			.getConfigurationElementsFor(DATA_EXT_ID);
		if (configs.length != 1)
			throw new Exception("Expected 1 implementation of " + DATA_EXT_ID
					+ ", got " + configs.length);
		
		final RackDataAPI data = (RackDataAPI)
			configs[0].createExecutableExtension("class");
		return data;
	}

    
    
}
