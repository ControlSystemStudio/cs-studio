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
package org.csstudio.archive.common.service.mysqlimpl;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.requesttypes.IArchiveRequestTypeParameter;

/**
 * Parameter available to the archive request types of this service implementation.
 *
 * @author bknerr
 * @since 26.01.2011
 */
public final class ARTParameters {
    /**
     * Constructor.
     */
    private ARTParameters() {
        // Don't instantiate
    }

    public static final IArchiveRequestTypeParameter<Integer> NUM_OF_BINS =
        new IArchiveRequestTypeParameter<Integer>() {
        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public String getName() {
            return "numOfBins";
        }
        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public Integer getValue() {
            return Integer.valueOf(1000);
        }
    };
    public static final IArchiveRequestTypeParameter<Double> PRECISION =
        new IArchiveRequestTypeParameter<Double>() {
        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public String getName() {
            return "precision";
        }
        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public Double getValue() {
            return Double.valueOf(2.0);
        }
    };

}
