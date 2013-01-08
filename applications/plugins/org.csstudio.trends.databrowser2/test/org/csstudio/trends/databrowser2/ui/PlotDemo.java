/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.ui;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.swt.xygraph.figures.Annotation;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.PlotSample;
import org.csstudio.trends.databrowser2.model.PlotSampleArray;
import org.csstudio.trends.databrowser2.model.PlotSamples;
import org.csstudio.trends.databrowser2.model.TestHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.junit.Test;

/** (Headless) JUnit Plug-in demo of Plot
 *
 *  Simply displays the plot. Static data, no controller.
 *
 *  Must run as plug-in test to load XY Graph icons etc.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PlotDemo
{
    private boolean run = true;
    private volatile boolean scroll = true;
    private Timestamp start_time, end_time;

    final private PlotListener listener = new PlotListener()
    {
        @Override
        public void scrollRequested(final boolean enable_scrolling)
        {
        	System.out.println("Scroll enabled: " + enable_scrolling);
        	scroll = enable_scrolling;
        }

        @Override
        public void timeConfigRequested()
        {
        	System.out.println("Time Config requested");
        }

        @Override
        public void timeAxisChanged(final long start_ms, final long end_ms)
        {
        	start_time = TimestampHelper.fromMillisecs(start_ms);
        	end_time = TimestampHelper.fromMillisecs(end_ms);
        	System.out.println("Time axis: " + start_time + " ... " + end_time);
        }

        @Override
        public void valueAxisChanged(final int index, final double lower, final double upper)
        {
            System.out.println("Value axis " + index + ": " + lower + " ... " + upper);
        }

        @Override
        public void droppedName(final String name)
        {
            System.out.println("Name dropped: " + name);
        }

        @Override
        public void droppedPVName(final ProcessVariable name, final ArchiveDataSource archive)
        {
            System.out.println("PV Name dropped: " + name);
        }

        @Override
        public void droppedFilename(final String file_name)
        {
            System.out.println("File Name dropped: " + file_name);
        }

        @Override
		public void xyGraphConfigChanged(XYGraph newValue) {
			// TODO Auto-generated method stub

		}

		@Override
		public void removeAnnotationChanged(Annotation oldValue) {
			// TODO Auto-generated method stub

		}

		@Override
		public void addAnnotationChanged(Annotation newValue) {
			// TODO Auto-generated method stub

		}

		@Override
		public void backgroundColorChanged(Color newValue) {
			// TODO Auto-generated method stub

		}

		@Override
		public void timeAxisForegroundColorChanged(Color oldColor,
				Color newColor) {
			// TODO Auto-generated method stub

		}

		@Override
		public void valueAxisForegroundColorChanged(int index, Color oldColor,
				Color newColor) {
			// TODO Auto-generated method stub

		}

		@Override
		public void valueAxisTitleChanged(int index, String oldTitle,
				String newTitle) {
			// TODO Auto-generated method stub

		}

		@Override
		public void valueAxisAutoScaleChanged(int index, boolean oldAutoScale,
				boolean newAutoScale) {
			// TODO Auto-generated method stub

		}

		@Override
		public void traceNameChanged(int index, String oldName, String newName) {
			// TODO Auto-generated method stub

		}

		@Override
		public void traceYAxisChanged(int index, AxisConfig oldConfig,
				AxisConfig config) {
			// TODO Auto-generated method stub

		}

		@Override
		public void traceTypeChanged(int index, TraceType old,
				TraceType newTraceType) {
			// TODO Auto-generated method stub

		}

		@Override
		public void traceColorChanged(int index, Color old, Color newColor) {
			// TODO Auto-generated method stub

		}

		@Override
		public void valueAxisLogScaleChanged(int index, boolean old,
				boolean logScale) {
			// TODO Auto-generated method stub

		}
    };

    private void createGUI(final Composite parent)
    {
    	final Display display = parent.getDisplay();
        final GridLayout layout = new GridLayout(1, false);
        parent.setLayout(layout);

        // Canvas that holds the graph
        final Canvas plot_box = new Canvas(parent, 0);
        plot_box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));

        // [Done] button to end demo
        final Button ok = new Button(parent, SWT.PUSH);
        ok.setText("Done");
        ok.setLayoutData(new GridData(SWT.RIGHT, 0, true, false));
        ok.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                run = false;
            }
        });

        final Plot plot = Plot.forCanvas(plot_box);
        plot.addListener(listener);

        // Create demo samples
        final List<PlotSample> values = new ArrayList<PlotSample>();
        for (int i=1; i<10; ++i)
            values.add(new PlotSample("Test", TestHelper.makeValue(i)));
        values.add(new PlotSample("Test", TestHelper.makeError(15, "Disconnected")));
        // Single value. Line should continue until the following 'disconnect'.
        values.add(new PlotSample("Test", TestHelper.makeValue(17)));
        values.add(new PlotSample("Test", TestHelper.makeError(18, "Disconnected")));

        for (int i=20; i<30; ++i)
            values.add(new PlotSample("Test", TestHelper.makeValue(i)));

        final PlotSampleArray samples = new PlotSampleArray();
        samples.set(values);

        // Add item with demo samples
        final ModelItem item = new ModelItem("Demo")
        {
            @Override
            public PlotSamples getSamples()
            {
                return samples;
            }

            @Override
            public void write(final PrintWriter writer)
            {
                // NOP
            }
        };
        item.setColor(new RGB(0, 0, 255));
        plot.addTrace(item);

        start_time = VTypeHelper.getTimestamp(samples.getSample(0).getValue());
        end_time = VTypeHelper.getTimestamp(samples.getSample(samples.getSize()-1).getValue());
        plot.setTimeRange(start_time, end_time);

        new Thread(new Runnable()
        {
			@Override
            public void run()
            {
				while (true)
				{
					try
					{
						Thread.sleep(1000);
					}
					catch (Exception ex)
					{
						return;
					}
					if (scroll)
					{
						start_time = start_time.plus(TimeDuration.ofSeconds(1));
						end_time = end_time.plus(TimeDuration.ofSeconds(1));
						display.syncExec(new Runnable()
						{
							@Override
		                    public void run()
		                    {
						        plot.setTimeRange(start_time, end_time);
		                    }
						});
					}
				}
            }
        }, "Scroller").start();
    }

    @Test
    public void plotDemo()
    {
        final Shell shell = new Shell();
        shell.setSize(600, 500);

        createGUI(shell);
        shell.open();

        final Display display = Display.getDefault();
        while (run  &&  !shell.isDisposed())
        {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }
}
