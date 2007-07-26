package org.csstudio.display.pvtable.model;

import java.io.FileInputStream;
import java.io.InputStream;

import junit.framework.Assert;
import junit.framework.TestCase;

/** Test of the PVModel
 * 
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVListModelTest extends TestCase implements
    PVListModelListener
{
    private int runstate = 0;
    private int updates = 0;
    
    /** @see org.csstudio.display.pvtable.model.PVListModelListener#runstateChanged(boolean) */
    public void runstateChanged(boolean isRunning)
    {
        ++runstate;
    }

    /** @see org.csstudio.display.pvtable.model.PVListModelListener#entryAdded(org.csstudio.display.pvtable.model.PVListEntry) */
    public void entryAdded(PVListEntry entry)
    { /* NOP */ }

    /** @see org.csstudio.display.pvtable.model.PVListModelListener#entryRemoved(org.csstudio.display.pvtable.model.PVListEntry) */
    public void entryRemoved(PVListEntry entry)
    { /* NOP */ }

    /** @see org.csstudio.display.pvtable.model.PVListModelListener#entriesChanged() */
    public void entriesChanged()
    { /* NOP */ }

    /** no longer used
    public void valueUpdate(PVListEntry entry)
    {
        System.out.println("newEntryValue runs in '" 
                + Thread.currentThread().getName() + "'");
        System.out.println("PVListEntry changed: " + entry.getPV().getName());
        ++updates;
    }
     */
    
    /** @see org.csstudio.display.pvtable.model.PVListListener#valuesUpdated() */
    public void valuesUpdated()
    {
        System.out.println("valuesUpdated()"); 
        ++updates;
    }

    public void testModel() throws Exception
    {
        System.out.println("Test runs in thread '"
                + Thread.currentThread().getName() + "'");
        PVListModel pvlist = new PVListModel();
        
        InputStream input = new FileInputStream("lib/excas.xml");
        pvlist.load(input);
        input.close();
        
        pvlist.addModelListener(this);
        pvlist.start();
        Assert.assertEquals(1, runstate);
        Thread.sleep(5000);
        pvlist.stop();
        Assert.assertEquals(2, runstate);
        pvlist.removeModelListener(this);
        
        Assert.assertTrue(updates > 0);
        pvlist.dispose();
    }
}
