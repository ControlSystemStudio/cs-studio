package org.csstudio.swt.rtplot.internal;

import org.csstudio.swt.rtplot.Activator;
import org.csstudio.swt.rtplot.Messages;
import org.csstudio.swt.rtplot.RTPlot;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

/** Action to display the 'Add Annotation' dialog.
 *
 *  @param <XTYPE> Data type used for the {@link PlotDataItem}
 *  @author Rebecca Williams (OSL)
 */
public class AddAnnotationDialogAction<XTYPE extends Comparable<XTYPE>> extends Action
{
    final private RTPlot<XTYPE> plot;
    private Shell shell;

    public AddAnnotationDialogAction(final RTPlot<XTYPE> plot, Shell shell)
    {
        super(Messages.AddAnnotation, Activator.getIcon("add_annotation"));
        this.plot = plot;
        this.shell = shell;
    }

    @Override
    public void run()
    {
       new AddAnnotationDialog<XTYPE>(shell, plot).open();
    }
}
