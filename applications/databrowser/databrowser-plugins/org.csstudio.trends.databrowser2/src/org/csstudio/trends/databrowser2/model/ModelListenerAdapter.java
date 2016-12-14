/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import java.time.Instant;
import java.util.Optional;

/** Helper for implementing {@link ModelListener}
 *  @author Kay Kasemir
 */
public class ModelListenerAdapter implements ModelListener
{
    @Override
    public void changedSaveChangesBehavior(final boolean save_changes)  { /* NOP */}

    @Override
    public void changedTitle()  { /* NOP */}

    @Override
    public void changedLayout() { /* NOP */}

    @Override
    public void changedTiming() { /* NOP */}

    @Override
    public void changedArchiveRescale() { /* NOP */}

    @Override
    public void changedColorsOrFonts() { /* NOP */}

    @Override
    public void changedTimerange() { /* NOP */}

    @Override
    public void changeTimeAxisConfig() { /* NOP */}

    @Override
    public void changedAxis(Optional<AxisConfig> axis) { /* NOP */}

    @Override
    public void itemAdded(ModelItem item) { /* NOP */}

    @Override
    public void itemRemoved(ModelItem item) { /* NOP */}

    @Override
    public void changedItemVisibility(ModelItem item) { /* NOP */}

    @Override
    public void changedItemLook(ModelItem item) { /* NOP */}

    @Override
    public void changedItemDataConfig(PVItem item) { /* NOP */}

    @Override
    public void scrollEnabled(boolean scroll_enabled) { /* NOP */}

    @Override
    public void changedAnnotations() { /* NOP */}

    @Override
    public void itemRefreshRequested(PVItem item) { /* NOP */}

    @Override
    public void selectedSamplesChanged() { /* NOP */}

    @Override
    public void xAxisLabelChanged(Instant time) { /* NOP */}

}
