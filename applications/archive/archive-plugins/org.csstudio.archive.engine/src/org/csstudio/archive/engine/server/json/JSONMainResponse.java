/*******************************************************************************
 * Copyright (c) 2017 Science & Technology Facilities Council.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server.json;

import java.time.Duration;
import java.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.apputil.time.PeriodFormat;
import org.csstudio.archive.engine.Messages;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.model.SampleBuffer;
import org.csstudio.archive.engine.server.AbstractMainResponse;
import org.csstudio.archive.vtype.TimestampHelper;
import org.diirt.util.time.TimeDuration;
import org.eclipse.core.runtime.Platform;

/** Provide web page with engine overview in JSON.
 *  @author Dominic Oram
 */
@SuppressWarnings("nls")
public class JSONMainResponse extends AbstractMainResponse
{
    public JSONMainResponse(final EngineModel model)
    {
        super(model);
    }

    @Override
    public void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {
        final JSONRoot json = new JSONRoot(resp);

        json.writeObjectEntry(Messages.HTTP_Version, EngineModel.VERSION);
        json.writeObjectEntry(Messages.HTTP_Description, model.getName());
        json.writeObjectEntry(Messages.HTTP_Host, host + ":" + req.getLocalPort());
        json.writeObjectEntry(Messages.HTTP_State, model.getState().name());

        final Instant start = model.getStartTime();
        if (start != null) {
            json.writeObjectEntry(Messages.HTTP_StartTime, TimestampHelper.format(start));

            final double up_secs = TimeDuration.toSecondsDouble(Duration.between(start, Instant.now()));
            json.writeObjectEntry(Messages.HTTP_Uptime, PeriodFormat.formatSeconds(up_secs));
        }

        json.writeObjectEntry(Messages.HTTP_Workspace, Platform.getInstanceLocation().getURL().getFile().toString());

        json.writeObjectEntry(Messages.HTTP_GroupCount, model.getGroupCount());

        updateChannelCount();

        json.writeObjectEntry(Messages.HTTP_ChannelCount, totalChannelCount);
        json.writeObjectEntry(Messages.HTTP_Disconnected, disconnectCount);

        json.writeObjectEntry(Messages.HTTP_BatchSize, model.getBatchSize());
        json.writeObjectEntry(Messages.HTTP_WritePeriod, model.getWritePeriod());

        json.writeObjectEntry(Messages.HTTP_WriteState, SampleBuffer.isInErrorState()?
                                                        Messages.HTTP_WriteError
                                                        : "OK");

        final Instant last_write_time = model.getLastWriteTime();
        json.writeObjectEntry(Messages.HTTP_LastWriteTime, last_write_time == null?
                                                           Messages.HTTP_Never
                                                           : TimestampHelper.format(last_write_time));

        json.writeObjectEntry(Messages.HTTP_WriteCount, model.getWriteCount());
        json.writeObjectEntry(Messages.HTTP_WriteDuration, model.getWriteDuration());

        json.writeObjectEntry(Messages.HTTP_Idletime, model.getIdlePercentage());

        final Runtime runtime = Runtime.getRuntime();
        final double used_mem = runtime.totalMemory() / MB;
        final double max_mem = runtime.maxMemory() / MB;
        final double perc_mem = max_mem > 0 ?
                     used_mem / max_mem * 100.0 : 0.0;

        json.writeObjectEntry("Used Memory", used_mem);
        json.writeObjectEntry("Max Memory", max_mem);
        json.writeObjectEntry("Percentage Memory", perc_mem);

        json.close();
    }
}
