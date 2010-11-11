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
package org.csstudio.archive.service.adapter;

import javax.annotation.Nonnull;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.SampleMode;
import org.csstudio.archive.service.channel.IArchiveChannel;
import org.csstudio.archive.service.samplemode.IArchiveSampleMode;
import org.csstudio.platform.data.ITimestamp;
import org.joda.time.DateTime;

/**
 * Adapter interface to be implemented by the archive dao impls.
 *
 * @author bknerr
 * @since 10.11.2010
 */
public interface IArchiveEngineAdapter {

    /**
     * Maps the channel DTO from the DAO layer to the specific engine classes.
     * @param channelDTO the dTO for the archive channel
     * @param name the name of the channel
     *
     * @return the channel configuration engine object
     */
    @Nonnull
    public ChannelConfig adapt(@Nonnull final String name,
                               @Nonnull final IArchiveChannel channelDTO,
                               @Nonnull final IArchiveSampleMode sampleModeDTO);

    /**
     * Maps the sample mode DTO from the DAO layer to the specific engine classes.
     * @param sampleModeDTO the sample mode dTO for the archive channel
     *
     * @return the sample mode engine class object
     */
    @Nonnull
    public SampleMode adapt(@Nonnull final IArchiveSampleMode sampleModeDTO);

    /**
     * Maps the joda time DateTime object to the ITimestamp type.
     * @param time the joda time
     * @return the css timestamp
     */
    @Nonnull
    public ITimestamp adapt(@Nonnull final DateTime time);

}
