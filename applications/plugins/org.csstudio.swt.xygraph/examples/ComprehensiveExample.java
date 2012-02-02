import java.util.Calendar;

import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider;
import org.csstudio.swt.xygraph.dataprovider.Sample;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.ToolbarArmedXYGraph;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.Trace.BaseLine;
import org.csstudio.swt.xygraph.figures.Trace.ErrorBarType;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.linearscale.AbstractScale.LabelSide;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.swt.xygraph.undo.ZoomType;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.KeyEvent;
import org.eclipse.draw2d.KeyListener;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A Comprehensive Example

 * @author Xihui Chen
 */
public class ComprehensiveExample {
	public static void main(final String[] args) {
		final Shell shell = new Shell();
		shell.setSize(800, 500);
	    shell.open();

		final LightweightSystem lws = new LightweightSystem(shell);
		final XYGraphTest testFigure = new XYGraphTest();
		lws.setContents(testFigure);

	    shell.setText("Comprehensive Example");
	    final Display display = Display.getDefault();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch()) {
            display.sleep();
        }
	    }
	}
}


class XYGraphTest extends Figure {
	public Trace trace1;
	public Trace trace2;
	public Trace trace3;
	public XYGraph xyGraph;
	public Runnable updater;
	private double updateIndex = 0;
	private final CircularBufferDataProvider trace2Provider;
	boolean running = false;
	private long t;
	private final Trace trace4;
	private final ToolbarArmedXYGraph toolbarArmedXYGraph;
	public XYGraphTest() {

		xyGraph = new XYGraph();
		xyGraph.setTitle("XY Graph Test");
		xyGraph.setFont(XYGraphMediaFactory.getInstance().getFont(XYGraphMediaFactory.FONT_TAHOMA));
		xyGraph.primaryXAxis.setTitle("Time");
		xyGraph.primaryYAxis.setTitle("Amplitude");
		xyGraph.primaryXAxis.setRange(new Range(0,200));
		xyGraph.primaryXAxis.setDateEnabled(true);
		xyGraph.primaryYAxis.setAutoScale(true);
		xyGraph.primaryXAxis.setAutoScale(true);
		xyGraph.primaryXAxis.setShowMajorGrid(true);
		xyGraph.primaryYAxis.setShowMajorGrid(true);
		xyGraph.primaryXAxis.setAutoScaleThreshold(0);

		final Axis x2Axis = new Axis("X-2", false);
		x2Axis.setTickLableSide(LabelSide.Secondary);
		//x2Axis.setAutoScale(true);
		xyGraph.addAxis(x2Axis);


		final Axis y2Axis = new Axis("Log Scale", true);
		y2Axis.setRange(10, 1000);
		y2Axis.setLogScale(true);
		//y2Axis.setAutoScale(true);
		y2Axis.setForegroundColor(XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_PINK));
		y2Axis.setTickLableSide(LabelSide.Secondary);
		xyGraph.addAxis(y2Axis);
		
		Axis y3Axis = new Axis("Y-3", true);
		y3Axis.setForegroundColor(XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_BLUE));
		y3Axis.setTickLableSide(LabelSide.Secondary);
		y3Axis.setRange(new Range(-2, 3));
		y3Axis.setShowMajorGrid(false);
		y3Axis.setAutoScale(true);
		xyGraph.addAxis(y3Axis);
		
		trace2Provider = new CircularBufferDataProvider(true);
		trace2Provider.setBufferSize(100);
		trace2Provider.setUpdateDelay(100);

		trace2 = new Trace("Trace 2", xyGraph.primaryXAxis, xyGraph.primaryYAxis, trace2Provider);
		trace2.setDataProvider(trace2Provider);
		trace2.setTraceType(TraceType.SOLID_LINE);
		trace2.setLineWidth(1);
		trace2.setPointStyle(PointStyle.CIRCLE);
		trace2.setPointSize(1);
		trace2.setBaseLine(BaseLine.NEGATIVE_INFINITY);
		trace2.setAreaAlpha(100);
		trace2.setAntiAliasing(true);
		trace2.setErrorBarEnabled(true);
		//trace2.setDrawYErrorInArea(true);
		trace2.setYErrorBarType(ErrorBarType.BOTH);
		trace2.setXErrorBarType(ErrorBarType.NONE);
		trace2.setErrorBarCapWidth(3);
		xyGraph.addTrace(trace2);

		final CircularBufferDataProvider trace3Provider = new CircularBufferDataProvider(true);
		trace3 = new Trace("Trace3", xyGraph.primaryXAxis, xyGraph.primaryYAxis, trace3Provider);
		trace3.setPointStyle(PointStyle.XCROSS);
		trace3.setTraceType(TraceType.BAR);
		trace3.setLineWidth(4);
		trace3Provider.setUpdateDelay(100);
		xyGraph.addTrace(trace3);

		final CircularBufferDataProvider trace4Provider = new CircularBufferDataProvider(false);
		trace4 = new Trace("Trace 4-Lissajous", x2Axis, y2Axis, trace4Provider);
		trace4.setPointStyle(PointStyle.POINT);
		trace4.setPointSize(2);

		trace4Provider.setUpdateDelay(100);
		trace4Provider.setBufferSize(100);
		xyGraph.addTrace(trace4);

		toolbarArmedXYGraph = new ToolbarArmedXYGraph(xyGraph);
		add(toolbarArmedXYGraph);

		//add key listener to XY-Graph. The key pressing will only be monitored when the
		//graph gains focus.
		
		xyGraph.setFocusTraversable(true);
		xyGraph.setRequestFocusEnabled(true);
		
		xyGraph.getPlotArea().addMouseListener(new MouseListener.Stub(){
			@Override
			public void mousePressed(final MouseEvent me) {
				xyGraph.requestFocus();
			}
		});	


		xyGraph.addKeyListener(new KeyListener.Stub(){
			@Override
			public void keyPressed(final KeyEvent ke) {
				if((ke.getState() == SWT.CONTROL) && (ke.keycode == 'z')){
					xyGraph.getOperationsManager().undo();
				}
				if((ke.getState() == SWT.CONTROL) && (ke.keycode == 'y')){
					xyGraph.getOperationsManager().redo();
				}
				if((ke.getState() == SWT.CONTROL) && (ke.keycode == 'x')){
					xyGraph.performAutoScale();
				}
				if((ke.getState() == SWT.CONTROL) && (ke.keycode + 'a' -97 == 't')){
					switch (xyGraph.getZoomType()) {
					case RUBBERBAND_ZOOM:
						xyGraph.setZoomType(ZoomType.HORIZONTAL_ZOOM);
						break;
					case HORIZONTAL_ZOOM:
						xyGraph.setZoomType(ZoomType.VERTICAL_ZOOM);
						break;
					case VERTICAL_ZOOM:
						xyGraph.setZoomType(ZoomType.ZOOM_IN);
						break;
					case ZOOM_IN:
						xyGraph.setZoomType(ZoomType.ZOOM_OUT);
						break;
					case ZOOM_OUT:
						xyGraph.setZoomType(ZoomType.PANNING);
						break;
					case PANNING:
						xyGraph.setZoomType(ZoomType.NONE);
						break;
					case NONE:
						xyGraph.setZoomType(ZoomType.RUBBERBAND_ZOOM);
						break;
					default:
						break;
					}
				}
			}
		});

		updater = new Runnable(){
			public void run() {
				 t+=60000;
				trace3Provider.setCurrentYData(Math.cos(updateIndex), t);
				if(((updateIndex >= 10) && (updateIndex <=10.5)) ||
					((updateIndex >= 20) && (updateIndex <=20.2))	){
					trace2Provider.addSample(new Sample(t, Double.NaN));
					running = false;
				}
				else{
					final Sample sampe = new Sample(t, Math.sin(updateIndex),
					        0.1* Math.random(), 0.1*Math.random(),
					        t*0.0000001* Math.random(), t*0.0000001* Math.random(),
					        "sdfsf");
					trace2Provider.addSample(sampe);
				}

				trace2Provider.setCurrentYDataTimestamp(t);
				trace4Provider.setCurrentXData(Math.sin(updateIndex + 10)*20+50);
				trace4Provider.setCurrentYData(Math.cos(updateIndex)*30+50);
				updateIndex+=0.1;

				if(running) {
                    Display.getCurrent().timerExec(1, this);
                }
			}
		};

	Display.getCurrent().timerExec(1000, updater);
		running = true;
		t = Calendar.getInstance().getTimeInMillis();
	}

	@Override
	protected void layout() {
		toolbarArmedXYGraph.setBounds(bounds.getCopy().shrink(5, 5));
		super.layout();
	}



}
