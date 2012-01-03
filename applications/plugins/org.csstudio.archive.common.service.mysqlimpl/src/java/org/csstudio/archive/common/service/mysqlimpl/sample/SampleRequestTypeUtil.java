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

import org.csstudio.archive.common.service.mysqlimpl.requesttypes.DesyArchiveRequestType;
import org.csstudio.archive.common.service.util.ArchiveTypeConversionSupport;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.joda.time.Duration;

/**
 * Static helper class to determine sample request types based on the given request type and of the
 * actual type of the sample values.
 *
 * @author bknerr
 * @since 10.08.2011
 */
public final class SampleRequestTypeUtil {
    /**
     * Constructor.
     */
    private SampleRequestTypeUtil() {
        ArchiveTypeConversionSupport.install();
    }

    /**
     * Computes the request type according the given datatype and the time interval.
     * If the data type is optimizable, then the time interval is checked and the request is
     * adjusted accordingly.
     * If not so, a raw request type is used, either the multiscalar or the scalar one depending on
     * what the datatype is.
     *
     * @param dataType the data type
     * @param start the start of the time interval
     * @param end the end of the time interval
     * @return the archive request type
     * @throws TypeSupportException
     */
    @Nonnull
    public static DesyArchiveRequestType determineRequestType(@Nonnull final String dataType,
                                                              @Nonnull final TimeInstant start,
                                                              @Nonnull final TimeInstant end) throws TypeSupportException {
        if (ArchiveTypeConversionSupport.isDataTypeOptimizable(dataType)) {
            return computeReducedDataSetTable(start, end);
        }
        return DesyArchiveRequestType.RAW;
    }

    @Nonnull
    private static DesyArchiveRequestType computeReducedDataSetTable(@Nonnull final TimeInstant s,
                                                                    @Nonnull final TimeInstant e) {
        final Duration d = new Duration(s.getInstant(), e.getInstant());
        if (d.isLongerThan(Duration.standardDays(45))) {
            return DesyArchiveRequestType.AVG_PER_HOUR;
        } else if (d.isLongerThan(Duration.standardDays(1))) {
            return DesyArchiveRequestType.AVG_PER_MINUTE;
        } else {
            return DesyArchiveRequestType.RAW;
        }
    }
}
