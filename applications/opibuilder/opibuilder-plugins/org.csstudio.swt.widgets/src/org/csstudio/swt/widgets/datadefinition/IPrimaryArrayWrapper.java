/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.datadefinition;

/**A wrapper that wraps primary data types array.
 *  This allows clients to accept all primary data types array without converting the array type.
 * @author Xihui Chen
 *
 */
public interface IPrimaryArrayWrapper {

    /**Get the array element value at index i.
     * @param i index
     * @return the value at index i.
     */
    public double get(int i);

    /**Get size of the array.
     * @return size of the array.
     */
    public int getSize();


}
