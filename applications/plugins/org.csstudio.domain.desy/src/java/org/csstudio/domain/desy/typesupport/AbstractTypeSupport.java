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
package org.csstudio.domain.desy.typesupport;

import javax.annotation.Nonnull;

import org.epics.pvmanager.TypeSupport;

/**
 * Abstract type support to encapsulate some methods from {@link TypeSupport} that
 * do not act as we'd like them to.
 *
 * @author bknerr
 * @since Mar 30, 2011
 * @param <T> the type parameter for this support's discriminator
 */
public abstract class AbstractTypeSupport<T> extends TypeSupport<T> {

    /**
     * Constructor.
     */
    protected AbstractTypeSupport(@Nonnull final Class<T> type,
                               @SuppressWarnings("rawtypes") @Nonnull final Class<? extends TypeSupport> typeSupportFamily) {
        super(type, typeSupportFamily);
    }

    @Nonnull
    protected static <T> TypeSupport<T> findTypeSupportForOrThrowTSE(@SuppressWarnings("rawtypes") @Nonnull final Class<? extends TypeSupport> supportFamily,
                                                                     @Nonnull final Class<T> typeClass)
                                                                     throws TypeSupportException {
        try {
            final TypeSupport<T> support = findTypeSupportFor(supportFamily, typeClass);
            if (support != null) {
                return support;
            }
        // CHECKSTYLE OFF: EmptyBlock
        } catch (final RuntimeException e) {
            // Ignore
        }
        // CHECKSTYLE ON: EmptyBlock
        throw new TypeSupportException("Type support for " + typeClass.getName() + " not present in family " + supportFamily.getName(), null);
    }

    public static <T> void installIfNotExists(@SuppressWarnings("rawtypes") @Nonnull final Class<? extends TypeSupport> family,
                                              @Nonnull final Class<T> type,
                                              @Nonnull final AbstractTypeSupport<T> support) {
        try {
            findTypeSupportForOrThrowTSE(family, type);
        } catch (final TypeSupportException e) {
            addTypeSupport(support);
        }
    }
}
