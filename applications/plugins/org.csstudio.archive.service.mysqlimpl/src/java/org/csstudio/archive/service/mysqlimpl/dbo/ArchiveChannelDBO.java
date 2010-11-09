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
package org.csstudio.archive.service.mysqlimpl.dbo;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.rdb.SampleMode;
import org.csstudio.archive.service.businesslogic.IArchiveChannel;
import org.csstudio.domain.desy.common.id.Identifiable;
import org.csstudio.domain.desy.core.channel.ChannelId;
import org.csstudio.platform.data.IMetaData;


/**
 * Database object for archive channel configuration.
 *
 * @author bknerr
 * @since 09.11.2010
 */
public final class ArchiveChannelDBO implements Identifiable<ChannelId> {

    private ChannelId _id;

    private int _groupId;

    private SampleMode _sampleMode;

    private double _sampleValue;

    private double _samplePeriod;

    /** The channel's meta data */
    private IMetaData _metaData;

    /**
     * Parameterless constructor for reflective creation.
     */
    public ArchiveChannelDBO() {
        super();
    }

    /**
     * Constructor.
     * @param other another archive channel configuration
     */
    public ArchiveChannelDBO(@Nonnull final IArchiveChannel other) {
        _id = other.getId();
        _groupId = other.getGroupId();
        _sampleMode = other.getSampleMode();
        _samplePeriod = other.getSamplePeriod();
        _metaData = other.getMetaData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public ChannelId getId() {
        return _id;
    }
    @CheckForNull
    public int getGroupId() {
        return _groupId;
    }
    @CheckForNull
    public SampleMode getSampleMode() {
        return _sampleMode;
    }

    public double getSampleValue() {
        return _sampleValue;
    }

    public double getSamplePeriod() {
        return _samplePeriod;
    }
    @CheckForNull
    public IMetaData getMetaData() {
        return _metaData;
    }

    public void setId(@Nonnull final ChannelId id) {
        _id = id;
    }

    public void setGroupId(final int groupId) {
        _groupId = groupId;
    }

    public void setSampleMode(@Nonnull final SampleMode sampleMode) {
        _sampleMode = sampleMode;
    }

    public void setSampleValue(final double sampleValue) {
        _sampleValue = sampleValue;
    }

    public void setSamplePeriod(final double samplePeriod) {
        _samplePeriod = samplePeriod;
    }

    public void setMetaData(@Nonnull final IMetaData metaData) {
        _metaData = metaData;
    }
}
