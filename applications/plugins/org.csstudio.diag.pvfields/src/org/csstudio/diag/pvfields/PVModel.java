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

        // TODO Check if a site-specific data provider was registered
        final DataProvider data_provider
            //= new DefaultDataProvider();
            = new SNSDataProvider();
        
        data_provider.run(name, new PVModelListener()
        {
            @Override
            public void updateProperties(Map<String, String> properties)
            {
                synchronized (PVModel.this)
                {
                    PVModel.this.properties = properties;
                }
                listener.updateProperties(properties);
            }

            @Override
            public void updateFields(PVField[] fields)
            {
                synchronized (PVModel.this)
                {
                    PVModel.this.fields = fields;
                }
                listener.updateFields(fields);
            }
        });
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
