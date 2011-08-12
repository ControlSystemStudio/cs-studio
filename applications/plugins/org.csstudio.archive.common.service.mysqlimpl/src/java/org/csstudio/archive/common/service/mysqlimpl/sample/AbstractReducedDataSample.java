/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.archive.common.service.mysqlimpl.sample;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.domain.desy.time.TimeInstant;

/**
 * Reduced data archive sample (not a real sample, but a derived value).
 *
 * @author bknerr
 * @since 21.07.2011
 */
abstract class AbstractReducedDataSample {

    private final ArchiveChannelId _channelId;
    private final TimeInstant _timestamp;
    private final Double _avg;
    private final Double _min;
    private final Double _max;

    /**
     * Constructor.
     */
    protected AbstractReducedDataSample(@Nonnull final ArchiveChannelId id,
                                        @Nonnull final TimeInstant timestamp,
                                        @Nonnull final Double avg,
                                        @Nonnull final Double min,
                                        @Nonnull final Double max) {
        _channelId = id;
        _timestamp = timestamp;
        _avg = avg;
        _min = min;
        _max = max;
    }
    @Nonnull
    protected ArchiveChannelId getChannelId() {
        return _channelId;
    }
    @Nonnull
    public TimeInstant getTimestamp() {
        return _timestamp;
    }
    @Nonnull
    public Double getAvg() {
        return _avg;
    }
    @Nonnull
    public Double getMin() {
        return _min;
    }
    @Nonnull
    public Double getMax() {
        return _max;
    }
}
