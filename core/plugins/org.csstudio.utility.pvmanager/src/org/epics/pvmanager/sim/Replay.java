/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.sim;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.util.TimeDuration;
import org.epics.pvmanager.util.TimeInterval;
import org.epics.pvmanager.util.TimeStamp;
import org.epics.pvmanager.data.VDouble;

/**
 * Function that reads an xml file and simulates a pv by replaying it.
 *
 * @author carcassi
 */
public class Replay extends Simulation<VDouble> {

    private TimeStamp reference = TimeStamp.now();
    private TimeDuration offset;
    private XmlValues values;

    /**
     * The URI of the file. Any of the standard protocol is supported (file:,
     * http:, ...). Relative uris are allowed, and they will be resolved on the
     * current location in the filesystem.
     *
     * @param uri the location of the playback file
     */
    public Replay(String uri) {
        super(TimeDuration.ms(10), VDouble.class);
        values = ReplayParser.parse(URI.create(uri));
        offset = ((VDouble) values.getValues().get(0)).getTimeStamp().durationFrom(reference);
    }

    @Override
    List<VDouble> createValues(TimeInterval interval) {
        TimeInterval originalInterval = interval.minus(offset);
        List<VDouble> newValues = new ArrayList<VDouble>();
        for (ReplayValue value : values.getValues()) {
            if (originalInterval.contains(value.getTimeStamp())) {
                ReplayValue copy = value.copy();
                if (values.isAdjustTime()) {
                    copy.adjustTime(offset);
                }
                newValues.add((VDouble) copy);
            }
        }
        return newValues;
    }

}
