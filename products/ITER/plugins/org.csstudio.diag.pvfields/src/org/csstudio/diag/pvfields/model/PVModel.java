/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;

/** PVModel that performs lookup,
 *  then starts all {@link PVField}s to they'll update
 *  when the PVs change.
 *   
 *  @author Kay Kasemir
 */
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
    
    /** Locate all DataProviders in registry
     *  @return {@link DataProvider}s
     */
    private DataProvider[] getDataProviders()
    {
    	final List<DataProvider> providers = new ArrayList();
    	// Always use at least the EPICSDataProvider
    	providers.add(new EPICSDataProvider());
    	
    	final IExtensionRegistry registry =  RegistryFactory.getRegistry();
    	if (registry != null)
    	{
    		final IConfigurationElement[] configs = registry.getConfigurationElementsFor(DataProvider.ID);
    		for (IConfigurationElement config : configs)
    		{
    			try
    			{
	    			final String name = config.getAttribute("name");
	    			final DataProvider provider = (DataProvider) config.createExecutableExtension("class");
	    			Activator.getLogger().log(Level.INFO,
	    				"Found DataProvider {0}, implemented by {1} from {2}",
	    				new Object[] { name, provider.getClass().getName(), config.getContributor().getName()  });
	    			providers.add(provider);
    			}
    			catch (CoreException ex)
    			{
	    			Activator.getLogger().log(Level.WARNING,
	    				"Error creating DataProvider {0} provided by {1}",
	    				new Object[] { config.getAttribute("class"), config.getContributor().getName() });
    			}
    		}
    	}
    	return providers.toArray(new DataProvider[providers.size()]);
    }
    
    /** Perform the lookup, invoke listener when done
     *  @param name Channel/PV name
     */
    private void performLookup(final String name)
    {
    	final DataProvider providers[] = getDataProviders();

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

    /** @return PV name of last lookup */
	public synchronized String getPVName()
    {
        return name;
    }

	/** @return Properties obtained in last lookup */
    public synchronized Map<String, String> getProperties()
    {
        return properties;
    }

	/** @return {@link PVField}s obtained in last lookup */
    public synchronized List<PVField> getFields()
    {
        return fields;
    }

    /** Must be called when model is no longer used */
	public void stop()
	{
		final List<PVField> old_fields;
		synchronized (this)
		{
			old_fields = fields;
			fields = null;
		}
		if (old_fields != null)
			for (PVField field : old_fields)
				field.stop();
	}
}
