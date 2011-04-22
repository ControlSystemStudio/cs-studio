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
package org.csstudio.domain.desy.system;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.time.TimeInstant;

/**
 * Replacement for my very own ICssValueStuff
 *
 * @author bknerr
 * @since 09.02.2011
 *
 * @param <T> the type of the system variable
 */
public abstract class AbstractSystemVariable<T>  implements ISystemVariable<T> {

    private static final long serialVersionUID = 1867661799693271466L;

    private final String _name;
    private final T _data;
    private final ControlSystem _origin;
    private final TimeInstant _timestamp;

    /**
     * Constructor.
     */
    public AbstractSystemVariable(@Nonnull final String name,
                                  @Nonnull final T data,
                                  @Nonnull final ControlSystem origin,
                                  @Nonnull final TimeInstant time) {
        _name = name;
        _data = data;
        _origin = origin;
        _timestamp = time;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public T getData() {
        return _data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ControlSystem getOrigin() {
        return _origin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public TimeInstant getTimestamp() {
        return _timestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getName() {
        return _name;
    }
}
