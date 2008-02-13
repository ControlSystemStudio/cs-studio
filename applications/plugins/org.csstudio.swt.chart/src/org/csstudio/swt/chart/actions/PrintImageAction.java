package org.csstudio.swt.chart.actions;

import org.csstudio.swt.chart.Activator;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;

/** An Action for printing the current image.
 *  <p>
 *  Suggested use is in the context menu of an editor or view that
 *  uses the InteractiveChart.
 *  
 *  @author Kay Kasemir
 */
public class PrintImageAction extends Action
{
    private final Chart chart;
    private Image snapshot;
    private Printer printer;

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
        snapshot = chart.createSnapshot();
        if (snapshot == null)
            return;
        
        // Printer GUI
        PrintDialog dlg = new PrintDialog(chart.getShell());
        PrinterData data = dlg.open();
        if (data == null)
            return;
        // Get filename
        if (data.printToFile == true)
            data.fileName = ImageFileName.get(chart.getShell());
        printer = new Printer(data);
        // Print in background thread
        final Thread print_thread = new Thread("Print Thread") //$NON-NLS-1$
        {
            @Override
            public void run()
            {
                print();
            }
        };
        print_thread.start();
    }

    private void print()
    {
        if (!printer.startJob("Data Browser")) //$NON-NLS-1$
            return;
        try
        {
            // Printer page info
            final Rectangle area = printer.getClientArea();
            final Rectangle trim = printer.computeTrim(0, 0, 0, 0);
            final Point dpi = printer.getDPI();
            
            // Compute layout
            final Rectangle image_rect = snapshot.getBounds();
            // Leave one inch on each border
            final int left_right = dpi.x + trim.x;
            final int top_bottom = dpi.y + trim.y;
            final int printed_width = area.width - 2*left_right;
            // Scale height to keep the on-screen aspect ratio
            final int max_height = area.height - 2*top_bottom;
            final int printed_height = Math.min(max_height,
               image_rect.height * printed_width / image_rect.width);
            
            final GC gc = new GC(printer);
            
            // Print one page
            printer.startPage();
            gc.drawImage(snapshot, 0, 0, image_rect.width, image_rect.height,
                            left_right, top_bottom, printed_width, printed_height);
            
            printer.endPage();
            // Done
            printer.endJob();
        }
        finally
        {
            snapshot.dispose();
        }
    }

}

