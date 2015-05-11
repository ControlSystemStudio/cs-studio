/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.undo;

import org.csstudio.swt.rtplot.Annotation;
import org.csstudio.swt.rtplot.Messages;
import org.csstudio.swt.rtplot.RTPlot;

/** Action to remove annotation
 *  @author Kay Kasemir
 */
public class RemoveAnnotationAction<XTYPE extends Comparable<XTYPE>> extends UndoableAction
{
    final private RTPlot<XTYPE> plot;
    final private Annotation<XTYPE> annotation;

    public RemoveAnnotationAction(final RTPlot<XTYPE> plot, final Annotation<XTYPE> annotation)
    {
        super(Messages.EditAnnotation);
        this.plot = plot;
        this.annotation = annotation;
    }

    @Override
    public void run()
    {
        plot.removeAnnotation(annotation);
    }

    @Override
    public void undo()
    {
        plot.addAnnotation(annotation);
    }
}
