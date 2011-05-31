/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A data source that can dispatch a request to multiple different
 * data sources.
 *
 * @author carcassi
 */
public class CompositeDataSource extends DataSource {

    // Stores all data sources by name
    private Map<String, DataSource> dataSources = new ConcurrentHashMap<String, DataSource>();

    private volatile String delimiter = "://";
    private volatile String defaultDataSource;

    /**
     * Returns the delimeter that divides the data source name from the
     * channel name. Default is "://" so that "epics://pv1" corresponds
     * to the "pv1" channel from the "epics" datasource.
     *
     * @return data source delimeter; can't be null
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Changes the data source delimiter.
     *
     * @param delimiter new data source delimiter; can't be null
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Adds/replaces the data source corresponding to the given name.
     *
     * @param name the name of the data source
     * @param dataSource the data source to add/replace
     */
    public void putDataSource(String name, DataSource dataSource) {
        dataSources.put(name, dataSource);
    }

    /**
     * Returns which data source is used if no data source is specified in the
     * channel name.
     *
     * @return the default data source, or null if it was never set
     */
    public String getDefaultDataSource() {
        return defaultDataSource;
    }

    /**
     * Sets the data source to be used if the channel does not specify
     * one explicitely. The data source must have already been added.
     *
     * @param defaultDataSource the default data source
     */
    public void setDefaultDataSource(String defaultDataSource) {
        if (!dataSources.containsKey(defaultDataSource))
            throw new IllegalArgumentException("The data source " + defaultDataSource + " was not previously added, and therefore cannot be set as default");

        this.defaultDataSource = defaultDataSource;
    }

    // Need to remember how the recipes where split, so that they can be
    // re-sent for disconnection
    private Map<DataRecipe, Map<String, DataRecipe>> splitRecipes =
            new ConcurrentHashMap<DataRecipe, Map<String, DataRecipe>>();

    @Override
    public void connect(DataRecipe recipe) {
        Map<String, DataRecipe> splitRecipe = new HashMap<String, DataRecipe>();

        // Iterate through the recipe to understand how to distribute
        // the calls
        for (Map.Entry<Collector, Map<String, ValueCache>> collEntry : recipe.getChannelsPerCollectors().entrySet()) {
            Map<String, Map<String, ValueCache>> routingCaches = new HashMap<String, Map<String, ValueCache>>();
            Collector collector = collEntry.getKey();
            for (Map.Entry<String, ValueCache> entry : collEntry.getValue().entrySet()) {
                String name = entry.getKey();
                String dataSource = defaultDataSource;

                int indexDelimiter = name.indexOf(delimiter);
                if (indexDelimiter != -1) {
                    dataSource = name.substring(0, indexDelimiter);
                    name = name.substring(indexDelimiter + delimiter.length());
                }

                if (dataSource == null)
                    throw new IllegalArgumentException("Channel " + name + " uses the default data source but one was never set.");

                // Add recipe for the target dataSource
                if (routingCaches.get(dataSource) == null)
                    routingCaches.put(dataSource, new HashMap<String, ValueCache>());
                routingCaches.get(dataSource).put(name, entry.getValue());
            }

            // Add to the recipes
            for (Map.Entry<String, Map<String, ValueCache>> entry : routingCaches.entrySet()) {
                if (splitRecipe.get(entry.getKey()) == null)
                    splitRecipe.put(entry.getKey(), new DataRecipe(recipe.getExceptionHandler()));
                splitRecipe.put(entry.getKey(), splitRecipe.get(entry.getKey()).includeCollector(collector, entry.getValue()));
            }

        }

        splitRecipes.put(recipe, splitRecipe);

        // Dispatch calls to all the data sources
        for (Map.Entry<String, DataRecipe> entry : splitRecipe.entrySet()) {
            try {
                DataSource dataSource = dataSources.get(entry.getKey());
                if (dataSource == null)
                    throw new IllegalArgumentException("DataSource '" + entry.getKey() + "://' was not configured.");
                dataSource.connect(entry.getValue());
            } catch (RuntimeException ex) {
                // If data source fail, still go and connect the others
                recipe.getExceptionHandler().handleException(ex);
            }
        }
    }

    @Override
    public void disconnect(DataRecipe recipe) {
        Map<String, DataRecipe> splitRecipe = splitRecipes.get(recipe);
        if (splitRecipe == null) {
            throw new IllegalStateException("Recipe was never opened or already closed");
        }

        // Dispatch calls to all the data sources
        for (Map.Entry<String, DataRecipe> entry : splitRecipe.entrySet()) {
            try {
                dataSources.get(entry.getKey()).disconnect(entry.getValue());
            } catch(RuntimeException ex) {
                // If a data source fails, still go and disconnect the others
                recipe.getExceptionHandler().handleException(ex);
            }
        }

        splitRecipes.remove(recipe);
    }



}
