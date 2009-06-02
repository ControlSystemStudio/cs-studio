package org.csstudio.swt.chart.test;

import java.util.ArrayList;
import java.util.Vector;

import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDropTarget;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.ChartListener;
import org.csstudio.swt.chart.DefaultColors;
import org.csstudio.swt.chart.InteractiveChart;
import org.csstudio.swt.chart.TraceType;
import org.csstudio.swt.chart.actions.PrintCurrentImageAction;
import org.csstudio.swt.chart.actions.SaveCurrentImageAction;
import org.csstudio.swt.chart.actions.ShowButtonBarAction;
import org.csstudio.swt.chart.axes.XAxis;
import org.csstudio.swt.chart.axes.YAxis;
import org.csstudio.swt.chart.axes.YAxisListener;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/** SWT app for testing the chart.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ChartTest
{
    /** Flags for chart */
    private static final int chart_flags = Chart.USE_TRACE_NAMES
                                            | Chart.TIME_CHART;
    
    /** Demo scrolling? */
    private static final boolean scroll = true;

    /** Millisec period of scroll */
    private static final int SCROLL_MS = 100;

    private Chart chart;
    private ListViewer list_viewer;

    final private ArrayList<ChartSampleSequenceDemo> trace_data =
        new ArrayList<ChartSampleSequenceDemo>();

    final private Runnable sample_adder = new Runnable()
    {
        public void run()
        {
            if (chart.isDisposed())
                return;
            final Display display = chart.getDisplay();
            
            // Add a sample to each trace
            double start = Double.MAX_VALUE;
            double end = 0.0;
            synchronized (trace_data)
            {
                for (ChartSampleSequenceDemo seq : trace_data)
                {
                    seq.add();
                    final double first_x = seq.get(0).getX();
                    if (start > first_x)
                        start = first_x;
                    final double last_x = seq.get(seq.size()-1).getX();
                    if (end < last_x)
                        end = last_x;
                }
            }
            
            // Trigger redraw
            if (scroll)
            {
                // Scroll so that the last 60 seconds roll by
                if (end - start > 60.0)
                    start = end - 60.0;
                chart.getXAxis().setValueRange(start, end);
            }
            else
                chart.redrawTraces();
            
            // Schedule next update
            display.timerExec(SCROLL_MS, sample_adder);
        }
    };

    private void run()
    {
        Display display = new Display();
        Shell shell = new Shell(display); // Compare: JFrame
        shell.setBounds(100, 100, 800, 600);
        makeGUI(shell);
        
        // Add initial demo data
        addDemoTrace("y", chart.getYAxis(0));
       
        // List is a drag source
        new ProcessVariableDragSource(list_viewer.getList(), list_viewer);
        // Listener Demo   
        chart.addListener(new ChartListener()
        {
            public void aboutToZoomOrPan(final String description)
            {
                System.out.println("ChartTest ChartListener: aboutToZoomOrPan "
                        + description);
            }

            public void changedXAxis(final XAxis xaxis)
            {
                System.out.println("ChartTest ChartListener: XAxis changed");
            }

            public void changedYAxis(final YAxisListener.Aspect what, final YAxis yaxis)
            {
                System.out.println("ChartTest ChartListener: " + yaxis
                                + " has new " + what);
            }

            public void pointSelected(final int x, final int y)
            {
                System.out.println("ChartTest ChartListener: Point " +
                                x + ", " + y + " Selected");
            }
        });
        
        // Allow PV drops into the chart
        new ProcessVariableDropTarget(chart)
        {
            @Override
            public void handleDrop(IProcessVariable name, DropTargetEvent event)
            {
                YAxis yaxis = chart.getYAxisAtScreenPoint(event.x, event.y);
                if (yaxis == null)
                    yaxis = addDemoTrace(name.getName(), null);
                else
                {
                    // Does the chart automatically use the trace labels?
                    String label;
                    if ((chart_flags & Chart.USE_TRACE_NAMES) == 0)
                        label = yaxis.getLabel() + ", " + name.getName();
                    else
                        label = name.getName();
                    addDemoTrace(label, yaxis);
                }
            }
        };

        shell.open();
        
        display.timerExec(SCROLL_MS, sample_adder);
        
        // Message loop left to the application
        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        display.dispose(); // !
    }

    private void makeGUI(Shell shell)
    {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        shell.setLayout(layout);
        GridData gd;
    
        // ------ Title ---------------
        // | Process Variables | Plot |
        // | PV List ......... | .... |
        // ----------------------------
        Label l = new Label(shell, SWT.CENTER);
        l.setText("Plot Test");
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        l.setLayoutData(gd);
        
        l = new Label(shell, SWT.LEFT);
        l.setText("Process Variables");
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        l.setLayoutData(gd);

        gd = new GridData();
        gd.verticalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        // Raw chart or interactive one?
        if (false)
        {
            chart = new Chart(shell, chart_flags);
            chart.setLayoutData(gd);
        }
        else
        {
            InteractiveChart ichart = new InteractiveChart(shell, chart_flags);
            chart = ichart.getChart();
            ichart.setLayoutData(gd);
            
            new ToolBarActionHook(ichart, new ShowButtonBarAction(ichart));
            new ToolBarActionHook(ichart, new SaveCurrentImageAction(chart));
            new ToolBarActionHook(ichart, new PrintCurrentImageAction(chart));
        }
        chart.getXAxis().setLabel("The X Axis");
        if ((chart_flags & Chart.TIME_CHART) != 0)
        {
            double high = TimestampFactory.now().toDouble() + 60;
            double low = high - 120;
            chart.getXAxis().setValueRange(low, high);
        }
        // LOG TEST?
        chart.getYAxis(0).setLogarithmic(false);
        chart.getYAxis(0).setValueRange(1, 100);
        List list = new List(shell, SWT.SINGLE);
        list_viewer = new ListViewer(list);
        list_viewer.setLabelProvider(new LabelProvider());
        list_viewer.setContentProvider(new ArrayContentProvider());
        Vector<IProcessVariable> names = new Vector<IProcessVariable>();
        names.add(CentralItemFactory.createProcessVariable("fred"));
        names.add(CentralItemFactory.createProcessVariable("freddy"));
        names.add(CentralItemFactory.createProcessVariable("jane"));
        names.add(CentralItemFactory.createProcessVariable("janet"));
        list_viewer.setInput(names);
        gd = new GridData();
        gd.minimumWidth = 300;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        list.setLayoutData(gd);
    }

    /** Add another trace of demo data.
     * 
     *  @param name New or modified title
     *  @param yaxis Axis to use or <code>null</code> for new axis to create.
     *  @return
     */
    private YAxis addDemoTrace(final String name, YAxis yaxis)
    {
        // Create new sample sequence
        final double phase = 2.0 * Math.PI * Math.random();
        final double x0 = ((chart_flags & Chart.TIME_CHART) != 0)
                        ? TimestampFactory.now().toDouble()
                        : 0.0;
        final int N = 100;
        final ChartSampleSequenceDemo seq = new ChartSampleSequenceDemo(x0, phase, N);
        for (int i=0; i<=N; ++i)
        {
            seq.add();
        }
        // Create new trace, using new or given Y axis
        if (yaxis == null)
            yaxis = chart.addYAxis(name);
        else
            yaxis.setLabel(name);
        final int axis_index = chart.getYAxisIndex(yaxis);
        // Auto-scale every other axis
        if ((axis_index % 2) == 1)
            yaxis.setAutoScale(true);
        // Hide every 3rd axis
        if ((axis_index % 3) == 1)
            yaxis.setVisible(false);
        final int next = chart.getNumTraces();
        Color color = new Color(null, DefaultColors.getRed(next),
                                DefaultColors.getGreen(next),
                                DefaultColors.getBlue(next));
        // Add to chart
        chart.addTrace(name, seq, color, 0, axis_index,
                       TraceType.Area);
        synchronized (trace_data)
        {
            trace_data.add(seq);
        }
        chart.stagger();
        return yaxis;
    }

    public static void main(String[] args)
    {
        ChartTest app = new ChartTest();
        app.run();
    }
}
