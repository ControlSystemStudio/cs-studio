/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.undo;

import org.csstudio.swt.rtplot.Messages;
import org.csstudio.swt.rtplot.internal.AnnotationImpl;
import org.csstudio.swt.rtplot.internal.Plot;

/** Action to add annotation
 *  @author Kay Kasemir
 */
public class AddAnnotationAction<XTYPE extends Comparable<XTYPE>> extends UndoableAction
{
    final private Plot<XTYPE> plot;
    final private AnnotationImpl<XTYPE> annotation;

    public AddAnnotationAction(final Plot<XTYPE> plot, final AnnotationImpl<XTYPE> annotation)
    {
        super(Messages.AddAnnotation);
        this.plot = plot;
        this.annotation = annotation;
    }

    @Override
    public void run()
    {
        plot.addAnnotation(annotation);
    }

    @Override
    public void undo()
    {
        plot.removeAnnotation(annotation);
    }
}
