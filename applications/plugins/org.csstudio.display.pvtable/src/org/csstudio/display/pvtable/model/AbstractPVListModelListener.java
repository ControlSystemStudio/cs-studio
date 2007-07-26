package org.csstudio.display.pvtable.model;

/** NOP implementation of PVListModelListener */
public class AbstractPVListModelListener implements PVListModelListener
{
    public void runstateChanged(boolean isRunning)
    { /* NOP */ }

    public void entryAdded(PVListEntry entry)
    { /* NOP */ }

    public void entryRemoved(PVListEntry entry)
    { /* NOP */ }

    public void entriesChanged()
    { /* NOP */ }
    
    public void valuesUpdated()
    { /* NOP */ }
}
