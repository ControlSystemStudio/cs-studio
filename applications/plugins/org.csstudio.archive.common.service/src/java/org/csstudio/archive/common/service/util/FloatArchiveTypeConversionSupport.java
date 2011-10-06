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
package org.csstudio.archive.common.service.util;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.ArchiveLimitsChannel;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.controlsystem.IArchiveControlSystem;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.TypeSupportException;

/**
 * Type conversions for {@link Float}.
 *
 * @author bknerr
 * @since 14.12.2010
 */
public class FloatArchiveTypeConversionSupport extends AbstractNumberArchiveTypeConversionSupport<Float> {


    /**
     * Constructor.
     * @param type
     */
    FloatArchiveTypeConversionSupport() {
        super(Float.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Float convertFromArchiveString(@Nonnull final String value) throws TypeSupportException {
        try {
            return Float.parseFloat(value);
        } catch (final NumberFormatException e) {
            throw new TypeSupportException("Parsing failed.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Float convertFromDouble(@Nonnull final Double value) throws TypeSupportException {
        return value.floatValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    // CHECKSTYLE OFF : ParameterNumber
    protected IArchiveChannel createChannel(@Nonnull final ArchiveChannelId id,
                                            @Nonnull final String name,
                                            @Nonnull final String datatype,
                                            @Nonnull final ArchiveChannelGroupId grpId,
                                            @Nonnull final TimeInstant time,
                                            @Nonnull final IArchiveControlSystem cs,
                                            final boolean enabled,
                                            @Nonnull final Float low,
                                            @Nonnull final Float high) {
        // CHECKSTYLE ON : ParameterNumber
        return new ArchiveLimitsChannel<Float>(id, name, datatype, grpId, time, cs, enabled, low, high);
    }
}
