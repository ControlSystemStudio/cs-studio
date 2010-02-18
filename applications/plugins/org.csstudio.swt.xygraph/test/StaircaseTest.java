import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider;
import org.csstudio.swt.xygraph.dataprovider.Sample;
import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.figures.XYGraphFlags;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** JUnit demo of the plot with 'staircase' data, including gaps,
 *  as it can be used for showing historic data.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StaircaseTest
{
    private static int next_x = 1;

    @Test
    public void testStaircasePlot()
    {
        // Main window (shell)
        final Shell shell = new Shell();
        shell.setSize(800, 500);
        shell.open();
        
        // XYGraph
        final LightweightSystem lws = new LightweightSystem(shell);
        final ToolbarArmedXYGraph plot = new ToolbarArmedXYGraph(new XYGraph(),
                XYGraphFlags.SEPARATE_ZOOM | XYGraphFlags.STAGGER);
        final XYGraph xygraph = plot.getXYGraph();
        xygraph.setTransparent(false);
        xygraph.setTitle("You should see a line. Zoom out to see more data");
        lws.setContents(plot);

        // Add data & trace
        final CircularBufferDataProvider data = new CircularBufferDataProvider(true);
        data.addSample(new Sample(next_x++, 1, 1, 1, 0, 0));
        data.addSample(new Sample(next_x++, 2, 1, 1, 0, 0));
        data.addSample(new Sample(next_x++, 3, 1, 1, 0, 0));
        data.addSample(new Sample(next_x++, 1, 1, 1, 0, 0));
        data.addSample(new Sample(next_x++, 1, 1, 1, 0, 0));
        data.addSample(new Sample(next_x++, 1, 1, 1, 0, 0));
        // TODO add Double.NaN gaps

        // Always looked OK with this range
        xygraph.primaryXAxis.setRange(data.getXDataMinMax());
        xygraph.primaryYAxis.setRange(data.getYDataMinMax());
        
        // With STEP_HORIZONTALLY this should have shown just a horizontal
        // line, but a bug resulted in nothing when both the 'start' and 'end'
        // point of the horizontal line were outside the plot range.
        // Similarly, using STEP_VERTICALLY failed to draw anything when
        // both end-points of the horizontal or vertical section of the step
        // were outside the plot. (fixed)
        // 
        // There's still a question about handling 'YErrorInArea':
        // For now, the axis intersection removes x/y errors,
        // so when moving a sample with y error left or right outside
        // of the plot range, the error area suddenly shrinks when
        // the axis intersection is assumed to have +-0 y error.
        xygraph.primaryXAxis.setRange(4.1, 4.9);

        final Trace trace = new Trace("Demo", xygraph.primaryXAxis,
                xygraph.primaryYAxis, data);
        trace.setTraceType(TraceType.STEP_HORIZONTALLY);
//        trace.setTraceType(TraceType.STEP_VERTICALLY);
        trace.setPointStyle(PointStyle.NONE);
        trace.setErrorBarEnabled(true);
        trace.setDrawYErrorInArea(true);
        xygraph.addTrace(trace);        
        
        // SWT main loop
        final Display display = Display.getDefault();
        while (!shell.isDisposed()) {
          if (!display.readAndDispatch())
            display.sleep();
        }
    }
}
