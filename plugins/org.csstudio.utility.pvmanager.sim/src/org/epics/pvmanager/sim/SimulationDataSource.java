/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sim;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.vtype.DataTypeSupport;
import static org.epics.pvmanager.util.Executors.*;

/**
 * Data source to produce simulated signals that can be using during development
 * and testing.
 *
 * @author carcassi
 */
public final class SimulationDataSource extends DataSource {

    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }

    public SimulationDataSource() {
        super(false);
    }

    /**
     * Data source instance.
     *
     * @return the data source instance
     */
    public static DataSource simulatedData() {
        return SimulationDataSource.instance;
    }

    private static final Logger log = Logger.getLogger(SimulationDataSource.class.getName());
    static final SimulationDataSource instance = new SimulationDataSource();

    /**
     * ExecutorService on which all simulated data is generated.
     */
    private static ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(namedPool("PVMgr Simulator "));

    @Override
    @SuppressWarnings("unchecked")
    protected ChannelHandler createChannel(String channelName) {
        if (channelName.startsWith("const(")) {
            return new ConstantChannelHandler(channelName);
        }
        if (channelName.startsWith("delayedConnectionChannel(")) {
            return new DelayedConnectionChannelHandler(channelName, exec);
        }
        if (channelName.startsWith("intermittentChannel(")) {
            return new IntermittentChannelHandler(channelName, exec);
        }
        
        SimFunction<?> simFunction = (SimFunction<?>) NameParser.createFunction(channelName);
        return new SimulationChannelHandler(channelName, simFunction, exec);
    }

}
