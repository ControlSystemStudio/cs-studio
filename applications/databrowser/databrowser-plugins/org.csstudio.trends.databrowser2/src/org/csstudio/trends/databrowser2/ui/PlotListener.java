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

    /** Received names, presumably for PVs, via drag & drop
     *  @param name PV(?) names
     */
    public void droppedNames(String[] name);

    /** Received PV names and/or archive data sources via drag & drop
     *
     *  <p>If names with archive are received, the name and archive
     *  arrays will have the same size.
     *
     *  @param name PV names or <code>null</code>
     *  @param archive Archive data sources or <code>null</code>
     */
    public void droppedPVNames(ProcessVariable[] name, ArchiveDataSource[] archive);

    /** Received a file name */
    public void droppedFilename(String file_name);

    /** Received updated annotations */
    public void changedAnnotations(List<AnnotationInfo> annotations);

    /** ModelItems have new selected sample */
    public void selectedSamplesChanged();

    /** Plot tool bar displayed or hidden */
    public void changedToolbar(boolean visible);

    /** Plot legend displayed or hidden */
    public void changedLegend(boolean visible);
}
