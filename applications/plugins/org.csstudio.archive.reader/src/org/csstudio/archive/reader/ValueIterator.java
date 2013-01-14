/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader;

import org.epics.vtype.VType;

/** In principle this is like
 *  <code>Iterator&lt;Value&gt;</code>, but allows next() to
 *  throw an exception.
 *  @author Kay Kasemir
 */
public interface ValueIterator
{
    /** Returns <tt>true</tt> if the iteration has more elements. (In other
     *  words, returns <tt>true</tt> if <tt>next</tt> would return an element
     *  rather than throwing an exception.)
     *
     *  @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext();

    /** Returns the next element in the iteration.  Calling this method
     *  repeatedly until the {@link #hasNext()} method returns false will
     *  return each element in the underlying collection exactly once.
     *
     *  @return the next element in the iteration.
     *  @exception on Error in archive access
     */
    public VType next() throws Exception;

    /** Must be called to release resources */
    public void close();
}
