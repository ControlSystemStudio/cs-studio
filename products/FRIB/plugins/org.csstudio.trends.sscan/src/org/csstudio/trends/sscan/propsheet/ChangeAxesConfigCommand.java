/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.propsheet;

import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.sscan.Messages;
import org.csstudio.trends.sscan.model.AxesConfig;
import org.csstudio.trends.sscan.model.AxisConfig;

/** Undo-able command to change axis configuration
 *  @author Kay Kasemir
 */
public class ChangeAxesConfigCommand implements IUndoableCommand
{
    final private AxesConfig axes;
    final private AxesConfig old_config;
    private AxesConfig new_config;

    /** Register the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param axis Axis configuration to undo/redo
     */
    public ChangeAxesConfigCommand(final OperationsManager operations_manager,
            final AxesConfig axes)
    {
        this.axes = axes;
        this.old_config = axes.copy();
        operations_manager.addCommand(this);
    }

    /** Must be called after the original configuration was changed */
    public void rememberNewConfig()
    {
        this.new_config = axes.copy();
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
        apply(new_config);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        apply(old_config);
    }

    /** Apply whatever's different in the given configuration to the axis
     *  @param config
     */
    private void apply(final AxesConfig config)
    {
    	AxisConfig axis=axes.getXAxis();
    	AxisConfig xconfig = config.getXAxis();
    	if (axis.isVisible() != xconfig.isVisible())
    		axis.setVisible(xconfig.isVisible());
        if (! axis.getName().equals(xconfig.getName()))
            axis.setName(xconfig.getName());
        if (axis.getMin() != xconfig.getMin()  ||
            axis.getMax() != xconfig.getMax())
            axis.setRange(xconfig.getMin(), xconfig.getMax());
        if (! axis.getColor().equals(xconfig.getColor()))
            axis.setColor(xconfig.getColor());
        if (! axis.isLogScale() == xconfig.isLogScale())
            axis.setLogScale(xconfig.isLogScale());
        if (! axis.isAutoScale() == xconfig.isAutoScale())
            axis.setAutoScale(xconfig.isAutoScale());
        
        axis=axes.getYAxis();
        AxisConfig yconfig = config.getXAxis();
        if (axis.isVisible() != yconfig.isVisible())
    		axis.setVisible(yconfig.isVisible());
        if (! axis.getName().equals(yconfig.getName()))
            axis.setName(yconfig.getName());
        if (axis.getMin() != yconfig.getMin()  ||
            axis.getMax() != yconfig.getMax())
            axis.setRange(yconfig.getMin(), yconfig.getMax());
        if (! axis.getColor().equals(yconfig.getColor()))
            axis.setColor(yconfig.getColor());
        if (! axis.isLogScale() == yconfig.isLogScale())
            axis.setLogScale(yconfig.isLogScale());
        if (! axis.isAutoScale() == yconfig.isAutoScale())
            axis.setAutoScale(yconfig.isAutoScale());
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.Axis;
    }
}
