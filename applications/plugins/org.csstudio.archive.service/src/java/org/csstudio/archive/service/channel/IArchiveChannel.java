/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.archive.service.channel;

import javax.annotation.Nonnull;

import org.csstudio.archive.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.service.samplemode.ArchiveSampleModeId;
import org.csstudio.domain.desy.common.id.Identifiable;
import org.csstudio.domain.desy.time.TimeInstant;

/**
 * Read only interface of an channel configuration in the archive.
 *
 * @author bknerr
 * @since 09.11.2010
 */
public interface IArchiveChannel extends Identifiable<ArchiveChannelId> {

    /**
     * @return the name of the channel
     */
    @Nonnull
    public String getName();

    /**
     * @return Channel group ID
     */
    @Nonnull
    public ArchiveChannelGroupId getGroupId();

    /**
     * @return the sample mode (int means scan or monitor typically)
     */
    @Nonnull
    public ArchiveSampleModeId getSampleModeId();

    /**
     * @return
     */
    public double getSamplePeriod();

    /**
     * @return Sample mode configuration value, e.g. 'delta' for Monitor
     */
    public double getSampleValue();

    /**
     * @return the timestamp of the latest archived sample for this channel
     */
    @Nonnull
    public TimeInstant getLatestTimestamp();

}
