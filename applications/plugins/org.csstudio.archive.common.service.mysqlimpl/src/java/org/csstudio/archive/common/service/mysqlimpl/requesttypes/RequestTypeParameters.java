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
package org.csstudio.archive.common.service.mysqlimpl.requesttypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.requesttype.IArchiveRequestTypeParameter;
import org.csstudio.archive.common.requesttype.RequestTypeParameterException;

/**
 * Parameter available to the archive request types of this service implementation.
 *
 * @author bknerr
 * @since 26.01.2011
 */
public final class RequestTypeParameters {

    public static final NumOfBinsRequestParameter NUM_OF_BINS =
        new NumOfBinsRequestParameter();
    public static final PrecisionRequestParameter PRECISION =
        new PrecisionRequestParameter();

    /**
     * Internal typed request parameter.
     *
     * @author bknerr
     * @since 03.02.2011
     */
    private static final class PrecisionRequestParameter implements
                                                        IArchiveRequestTypeParameter<Double> {
        private Double _value;

        /**
         * Constructor.
         */
        public PrecisionRequestParameter() {
            _value = Double.valueOf(2.0);
        }

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
            return _value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public Class<Double> getValueType() {
            return Double.class;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public Double toValue(@Nonnull final String value) throws RequestTypeParameterException {
            try {
                return Double.parseDouble(value);
            } catch (final NumberFormatException e) {
                throw new RequestTypeParameterException("Value " + value +
                                                        " could not be parsed to " +
                                                        getValueType().getName(), e);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public PrecisionRequestParameter deepCopy() {
            return new PrecisionRequestParameter();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(@Nullable final Object obj) {
            if (obj instanceof PrecisionRequestParameter) {
                // no non-static fields
                return true;
            }
            return false;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
         // no non-static fields
            return super.hashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setValue(@Nonnull final Double newValue) throws RequestTypeParameterException {
            _value = newValue;
        }
    }

    /**
     * Internal typed request parameter.
     *
     * @author bknerr
     * @since 03.02.2011
     */
    private static final class NumOfBinsRequestParameter implements IArchiveRequestTypeParameter<Integer> {

        private Integer _value;

        /**
         * Constructor.
         */
        public NumOfBinsRequestParameter() {
            _value = Integer.valueOf(1000);
        }

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
            return _value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public Class<Integer> getValueType() {
            return Integer.class;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public Integer toValue(@Nonnull final String value) throws RequestTypeParameterException {
            try {
                return Integer.parseInt(value);
            } catch (final NumberFormatException e) {
                throw new RequestTypeParameterException("Value " + value +
                                                        " could not be parsed to " +
                                                        getValueType().getName(), e);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public NumOfBinsRequestParameter deepCopy() {
            return new NumOfBinsRequestParameter();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void setValue(@Nonnull final Integer newValue) throws RequestTypeParameterException {
            _value = newValue;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(@Nullable final Object obj) {
            if (obj instanceof NumOfBinsRequestParameter) {
                // no non-static fields
                return true;
            }
            return false;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            // no non-static fields
            return super.hashCode();
        }
    }
    /**
     * Constructor.
     */
    private RequestTypeParameters() {
        // Don't instantiate
    }
}
