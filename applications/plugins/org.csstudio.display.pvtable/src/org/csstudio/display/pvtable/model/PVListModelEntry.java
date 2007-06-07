package org.csstudio.display.pvtable.model;

import org.csstudio.display.pvtable.Plugin;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.core.runtime.PlatformObject;

/** Implementation of the PVListEntry as used by the PVListModel.
 *  @see PVListModel
 *  
 *  @author Kay Kasemir
 */
public class PVListModelEntry extends PlatformObject implements PVListEntry 
{
    private boolean selected;
    private PV pv, readback_pv;
    private final SavedValue saved_value, saved_readback_value;
    private int new_values;
    private PVListener pv_listener;

    /** Create new entry from pieces. */
    public PVListModelEntry(boolean selected, 
            String pv_name, SavedValue saved_value,
            String readback_name, SavedValue readback_value)
    {
        this.selected = selected;
        this.pv = createPV(pv_name);
        this.saved_value = saved_value;
        
        if (readback_name != null  &&  readback_name.length() > 0)
            this.readback_pv = createPV(readback_name);
        else
            this.readback_pv = null;
        this.saved_readback_value = readback_value;
        new_values = 0;

        pv_listener = new PVListener()
        {
            public void pvValueUpdate(PV pv)
            {   addNewValue(); }
    
            public void pvDisconnected(PV pv)
            {   addNewValue(); // handled the same way: mark for redraw
            }
        };
    }
    
    /** @return PV for the given name */
    @SuppressWarnings("nls")
    private PV createPV(final String name)
    {
        try
        {
            return PVFactory.createPV(name);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Plugin.logException("Cannot create PV '" + name + "'", ex);
        }
        return null;
    }
    
    /** @see org.csstudio.platform.model.IProcessVariable */
    public String getName()
    {   return pv.getName(); }
    
    /** @see org.csstudio.platform.model.IProcessVariable */
    public final String getTypeId()
    {   return IProcessVariable.TYPE_ID;  }
    
    public boolean isDisposed()
    {   return pv == null; }
    
    public boolean isSelected()
    {   return selected; }
    
    public void setSelected(boolean new_state)
    {   selected = new_state; }

    public PV getPV()
    {   return pv; }

    public SavedValue getSavedValue()
    {   return saved_value; }

    public PV getReadbackPV()
    {   return readback_pv; }

    public SavedValue getSavedReadbackValue()
    {   return saved_readback_value; }

    /** Mark entry as disposed. */
    public void dispose()
    {
        stop();
        pv = null;
        readback_pv = null;
    }
    
    public void start()
    {
        pv.addListener(pv_listener);
        try
        {
            pv.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (readback_pv != null)
        {
            readback_pv.addListener(pv_listener);
            try
            {
                readback_pv.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void stop()
    {
        if (readback_pv != null)
        {
            if (readback_pv.isRunning())
            {
                readback_pv.stop();
                readback_pv.removeListener(pv_listener);
            }
        }
        if (pv.isRunning())
        {
            pv.stop();
            pv.removeListener(pv_listener);
        }
    }

    /** Save the current values of the PVs. */
    public void takeSnapshot()
    {
        saved_value.readFromPV(pv);
        saved_readback_value.readFromPV(readback_pv);
    }
    
    /** Restore values from snapshot.
     *  @return Returns <code>false</code> if the entry is not selected. */
    public boolean restore()
    {
        if (!selected)
            return false;
        saved_value.restoreToPV(pv);
        saved_readback_value.restoreToPV(readback_pv);
        return true;
    }

    public void setReadbackPV(String readback_name)
    {
        boolean running = pv.isRunning();
        if (running)
            stop();
        if (readback_name != null  &&  readback_name.length() > 0)
            readback_pv = createPV(readback_name);
        else
            readback_pv = null;
        if (running)
        {
            try
            {
                start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /** Used by PVListModel to indicate that a PV of this entry has a new value. */
    private synchronized void addNewValue()
    {
        ++new_values;
    }
    
    /** Used by the PVListModel to check if this entry has a new value. */
    public synchronized int testAndResetNewValues()
    {
        int result = new_values;
        new_values = 0;
        return result;
    }
    
    @SuppressWarnings("nls")
    public String toXML()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("<pv>");

        buf.append(" <selected>");
        buf.append(isSelected() ? "true" : "false");
        buf.append("</selected>");
        
        buf.append(" <name>");
        buf.append(pv.getName());
        buf.append("</name>");
        if (saved_value != null)
        {
            buf.append(" <saved_value>");
            buf.append(saved_value);
            buf.append("</saved_value>");
        }
        if (readback_pv != null)
        {
            buf.append(" <readback>");
            buf.append(readback_pv.getName());
            buf.append("</readback>");
        }
        if (saved_readback_value != null)
        {
            buf.append(" <readback_value>");
            buf.append(saved_readback_value);
            buf.append("</readback_value>");
        }
        buf.append(" </pv>");
        return buf.toString();
    }
    
    @Override
    public String toString()
    {
        return toXML();
    }
}
