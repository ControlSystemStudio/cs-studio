/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.ui;

import java.util.List;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.swt.xygraph.figures.Annotation;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.swt.xygraph.util.XYGraphMediaFactory;
import org.csstudio.trends.sscan.model.AnnotationInfo;
import org.csstudio.trends.sscan.model.AxesConfig;
import org.csstudio.trends.sscan.model.AxisConfig;
import org.csstudio.trends.sscan.model.Model;
import org.csstudio.trends.sscan.model.ModelItem;
import org.csstudio.trends.sscan.model.ModelListener;
import org.csstudio.trends.sscan.model.Sscan;
import org.csstudio.trends.sscan.propsheet.AddAxesCommand;
import org.csstudio.trends.sscan.scancontrol.SscanListener;
import org.csstudio.trends.sscan.scancontrol.SscanView;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** Controller that interfaces the {@link Model} with the {@link Plot}:
 *  <ul>
 *  <li>For each item in the Model, create a trace in the plot.
 *  <li>Perform scrolling of the time axis.
 *  <li>When the plot is interactively zoomed, update the Model's time range.
 *  <li>Get archived data whenever the time axis changes.
 *  </ul>
 *  @author Kay Kasemir
 */
public class Controller
{
    /** Optional shell used to track shell state */
    final private Shell shell;

    /** Display used for dialog boxes etc. */
    final private Display display;

    /** Model with data to display */
    final private Model model;
    
    final private ViewPart view;

    /** Listener to model that informs this controller */
	private ModelListener model_listener;
	
	private SscanListener sscan_listener;
	
	private int previous = 0;

    /** GUI for displaying the data */
    final private Plot plot;

    /** Is the window (shell) iconized? */
    private volatile boolean window_is_iconized = false;

    /** Should we perform redraws, or is the window hidden and we should suppress them? */
    private boolean suppress_redraws = false;

    /** Is there any Y axis that's auto-scaled? */
    private volatile boolean have_autoscale_axis = false;


    /** Initialize
     *  @param shell Shell
     *  @param model Model that has the data
     *  @param plot Plot for displaying the Model
     *  @throws Error when called from non-UI thread
     */
    public Controller(final Shell shell, final Model model, final Plot plot, final ViewPart view)
    {
        this.shell = shell;
        this.model = model;
        this.plot = plot;
        this.view = view;

        if (shell == null)
        {
            display = Display.getCurrent();
            if (display == null)
                throw new Error("Must be called from UI thread"); //$NON-NLS-1$
        }
        else
        {
            display = shell.getDisplay();
            // Update 'iconized' state from shell
            shell.addShellListener(new ShellListener()
            {
                @Override
                public void shellIconified(ShellEvent e)
                {
                    window_is_iconized = true;
                }

                @Override
                public void shellDeiconified(ShellEvent e)
                {
                    window_is_iconized = false;
                }

                @Override
                public void shellDeactivated(ShellEvent e) { /* Ignore */  }
                @Override
                public void shellClosed(ShellEvent e)      { /* Ignore */  }
                @Override
                public void shellActivated(ShellEvent e)   { /* Ignore */  }
            });
            window_is_iconized = shell.getMinimized();
        }
        checkAutoscaleAxes();
        createPlotTraces();
        createAnnotations();
        createXYGraphSettings(); //ADD LAURENT PHILIPPE
		
        // Listen to user input from Plot UI, update model
        plot.addListener(new PlotListener()
        {

            @Override
            public void xAxisChanged(final int index, final double min, final double max)
            {
            	final AxesConfig axes = model.getAxes(index);
            	AxisConfig xAxis = axes.getXAxis();
            	xAxis.setRange(min, max);
                // Controller's ModelListener will fetch new archived data
            }

            @Override
            public void yAxisChanged(final int index, final double min, final double max)
            {   // Update axis range in model
            	final AxesConfig axes = model.getAxes(index);
            	AxisConfig yAxis = axes.getYAxis();
            	yAxis.setRange(min, max);
            }

            @Override
            public void droppedName(final String name)
            {
                // Offer potential PV name in dialog so user can edit/cancel
                final AddPVAction add = new AddPVAction(plot.getOperationsManager(), shell, model, false);
                // Allow passing in many names, assuming that white space separates them
                final String[] names = name.split("[\\r\\n\\t ]+"); //$NON-NLS-1$
                for (String n : names)
                    if (! add.runWithSuggestedName(n))
                        break;
            }

            @Override
            public void droppedPVName(final ProcessVariable name)
            {
                if (name == null)
                {
                    for (int i=0; i<model.getItemCount(); ++i)
                    {
                        if (! (model.getItem(i) instanceof ModelItem))
                            continue;
                        final ModelItem pv = (ModelItem) model.getItem(i);
                    }
                }
                else
                {   // Received PV name
                	
                	// Add the given PV to the model anyway even if the same PV
                	// exists in the model.
                    final OperationsManager operations_manager = plot.getOperationsManager();

                    // Add to first empty axis, or create new axis
                    Sscan sscan = model.addSscan(name.getName());
                    AxesConfig axes = model.getEmptyAxes();
                    if (axes == null)
                        axes = new AddAxesCommand(operations_manager, model).getAxes();

                    // Add new PV
                    AddModelItemCommand.forPV(shell, operations_manager,
                            model, name.getName(),
                            axes,sscan);
                    return;
                }
            }

			@Override
			public void xyGraphConfigChanged(XYGraph newValue) {
				// TODO Auto-generated method stub
				model.fireGraphConfigChanged();
				//model.setXYGraphMem(newValue);
			}

			@Override
			public void removeAnnotationChanged(Annotation oldValue) {
				model.setAnnotations(plot.getAnnotations());
			}

			@Override
			public void addAnnotationChanged(Annotation newValue) {
				model.setAnnotations(plot.getAnnotations());
			}

			@Override
			public void backgroundColorChanged(Color newValue) {
				System.out
						.println("**** Controller.Controller(...).new PlotListener() {...}.backgroundColorChanged() ****");
				model.setPlotBackground(newValue.getRGB());		
			}  
			
			
			@Override
			public void xAxisForegroundColorChanged(Color oldColor,
					Color newColor) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void yAxisForegroundColorChanged(int index,
					Color oldColor, Color newColor) {
				//System.err.println("valueAxis color changed");
				final AxesConfig axes = model.getAxes(index);
				AxisConfig yAxis = axes.getYAxis();
				yAxis.setColor(newColor.getRGB());
			}

			@Override
			public void yAxisTitleChanged(int index, String oldTitle,
					String newTitle) {
				//System.err.println("valueAxis title changed");
				final AxesConfig axes = model.getAxes(index);
				AxisConfig yAxis = axes.getYAxis();
	            yAxis.setName(newTitle);
				
			}

			@Override
			public void yAxisAutoScaleChanged(int index,
					boolean oldAutoScale, boolean newAutoScale) {
				final AxesConfig axes = model.getAxes(index);
				AxisConfig yAxis = axes.getYAxis();
	            yAxis.setAutoScale(newAutoScale);
				
			}
			
			@Override
			public void traceNameChanged(int index, String oldName,
					String newName) {
			
				//System.err.print("TRACE NEW NAME ");
				try {
					System.err.println(model.getItem(index).getName() + " " + model.getItem(index).getDisplayName() );
					model.getItem(index).setDisplayName(newName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void traceYAxisChanged(int index, AxisConfig oldAxis, AxisConfig newAxis) {
				//System.out
					//	.println("**** Controller.Controller(...).new PlotListener() {...}.traceYAxisChanged() ****");
				ModelItem item = model.getItem(index);
				System.out.println("AXIS OLD NAME  " + oldAxis.getName() + " NEW NAME " + newAxis.getName());
				AxesConfig c = model.getAxes(newAxis.getName());
				AxisConfig yAxis = c.getYAxis();
				System.out.println("AXIS CONFIG " + yAxis.getName());
				item.setAxes(c);
			}

			@Override
			public void traceTypeChanged(int index, TraceType old,
					TraceType newTraceType) {
				
				//DO NOTHING
				//The model trace type is not the same concept that graph settings traceType
				//The model trace type gather TraceType, PointStyle, ErrorBar graph config settings
				
				//ModelItem item = model.getItem(index);
				//item.setTraceType(org.csstudio.trends.databrowser2.model.TraceType.newTraceType);
			}

			@Override
			public void traceColorChanged(int index, Color old, Color newColor) {
				
				ModelItem item = model.getItem(index);
				item.setColor(newColor.getRGB());
			}

			@Override
			public void yAxisLogScaleChanged(int index, boolean old,
					boolean logScale) {
				
				final AxesConfig axes = model.getAxes(index);
				AxisConfig yAxis = axes.getYAxis();
				yAxis.setLogScale(logScale);
			}

			@Override
			public void xAxisTitleChanged(int index, String oldTitle,
					String newTitle) {
				//System.err.println("valueAxis title changed");
				final AxesConfig axes = model.getAxes(index);
				AxisConfig xAxis = axes.getXAxis();
	            xAxis.setName(newTitle);
				
			}

			@Override
			public void xAxisAutoScaleChanged(int index, boolean oldAutoScale,
					boolean newAutoScale) {
				
				final AxesConfig axes = model.getAxes(index);
				AxisConfig xAxis = axes.getXAxis();
	            xAxis.setAutoScale(newAutoScale);
				
			}

			@Override
			public void traceXAxisChanged(int index, AxisConfig oldAxis,
					AxisConfig newAxis) {
				
				ModelItem item = model.getItem(index);
				System.out.println("AXIS OLD NAME  " + oldAxis.getName() + " NEW NAME " + newAxis.getName());
				AxesConfig c = model.getAxes(newAxis.getName());
				AxisConfig xAxis = c.getXAxis();
				System.out.println("AXIS CONFIG " + xAxis.getName());
				item.setAxes(c);
				
			}

			@Override
			public void xAxisLogScaleChanged(int index, boolean old,
					boolean logScale) {
				final AxesConfig axes = model.getAxes(index);
				AxisConfig xAxis = axes.getXAxis();
				xAxis.setLogScale(logScale);
				
			}

			@Override
			public void xAxisForegroundColorChanged(int index, Color oldColor,
					Color newColor) {
				final AxesConfig axes = model.getAxes(index);
				AxisConfig xAxis = axes.getXAxis();
				xAxis.setColor(newColor.getRGB());
				
			}

        });

        model_listener = new ModelListener()
        {

            @Override
            public void changedColors()
            {
                plot.setBackgroundColor(model.getPlotBackground());
            }

            @Override
            public void itemAdded(final ModelItem item)
            {
                if (item.isVisible())
                    plot.addTrace(item,item.getModel().indexOf(item));

            }

            @Override
            public void itemRemoved(final ModelItem item)
            {
                if (item.isVisible())
                    plot.removeTrace(item);
            }

            @Override
            public void changedItemVisibility(final ModelItem item)
            {   // Add/remove from plot, but don't need to get archived data
                if (item.isVisible())
                    // itemAdded(item) would also get archived data
                    plot.addTrace(item);
                else
                    plot.removeTrace(item);
            }

            @Override
            public void changedItemLook(final ModelItem item)
            {
                plot.updateTrace(item);
            }
            
            @Override
            public void changedItemData(final ModelItem item)
            {
            	item.getLiveSamples();
            	plot.redrawTraces();
            }

            @Override
            public void changedItemDataConfig(final ModelItem item)
            {

            }      
		
            /**
             * ADD L.PHILIPPE
             */
			@Override
			public void changedAnnotations() {
				// TODO Auto-generated method stub
				
			}

		    /**
             * ADD L.PHILIPPE
             */
			@Override
			public void changedXYGraphConfig() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void changedAxis(AxisConfig axis) {
				 checkAutoscaleAxes();
	                if (axis == null)
	                {
	                    // New or removed axis: Recreate the whole plot
	                    createPlotTraces();
	                    return;
	                }
	                // Else: Update specific axes
	                for (int i=0; i<model.getAxesCount(); ++i)
	                {
	                    if (model.getAxes(i).getXAxis() == axis)
	                    {
	                        plot.updateXAxis(i, axis);
	                        return;
	                    }
	                    if (model.getAxes(i).getYAxis() == axis)
	                    {
	                        plot.updateYAxis(i, axis);
	                        return;
	                    }
	                }
				
			}
			
        };
        model.addListener(model_listener);
        
        sscan_listener = new SscanListener()
        {
        	@Override
			public void ScanEvent(String name, int status) {
        		if(status == 1 && previous == 0){
        			previous = 1;
        			final OperationsManager operations_manager = plot.getOperationsManager();

        			// Add to first empty axis, or create new axis
        			Sscan sscan = model.addSscan(name);
        			AxesConfig axes = model.getAxes(0);
        			if (axes == null)
        				axes = new AddAxesCommand(operations_manager, model).getAxes();
        			
        			
        			// Add new PV
        			AddModelItemCommand.forPV(shell, operations_manager,
                        model, name,
                        axes, sscan);
        			return;
        		}
        		if(status == 0 && previous == 1){
        			previous = 0;
        			stop();
        		}
				
			}
        	
        	@Override
        	public void PVName(String name) {
        		
    		
        	}

			@Override
			public void PVName(ProcessVariable name) {
				
			}
    	};
        
        ((SscanView)view).getControlComposite().addListener(sscan_listener);
    }

    /** @param suppress_redraws <code>true</code> if controller should suppress
     *        redraws because window is hidden
     */
    public void suppressRedraws(final boolean suppress_redraws)
    {
        if (this.suppress_redraws == suppress_redraws)
            return;
        this.suppress_redraws = suppress_redraws;
        if (!suppress_redraws)
            plot.redrawTraces();
    }

    /** Check if there's any axis in 'auto scale' mode.
     *  @see #have_autoscale_axis
     */
    private void checkAutoscaleAxes()
    {
        have_autoscale_axis = false;
        for (int i=0;  i<model.getAxesCount(); ++i)
            if (model.getAxes(i).getYAxis().isAutoScale()||model.getAxes(i).getXAxis().isAutoScale())
            {
                have_autoscale_axis = true;
                break;
            }
    }

    /** Stop scrolling and model items
     *  @throws IllegalStateException when not running
     */
    public void stop()
    {
        // Stop update task
        model.stop();
    }

    /** (Re-) create traces in plot for each item in the model */
    public void createPlotTraces()
    {
        plot.setBackgroundColor(model.getPlotBackground());
        plot.removeAll();
     
        for (int i=0; i<model.getAxesCount(); ++i) {
            plot.updateYAxis(i, model.getAxes(i).getYAxis());
        	plot.updateXAxis(i, model.getAxes(i).getXAxis());
        }
        for (int i=0; i<model.getItemCount(); ++i)
        {
            final ModelItem item = model.getItem(i);
            
            if (item.isVisible()){
            	//System.out.println("Controller.createPlotTraces() INDEX " + i + " => " + model.getItem(i).getDisplayName());
                plot.addTrace(item, i);
            }
        }
    }

    /** Add annotations from model to plot */
    private void createAnnotations()
    {
		final XYGraph graph = plot.getXYGraph();
    	final List<Axis> yaxes = graph.getYAxisList();
    	final AnnotationInfo[] annotations = model.getAnnotations();
        for (AnnotationInfo info : annotations)
        {
			final int axis_index = info.getAxis();
			if (axis_index < 0  ||  axis_index >= yaxes.size())
				continue;
			final Axis axis = yaxes.get(axis_index);
        	final Annotation annotation = new Annotation(info.getTitle(), graph.primaryXAxis, axis);
        	annotation.setValues(info.getTimestamp().toDouble() * 1000.0,
        			info.getValue());
			
        	//ADD Laurent PHILIPPE
			annotation.setCursorLineStyle(info.getCursorLineStyle());
        	annotation.setShowName(info.isShowName());
        	annotation.setShowPosition(info.isShowPosition());
        	
        	if(info.getColor() != null)
        		annotation.setAnnotationColor(XYGraphMediaFactory.getInstance().getColor(info.getColor()));
        	
        	if(info.getFontData() != null)
       			annotation.setAnnotationFont(XYGraphMediaFactory.getInstance().getFont(info.getFontData()));
        	
        	graph.addAnnotation(annotation);
        }
    }
    
    
    /**
     * Add XYGraphMemento (Graph config settings from model to plot)
     */
    private void createXYGraphSettings() {
     	plot.setGraphSettings(model.getGraphSettings());
 	}

}
