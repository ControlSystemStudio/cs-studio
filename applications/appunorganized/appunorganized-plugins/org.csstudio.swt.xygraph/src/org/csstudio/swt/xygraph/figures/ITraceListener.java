/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.xygraph.figures;

import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.eclipse.swt.graphics.Color;

public interface ITraceListener {

    void traceNameChanged(Trace trace, String oldName, String newName);

    void traceYAxisChanged(Trace trace, Axis oldName, Axis newName);

    void traceTypeChanged(Trace trace, TraceType old, TraceType newTraceType);

    void traceColorChanged(Trace trace, Color old, Color newColor);
}
