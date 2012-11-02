package org.csstudio.diag.pvfields.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.csstudio.diag.pvfields.Activator;
import org.csstudio.diag.pvfields.DataProvider;
import org.csstudio.diag.pvfields.PVField;
import org.csstudio.diag.pvfields.PVInfo;
import org.csstudio.diag.pvfields.Preferences;

public class PVModel
{
	final private String name;
    final private PVModelListener listener;
    private Map<String, String> properties;
    private List<PVField> fields;
    
    public PVModel(final String name, final PVModelListener listener)
    {
    	this.name = name;
        this.listener = listener;

        // Perform the lookup in a thread
        final Runnable lookup = new Runnable()
        {
			@Override
			public void run() 
			{
				performLookup(name);
			}
        };
        new Thread(lookup, "Lookup").start();
    }
    
    private void performLookup(final String name)
    {
        // TODO Locate all DataProviders
    	final DataProvider providers[] = new DataProvider[] { new EPICSDataProvider(), new SNSDataProvider() };

        // Execute them in parallel
        final ExecutorService executors = Executors.newFixedThreadPool(2);
        final List<Future<PVInfo>> results = new ArrayList<Future<PVInfo>>();
        for (DataProvider provider : providers)
        {
        	final DataProvider current_provider = provider;
        	final Callable<PVInfo> callable = new Callable<PVInfo>()
			{
				@Override
				public PVInfo call() throws Exception
				{
					return current_provider.lookup(name);
				}
			};
			results.add(executors.submit(callable));
        }
        
        // Merge their results
        // Will effectively wait for the first data provider to return,
        // then the next one and so on.
        final Map<String, String> properties = new HashMap<String, String>();
        final List<PVField> fields = new ArrayList<PVField>();
        for (Future<PVInfo> result : results)
        {
        	try
        	{
	        	final PVInfo info = result.get(Preferences.getTimeout(), TimeUnit.MILLISECONDS);
	        	properties.putAll(info.getProperties());
	        	for (PVField field : info.getFields())
	        		addOrReplaceField(fields, field);
        	}
        	catch (Exception ex)
        	{
        		Activator.getLogger().log(Level.WARNING, "DataProvider error", ex);
        	}
        }
        executors.shutdown();

        // Notify listeners
        synchronized (this)
        {
        	this.properties = properties;
        	this.fields = fields;
		}
        listener.updateProperties(properties);
        listener.updateFields(fields);
        // Start individual field updates
        for (PVField field : fields)
        	field.start(listener);
     }

    /** Add field only if it is unique, otherwise replace it
     *  @param fields List of {@link PVField}
     *  @param field {@link PVField} to add
     */
    private static void addOrReplaceField(final List<PVField> fields, final PVField field)
	{
		for (int i=0; i<fields.size(); ++i)
		{
			if (fields.get(i).getName().equals(field.getName()))
			{
				fields.set(i, field);
				return;
			}
		}
		// Else: New field, add to list
		fields.add(field);
	}

	public synchronized String getPVName()
    {
        return name;
    }

    public synchronized Map<String, String> getProperties()
    {
        return properties;
    }

    public synchronized List<PVField> getFields()
    {
        return fields;
    }

	public void stop()
	{
		for (PVField field : fields)
			field.stop();
	}
}
