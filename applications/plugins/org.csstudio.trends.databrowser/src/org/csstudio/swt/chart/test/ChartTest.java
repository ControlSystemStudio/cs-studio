package org.csstudio.swt.chart.test;

import java.util.Vector;

import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDropTarget;
import org.csstudio.platform.util.TimestampFactory;
import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.ChartListener;
import org.csstudio.swt.chart.ChartSample;
import org.csstudio.swt.chart.ChartSampleSequenceContainer;
import org.csstudio.swt.chart.InteractiveChart;
import org.csstudio.swt.chart.TraceType;
import org.csstudio.swt.chart.axes.XAxis;
import org.csstudio.swt.chart.axes.YAxis;
import org.csstudio.swt.chart.axes.YAxisListener;
import org.csstudio.util.swt.DefaultColors;
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
    private static final int chart_flags =
        Chart.USE_TRACE_NAMES
   | Chart.TIME_CHART
        ;
    //private static final int chart_flags = 0;

    private Chart chart;
    private ListViewer list_viewer;

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
            public void changedXAxis(XAxis xaxis)
            {
                System.out.println("ChartTest ChartListener: XAxis changed");
            }

            public void changedYAxis(YAxisListener.Aspect what, YAxis yaxis)
            {
                System.out.println("ChartTest ChartListener: " + yaxis
                                + " has new " + what);
            }

            public void pointSelected(XAxis xaxis, YAxis yaxis, double x, double y)
            {
                System.out.println("ChartTest ChartListener: Point " +
                                x + ", " + y + " Selected");
            }
            
            public void nothingSelected()
            {
                System.out.println("ChartTest ChartListener: Nothing Selected");
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
    private YAxis addDemoTrace(String name, YAxis yaxis)
    {
        // Create new sample sequence
        ChartSampleSequenceContainer seq = new ChartSampleSequenceContainer();
        int i;
        int N = 100;
        double twopi = 2.0 * Math.PI;
        double phase = twopi * Math.random();
        double shift = 100 + 10.0*Math.random();
        double x0 = TimestampFactory.now().toDouble();
        for (i=0; i<=N; ++i)
        {
            ChartSample.Type type;
            if (Math.random() > 0.95  ||  i==N)
                type = ChartSample.Type.Point;
            else
                type = ChartSample.Type.Normal;
            double x;
            if ((chart_flags & Chart.TIME_CHART) != 0)
                x = x0 + 10*i;
            else
                x = 0.1*i;
            
            double y = shift +
                       50*(0.1*Math.random() + Math.sin(twopi*0.01*i + phase));
            String info = null;
            if (type == ChartSample.Type.Point)
            {
                info = "Comment";
                y = Double.NEGATIVE_INFINITY;
            }
            seq.add(type, x, y, info);
        }
        // Create new trace, using new or given Y axis
        if (yaxis == null)
            yaxis = chart.addYAxis(name);
        else
            yaxis.setLabel(name);
        final int axis_index = chart.getYAxisIndex(yaxis);
        if ((axis_index % 2) == 1)
            yaxis.setAutoZoom(true);
        i = chart.getNumTraces();
        Color color = new Color(null, DefaultColors.getRed(i),
                                DefaultColors.getGreen(i),
                                DefaultColors.getBlue(i));
        // Add to chart
        chart.addTrace(name, seq, color, 0, axis_index,
                       TraceType.Lines);
        return yaxis;
    }

    public static void main(String[] args)
    {
        ChartTest app = new ChartTest();
        app.run();
    }
}
