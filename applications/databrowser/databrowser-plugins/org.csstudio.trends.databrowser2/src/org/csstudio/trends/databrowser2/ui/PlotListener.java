/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.ui;

import java.time.Instant;
import java.util.List;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.trends.databrowser2.model.AnnotationInfo;
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
    /** Called when the user requests time config dialog. */
    public void timeConfigRequested();

    /** Called when the user changes time axis, includes turning scrolling on/off
     *  @param scroll Scrolling?
     *  @param start New time axis start time
     *  @param end ... end time ...
     */
    public void timeAxisChanged(boolean scroll, Instant start, Instant end);

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

    /** Received a file name */
    public void droppedFilename(String file_name);

    /** Received updated annotations */
    public void changedAnnotations(List<AnnotationInfo> annotations);

    /** ModelItems have new selected sample */
    public void selectedSamplesChanged();
}
