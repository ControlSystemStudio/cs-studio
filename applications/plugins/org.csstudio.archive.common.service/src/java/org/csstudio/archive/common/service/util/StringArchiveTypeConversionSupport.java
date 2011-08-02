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

import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.typesupport.TypeSupportException;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 13.12.2010
 */
public class StringArchiveTypeConversionSupport extends ArchiveTypeConversionSupport<String> {


    /**
     * Constructor.
     * @param type
     */
    StringArchiveTypeConversionSupport() {
        super(String.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String convertToArchiveString(@Nonnull final String value) throws TypeSupportException {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String convertFromArchiveString(@Nonnull final String value) {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String convertFromDouble(@Nonnull final Double value) {
        return value.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<String> convertFromArchiveStringToMultiScalar(@Nonnull final Class<?> collectionClass,
                                                                    @Nonnull final String values) throws TypeSupportException {
        return Lists.newArrayList(Splitter.on(ARCHIVE_COLLECTION_ELEM_SEP).split(values));
    }
}
