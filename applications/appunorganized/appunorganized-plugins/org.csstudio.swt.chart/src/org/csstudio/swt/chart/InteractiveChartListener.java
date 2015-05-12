/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart;


/** Events that the InteractiveChart sends.
 *  @author Kay Kasemir
 */
public interface InteractiveChartListener
{
    /** The button bar became visible or hidden
     *  @param visible <code>true</code> if it's not visible
     */
    public void buttonBarChanged(boolean visible);
}
