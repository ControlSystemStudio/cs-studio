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
package org.csstudio.archive.common.requesttype;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;

/**
 * Common interface for (archive) request types.
 * Any service implementor has to provide a suitable data structure (probably enums) for the
 * set of different request types.
 *
 * @author bknerr
 * @since 05.01.2011
 */
public interface IArchiveRequestType {

    /**
     * The description of the specific request type, and its - optional - type parameters.
     *
     * @return the description
     */
    @Nonnull
    String getDescription();

    /**
     * Identifier for this request type, supposed to be unique among a given set
     * of request types
     *
     * @return the identifier
     */
    @Nonnull
    String getTypeIdentifier();

    /**
     * Any request types may have a set of parameters specifying itself.
     * For instance, for a request type AVERAGE one could think of parameter "windowLength", which
     * is an integer.
     * In case there is such a parameter that is adjustable by the client, the returned class yields
     * access to it.
     *
     * @return the data structure populating and giving access to the type's parameters.
     */
    @Nonnull
    ImmutableSet<IArchiveRequestTypeParameter<?>> getParameters();

    /**
     * Sets the parameter with the given id to the new value, unless such a parameter does not exist for this
     * request type or the newValue's type does not match. <br/>
     *
     * Another possibility is that any parameter provides toString and fromString methods,
     * to being called internally and the parameter description in the according type description
     * populates the required format (cmp java.util.Number.toString and in Byte.parseByte,
     * Double.parseDouble,...)
     * In other words any parameter type is carrying its own validator invisibly with it, the
     * signature would slightly change to: <br/>
     * void setParameter(\@Nonnull final String id, \@Nonnull final String newValue) throws RequestTypeParameterException;
     *
     * @param id
     * @param newValue
     * @throws RequestTypeParameterException
     */
    void setParameter(@Nonnull final String id, @Nonnull final Object newValue) throws RequestTypeParameterException;
    void setParameter(@Nonnull final String id, @Nonnull final String newValue) throws RequestTypeParameterException;

    /**
     * Returns a clone of the parameter with the given id, if it exists and its value's type matches the given type.
     * Otherwise an exception is thrown.
     * @param <T>
     * @param id
     * @param clazz
     * @return
     * @throws RequestTypeParameterException
     */
     @CheckForNull
     <T> IArchiveRequestTypeParameter<T> getParameter(@Nonnull final String id,
                                                      @Nonnull final Class<T> clazz) throws RequestTypeParameterException;
}
