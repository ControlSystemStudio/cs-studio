/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.undo;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/** A stack with limited size.
 *  When full, oldest element will be removed.
 *  @param <T> Stack element
 *  @author Xihui Chen original org.csstudio.swt.xygraph.undo.SizeLimitedStack
 *  @author Kay Kasemir
 */
public class SizeLimitedStack<T>
{
    final private int limit;
	final private LinkedList<T> list = new LinkedList<T>();

	/**@param limit Maximum number of stack elements */
	public SizeLimitedStack(final int limit)
	{
		this.limit = limit;
	}

	/** @return <code>true</code> if stack is empty */
	public boolean isEmpty()
	{
	    return list.isEmpty();
	}

	/** @param item Item to pushed onto stack. */
	public void push(final T item)
	{
		if (list.size() >= limit)
			list.removeFirst();
		list.addLast(item);
	}

	/** @return Top element
	 *  @throws NoSuchElementException if empty
     */
    public T peek()
    {
        return list.getLast();
    }

	/** @return Item removed from top of stack.
	 *  @throws NoSuchElementException if this stack is empty
     */
	public T pop()
	{
		return list.removeLast();
	}

	/** Empty the stack */
	public void clear()
	{
		list.clear();
	}
}
