package org.csstudio.diag.pvfields;

import java.util.Map;

public class PVModel
{
    final private PVModelListener listener;
    private String name;
    private Map<String, String> properties;
    private PVField[] fields;
    
    public PVModel(PVModelListener listener)
    {
        this.listener = listener;
    }

    public void setPVName(final String name) throws Exception
    {
        synchronized (this)
        {
            this.name = name;
        }
        
        // Locate all DataProviders
        // Execute them
        // Merge their results
        // Notify listeners
     }

    public synchronized String getPVName()
    {
        return name;
    }

    public synchronized Map<String, String> getProperties()
    {
        return properties;
    }
    
    public void close()
    {
        // TODO Auto-generated method stub
    }
}
