import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider;
import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**A simple XYGraph with toolbar.
 * @author Xihui Chen
 *
 */
public class SimpleToolbarArmedXYGraphExample {
	public static void main(String[] args) {
		final Shell shell = new Shell();
		shell.setSize(600, 400);
	    shell.open();
	    
	    //use LightweightSystem to create the bridge between SWT and draw2D
		final LightweightSystem lws = new LightweightSystem(shell);
		
		//create a new XY Graph.
		XYGraph xyGraph = new XYGraph();
		
		ToolbarArmedXYGraph toolbarArmedXYGraph = new ToolbarArmedXYGraph(xyGraph);
		
		xyGraph.setTitle("Simple Toolbar Armed XYGraph Example");
		//set it as the content of LightwightSystem
		lws.setContents(toolbarArmedXYGraph);
		
		xyGraph.primaryXAxis.setShowMajorGrid(true);
		xyGraph.primaryYAxis.setShowMajorGrid(true);
		
		//create a trace data provider, which will provide the data to the trace.
		CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(false);
		traceDataProvider.setBufferSize(100);		
		traceDataProvider.setCurrentXDataArray(new double[]{10, 23, 34, 45, 56, 78, 88, 99});
		traceDataProvider.setCurrentYDataArray(new double[]{11, 44, 55, 45, 88, 98, 52, 23});	
		
		//create the trace
		Trace trace = new Trace("Trace1-XY Plot", 
				xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProvider);			
		
		//set trace property
		trace.setPointStyle(PointStyle.XCROSS);
		
		//add the trace to xyGraph
		xyGraph.addTrace(trace);			
	   
	    Display display = Display.getDefault();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	   
	}
}
