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
package org.csstudio.archive.common.service.util;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.time.TimeInstant;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 11.04.2011
 */
// CHECKSTYLE OFF: AbstractClassName Turned off since this class provides static access methods
public abstract class DataRescueResult {
// CHECKSTYLE ON: AbstractClassName

    /**
     * Inner class to capture a success result.
     *
     * @author bknerr
     * @since 11.04.2011
     */
    private static final class SuccessResult extends DataRescueResult {
        /**
         * Constructor.
         */
        public SuccessResult(@Nonnull final String filePath,
                             @Nonnull final TimeInstant time) {
            super(filePath, time);
        }
        @Override
        public boolean hasSucceeded() {
            return true;
        }
    }

    /**
     * Inner class to capture a failure result.
     *
     * @author bknerr
     * @since 11.04.2011
     */
    private static final class FailureResult extends DataRescueResult {
        /**
         * Constructor.
         */
        public FailureResult(@Nonnull final String filePath,
                             @Nonnull final TimeInstant time) {
            super(filePath, time);
        }
        @Override
        public boolean hasSucceeded() {
            return false;
        }
    }

    private final String _filePath;
    private final TimeInstant _time;

    /**
     * Constructor.
     */
    DataRescueResult(@Nonnull final String filePath,
                     @Nonnull final TimeInstant time) {
        _filePath = filePath;
        _time = time;
    }

    @Nonnull
    public static DataRescueResult success(@Nonnull final String filePath,
                                           @Nonnull final TimeInstant time) {
        return new SuccessResult(filePath, time);
    }

    @Nonnull
    public static DataRescueResult failure(@Nonnull final String filePath,
                                           @Nonnull final TimeInstant time) {
        return new FailureResult(filePath, time);
    }

    public abstract boolean hasSucceeded();

    @Nonnull
    public String getFilePath() {
        return _filePath;
    }

    @Nonnull
    public TimeInstant getTime() {
        return _time;
    }
}
