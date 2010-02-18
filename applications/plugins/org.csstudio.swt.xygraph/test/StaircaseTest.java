import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider;
import org.csstudio.swt.xygraph.dataprovider.ISample;
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

    class TestSample implements ISample
    {
        final private double x, y;
        
        public TestSample(double y)
        {
            this.x = next_x++;
            this.y = y;
        }

        public String getInfo()
        {
            return "Test Sample";
        }
    
        public double getXMinusError()
        {
            return 0;
        }
    
        public double getXPlusError()
        {
            return 0;
        }
    
        public double getXValue()
        {
            return x;
        }
    
        public double getYMinusError()
        {
            return 1.0;
        }
    
        public double getYPlusError()
        {
            return 1.0;
        }
    
        public double getYValue()
        {
            return y;
        }
    }
    
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
        lws.setContents(plot);

        // Add data & trace
        final CircularBufferDataProvider data = new CircularBufferDataProvider(true);
        data.addSample(new TestSample(1));
        data.addSample(new TestSample(2));
        data.addSample(new TestSample(3));
        data.addSample(new TestSample(1));
        data.addSample(new TestSample(1));
        data.addSample(new TestSample(1));

        // Looks OK with this range
        xygraph.primaryXAxis.setRange(data.getXDataMinMax());
        xygraph.primaryYAxis.setRange(data.getYDataMinMax());
        
        // Should show a horizontal line with this range, but
        // instead displays nothing when both the 'start' and 'end'
        // point of the horizontal line are outside the plot range
        xygraph.primaryXAxis.setRange(4.1, 4.9);
        
        final Trace trace = new Trace("Demo", xygraph.primaryXAxis,
                xygraph.primaryYAxis, data);
        trace.setTraceType(TraceType.STEP_HORIZONTALLY);
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
