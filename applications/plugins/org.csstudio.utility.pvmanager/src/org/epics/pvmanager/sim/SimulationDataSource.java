/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.sim;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.epics.pvmanager.Collector;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.DataRecipe;
import org.epics.pvmanager.TimeStamp;
import org.epics.pvmanager.ValueCache;
import org.epics.pvmanager.data.VDouble;

/**
 * Data source to produce simulated signals that can be using during development
 * and testing.
 *
 * @author carcassi
 */
public final class SimulationDataSource extends DataSource {

    /**
     * Data source instance.
     *
     * @return the data source instance
     */
    public static DataSource simulatedData() {
        return SimulationDataSource.instance;
    }

    static abstract class ValueProcessor<T, E>
    extends DataSource.ValueProcessor<T, E> {

        public ValueProcessor(Collector collector, ValueCache<E> cache) {
            super(collector, cache);
        }

    }

    private static final Logger log = Logger.getLogger(SimulationDataSource.class.getName());
    static final SimulationDataSource instance = new SimulationDataSource();

    /**
     * Timer on which all simulated data is generated.
     */
    private static Timer timer = new Timer("Simulated Data Generator", true);

    /**
     * Cache for all functions created for each data recipe.
     */
    private static Map<DataRecipe, Set<Simulation<?>>> registeredFunctions = new ConcurrentHashMap<DataRecipe, Set<Simulation<?>>>();

    @Override
    public void connect(DataRecipe recipe) {
        // First create all the functions for the recipe
        TimeStamp startTime = TimeStamp.now();
        Set<Simulation<?>> functions = new HashSet<Simulation<?>>();
        for (Map.Entry<Collector, Map<String, ValueCache>> collEntry : recipe.getChannelsPerCollectors().entrySet()) {
            Collector collector = collEntry.getKey();
            for (Map.Entry<String, ValueCache> entry : collEntry.getValue().entrySet()) {
                Simulation<?> simFunction = connectSingle(collector, entry.getKey(), entry.getValue());
                functions.add(simFunction);
            }
        }

        // Synchronize the timing of the simulated channel
        // and start them
        for (Simulation<?> function : functions) {
            if (function != null) {
                function.setLastTime(startTime);
                function.start(timer);
            }
        }

        // Keep functions for later disconnections
        registeredFunctions.put(recipe, functions);
    }

    @Override
    public void disconnect(DataRecipe recipe) {
        // Get all the function registered for this recipe and stop them
        Set<Simulation<?>> functions = registeredFunctions.get(recipe);
        for (Simulation<?> function : functions) {
            if (function != null)
                function.stop();
        }
        registeredFunctions.remove(recipe);

        // Purge timer, or tasks will remain and memory would leak
        timer.purge();
    }

    private Simulation<?> connectSingle(Collector collector, String pvName, ValueCache<?> cache) {
        if (cache.getType().equals(VDouble.class)) {
            @SuppressWarnings("unchecked")
            ValueCache<VDouble> vDoubleCache = (ValueCache<VDouble>) cache;
            return connectVDouble(pvName, collector, vDoubleCache);
        } else {
            throw new UnsupportedOperationException("Type " + cache.getType().getName() + " is not yet supported");
        }
    }

    private Simulation<?> connectVDouble(String name, Collector collector, ValueCache<VDouble> cache) {
        @SuppressWarnings("unchecked")
        final Simulation<VDouble> simulation = (Simulation<VDouble>) NameParser.createFunction(name);
        simulation.initialize(collector, cache);
        return simulation;
    }

}
