package org.csstudio.swt.chart.actions;

import org.csstudio.swt.chart.Activator;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;

/** An Action for saving the current image to a file.
 *  <p>
 *  Suggested use is in the context menu of an editor or view that
 *  uses the InteractiveChart.
 *  
 *  @author Kay Kasemir
 */
public class PrintImageAction extends Action
{
    private final Chart chart;

    /** Constructor */
    public PrintImageAction(Chart chart)
    {
        super(Messages.PrintImage_ActionName,
              Activator.getImageDescriptor("icons/snapshot.gif")); //$NON-NLS-1$
        this.chart = chart;
        setToolTipText(Messages.PrintImage_ActionName_TT);
    }
    
    /** {@inheritDoc} */
    @Override
    public void run()
    {
        final Image snapshot = chart.createSnapshot();
        if (snapshot == null)
            return;
        try
        {
            // Printer GUI
            PrintDialog dlg = new PrintDialog(chart.getShell());
            PrinterData data = dlg.open();
            if (data == null)
                return;
            // Get filename
            if (data.printToFile == true)
                data.fileName = ImageFileName.get(chart.getShell());
            // Print
            final Printer printer = new Printer(data);
            // ... in background thread
            final Thread print_thread = new Thread("Print Thread") //$NON-NLS-1$
            {
                @Override
                public void run()
                {
                    if (!printer.startJob("Data Browser"))
                        return;
                    // TODO Compute layout
                    final Rectangle area = printer.getClientArea();
                    final Rectangle trim = printer.computeTrim(0, 0, 0, 0);
                    final GC gc = new GC(printer);
                    
                    // Print one page
                    printer.startPage();
                    gc.drawImage(snapshot, 0, 0);
                    printer.endPage();
                    // Done
                    printer.endJob();
                }
            };
            print_thread.start();
        }
        finally
        {
            snapshot.dispose();
        }
    }
}
