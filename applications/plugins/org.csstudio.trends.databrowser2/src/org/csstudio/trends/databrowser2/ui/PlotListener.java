/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.ui;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.swt.xygraph.undo.XYGraphMemento;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;

/** Interface used by Plot to send events in response to user input:
 *  Zoom changed, scrolling turned on/off
 *  @author Kay Kasemir
 *  
 *  Add events necessary in response of GRAPH settings changed by user
 *  ADD events link to add/remove annotation
 *  @author Laurent PHILIPPE (GANIL) 
 */
public interface PlotListener
{
    /** Called when the user enables/disables scrolling
     *  @param enable_scrolling true when user requested scrolling via GUI
     */
    public void scrollRequested(boolean enable_scrolling);

    /** Called when the user requests time config dialog. */
    public void timeConfigRequested();

    /** Called when the user enables/disables scrolling
     *  @param start_ms New time axis start time in ms since 1970
     *  @param end_ms ... end time ...
     */
    public void timeAxisChanged(long start_ms, long end_ms);

    /** Called when the user changed a value (Y) axis
     *  @param index Value axis index 0, 1, ...
     *  @param lower Lower range limit
     *  @param upper Upper range limit
     */
    public void valueAxisChanged(int index, double lower, double upper);

    /** Received a name, presumably a PV name via drag & drop
     *  @param name PV(?) name
     */
    public void droppedName(String name);

    /** Received a PV name and/or archive data source via drag & drop
     *  @param name PV name or <code>null</code>
     *  @param archive Archive data source or <code>null</code>
     */
    public void droppedPVName(ProcessVariable name, ArchiveDataSource archive);

    /**
	 * Called when the user changed graph settings
	 * @param newValue
	 *            The new graph settings
	 */
	public void xyGraphMemChanged(XYGraphMemento newValue);

}
