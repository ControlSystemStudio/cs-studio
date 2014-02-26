/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.internal;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** Future that has completed, offering a value right away
 *  @author Kay Kasemir
 */
public class ImmediateFuture<T> implements Future<T>
{
    final private T result;
    
    public ImmediateFuture(final T result)
    {
        this.result = result;
    }

    @Override
    public boolean isDone()
    {
        return true;
    }
    
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning)
    {
        return false;
    }

    @Override
    public boolean isCancelled()
    {
        return false;
    }
    
    @Override
    public T get() throws InterruptedException, ExecutionException
    {
        return result;
    }

    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException
    {
        return result;
    }
}
