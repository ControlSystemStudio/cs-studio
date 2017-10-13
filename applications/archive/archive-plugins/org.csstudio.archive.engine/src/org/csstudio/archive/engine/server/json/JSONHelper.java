/*******************************************************************************
 * Copyright (c) 2017 Science & Technology Facilities Council.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server.json;

import java.time.Instant;

import org.csstudio.archive.engine.Messages;
import org.csstudio.archive.engine.model.ArchiveChannel;
import org.csstudio.archive.engine.model.BufferStats;
import org.csstudio.archive.engine.model.SampleBuffer;
import org.csstudio.archive.vtype.StringVTypeFormat;
import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.archive.vtype.VTypeFormat;
import org.csstudio.archive.vtype.VTypeHelper;
import org.diirt.vtype.Display;
import org.diirt.vtype.VType;

/** Helper for creating JSON for a servlet response.
 *  @author Dominic Oram
 */

public class JSONHelper extends JSONObject {

    final public static String Null = "null";
    final public static String Value = "Value";
    final public static String Timestamp = "Timestamp";
    final public static String Units = "Units";
    final public static String Alarm = "Alarm";

    static private JSONObject createObjectFromVType(VType value) {
        JSONObject output = new JSONObject();
        if (value == null) {
            output.writeObjectEntry(Value, Null);
            return output;
        }

        Instant timestamp = VTypeHelper.getTimestamp(value);
        output.writeObjectEntry(Timestamp, TimestampHelper.format(timestamp));

        VTypeFormat stringFormatter = new StringVTypeFormat();
        output.writeObjectEntry(Value, stringFormatter.format(value));
        if (value instanceof Display)
        {
            final Display display = (Display) value;
            if (display != null  &&  display.getUnits() != null) {
                output.writeObjectEntry(Units, display.getUnits());
            }
        }
        StringBuilder alarm = new StringBuilder();
        VTypeHelper.addAlarm(alarm, value);
        output.writeObjectEntry(Alarm, alarm.toString());
        return output;
    }

    static public JSONObject createChannelObject(ArchiveChannel channel) {
        JSONObject JSONchannel = new JSONObject();

        JSONchannel.writeObjectEntry(Messages.HTTP_Channel, channel.getName());
        JSONchannel.writeObjectEntry(Messages.HTTP_Connected, channel.isConnected());

        JSONchannel.writeObjectEntry(Messages.HTTP_InternalState, channel.getInternalState());
        JSONchannel.writeObjectEntry(Messages.HTTP_Mechanism, channel.getMechanism());

        JSONObject currentValue = JSONHelper.createObjectFromVType(channel.getCurrentValue());
        JSONchannel.writeObjectEntry(Messages.HTTP_CurrentValue, currentValue);

        JSONObject lastValue = JSONHelper.createObjectFromVType(channel.getLastArchivedValue());
        JSONchannel.writeObjectEntry(Messages.HTTP_LastArchivedValue, lastValue);

        JSONchannel.writeObjectEntry(Messages.HTTP_ReceivedValues, channel.getReceivedValues());

        JSONchannel.writeObjectEntry(Messages.HTTP_State, channel.isEnabled());

        SampleBuffer buffer = channel.getSampleBuffer();
        JSONchannel.writeObjectEntry(Messages.HTTP_QueueLen, buffer.getQueueSize());

        BufferStats stats = buffer.getBufferStats();
        JSONchannel.writeObjectEntry(Messages.HTTP_QueueAvg, stats.getAverageSize());
        JSONchannel.writeObjectEntry(Messages.HTTP_QueueMax, stats.getMaxSize());
        JSONchannel.writeObjectEntry(Messages.HTTP_QueueCapacity, buffer.getCapacity());
        JSONchannel.writeObjectEntry(Messages.HTTP_QueueOverruns, stats.getOverruns());

        return JSONchannel;
    }
}
