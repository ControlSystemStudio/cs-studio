package org.csstudio.diag.rack.model;

import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.diag.rack.model.RackList;
import org.csstudio.diag.rack.model.RackModelListener.ChangeEvent;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/** Model of Rack Data
 *  <p>
 *  Uses RackDataAPI to obtain info in background threads
 *  
 *  @author Dave Purcell
 *  @author Kay Kasemir
 */
public class RackModel
{
    /** RackDataAPI implementation obtained from registry */
    final private RackDataAPI api;
    
    /** Rack information. Synchronize on 'this' for access */
    private String slotPosInd = "F";
    private String rackId;
    private int rackHeight;
    
    public String racks[] = new String[0];
    private RackList rackDvcList[] = new RackList[0];
    
    /** RackUtilListeners */
    final private static CopyOnWriteArrayList<RackModelListener> listeners =
            new CopyOnWriteArrayList<RackModelListener>();
   
    /** Initialize via registry lookup for implementation of RackDataAPI.
     *  @throws Exception when not found
     */
    public RackModel() throws Exception
    {
    	final IConfigurationElement[] configs =	Platform.getExtensionRegistry()
    		.getConfigurationElementsFor(RackDataAPI.DATA_EXT_ID);
    	if (configs.length != 1)
    		throw new Exception("Expected 1 implementation of " + RackDataAPI.DATA_EXT_ID
    				+ ", got " + configs.length);
    	
    	api = (RackDataAPI)	configs[0].createExecutableExtension("class");
    }

    public void addListener(final RackModelListener new_listener)
    {
    	listeners.add(new_listener);
    }
 
    public void removeListener(final RackModelListener listener)
    {
    	listeners.remove(listener);
    }
    
    public int getRackHeight()
    {
        return api.getRackHeight();
    }

    public void setRackFilter(String rackId)
    {
        synchronized (this)
        {
            this.rackId = rackId;
        }
        updateRackList();
    }
    
    public void setSelectedRack(String rackDvcId)
    {
        synchronized (this)
        {
            this.rackId = rackDvcId;
        }
        updateDeviceList();
    }

    public void setSlotPosIndFilter(String slotPosInd)
    {
        synchronized (this)
        {
            this.slotPosInd = slotPosInd;
            if (this.slotPosInd == "")
                this.slotPosInd = "F";
        }
        updateDeviceList();
    }

   
    public synchronized String getRack(int i)
    {
         return racks[i];
    }

    public synchronized int getRacksCount()
    {
        return racks.length;
    }
   
    public synchronized RackList getRackListDVC(int i)
    {
        return rackDvcList[i];
    }

    public synchronized String getRackDvcId()
    {
        return rackId;
    }

    public synchronized String getSlotPosInd()
    {
        return slotPosInd;
    }
    
    public synchronized int getRackDvcListCount()
    {
        return rackDvcList.length;
    }

    private void updateRackList()
    {
        final Thread lookup = new Thread("RackList")
        {
            @Override
            public void run()
            {
                try
                {
                    final String[] result = api.getRackNames(rackId);
                    synchronized (this)
                    {
                        racks = result;
                    }
                }catch (Exception e) {
                    CentralLogger.getInstance().getLogger(this).error("Call to Rack List Exception", e);
                }
                fireRackUtilChanged(ChangeEvent.RACKLIST);
            }
        };
        lookup.setDaemon(true);
        lookup.start();
    }
    
    private void updateDeviceList()
    {
        final Thread lookup = new Thread("DeviceList")
        {
            @Override
            public void run()
            {
                try
                {
                    final RackList[] result = api.getRackListing(rackId, slotPosInd);
                    synchronized (this)
                    {
                        rackDvcList = result;
                        rackId = api.getRackName();
                    }
                }catch (Exception e) {
                    CentralLogger.getInstance().getLogger(this).error("Call to Rack List Exception", e);
                }   
                fireRackUtilChanged(ChangeEvent.DVCLIST);
            }
        };
        lookup.setDaemon(true);
        lookup.start();
    }
    
    /** 
     * @param what  enumerated ChangeEvent (either RACKLIST, DVCLIST, or PARENT)
     */
    private void fireRackUtilChanged(final ChangeEvent what)
    {
        for (RackModelListener listener : listeners)
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
}
