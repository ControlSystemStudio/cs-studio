package org.csstudio.display.pvtable.model;

/** NOP implementation of PVListModelListener */
public class AbstractPVListModelListener implements PVListModelListener
{
    public void runstateChanged(boolean isRunning)
    {}

    public void entryAdded(PVListEntry entry)
    {}

    public void entryRemoved(PVListEntry entry)
    {}

    public void entriesChanged()
    {}
    
    public void valuesUpdated()
    {}
}
