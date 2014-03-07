package org.csstudio.trends.databrowser2.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.dataprovider.ISample;
import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.IAxisListener;
import org.csstudio.swt.xygraph.figures.Trace;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.swt.graphics.Color;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.PVWriterEvent;
import org.epics.pvmanager.PVWriterListener;
import org.epics.pvmanager.formula.ExpressionLanguage;
import org.epics.util.array.ArrayDouble;
import org.epics.vtype.VTable;
import org.epics.vtype.ValueFactory;

/**
 * 
 * <code>SelectionValueExporter</code> listens to the mouse motion events on the plot
 * and updates the selection value PV with values of all traces in the plot at the current
 * mouse location. The exporter updates on every mouse move event.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SelectionValueExporter extends MouseMotionListener.Stub implements IAxisListener {

	private static final String WRITER_FAILED_MSG = "Selection value writing failed.";
	private static final List<String> VTABLE_NAMES = Arrays.asList("Trace", "X", "Y");
	private static final List<String> VTABLE_NAMES_TIME = Arrays.asList("Trace", "Timestamp", "Value");
	private static final List<Class<?>> VTABLE_CLASSES_DOUBLE = Arrays.<Class<?>> asList(
			String.class, double.class, double.class);
	private static final List<Class<?>> VTABLE_CLASSES_TIME = Arrays.<Class<?>> asList(
			String.class, String.class, double.class);
	
	private String selectionValuePv;
	private PVWriter<Object> selectionValueWriter;
	private boolean useTimeFormat = false;
	private int cursorX;
	private VTable vTable;

	private XYGraph graph;
	
	private PropertyChangeSupport support = new PropertyChangeSupport(this);
	
	/**
	 * Constructs a new SelectionValueExported that attaches itself to the provided plot.
	 * 
	 * @param plot the plot to attach this exporter to
	 */
	public SelectionValueExporter(XYGraph graph) {
		setGraph(graph);
	}
	
	/**
	 * Sets the graph, which is used as a source of data for this exporter.
	 * The exporter will listen to the mouse motion events on the plot to get the
	 * location of the mouse cursor. Based on the location it will extract the
	 * values of all traces in the graph at that location and set the value to the 
	 * selection PV.
	 * 
	 * @param graph the graph to attach to
	 */
	private void setGraph(XYGraph graph) {
		if (this.graph != null) {
			this.graph.getPlotArea().removeMouseMotionListener(this);
			for (Axis a : this.graph.getXAxisList()) {
				a.removeListener(this);
			}
			
		}
		this.graph = graph;
		if (this.graph != null) {
			this.graph.getPlotArea().addMouseMotionListener(this);
			for (Axis a : this.graph.getXAxisList()) {
				a.addListener(this);
			}
		}
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.draw2d.MouseMotionListener.Stub#mouseMoved(org.eclipse.draw2d.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent me) {
		cursorX = me.getLocation().x;
		createAndSendVTable();
	}
	
	private void createAndSendVTable() {
		VTable oldValue = this.vTable;
		vTable = calculateVTable();
		writeSelectedValue(vTable);
		support.firePropertyChange("vTable",oldValue,vTable);
	}
	
	/**
	 * @return the latest v table
	 */
	public VTable getVTable() {
		return vTable;
	}
	
	/**
	 * Constructs and returns the VTable.
	 * 
	 * @return the VTable
	 */
	private VTable calculateVTable() {
		List<Trace> traceList = graph.getPlotArea().getTraceList();
		double[] x = new double[traceList.size()];
		double[] y = new double[x.length];
		ArrayList<String> names = new ArrayList<String>(x.length);
		Trace trace;
		for (int j = 0; j < x.length; j++) {
			trace = traceList.get(j);
			if (!trace.isVisible())	continue;
			
			final Axis xAxis = trace.getXAxis();
			final double xValue = xAxis.getPositionValue(cursorX, false);
			ISample previousSample = null;
			double previousSampleXValue = 0;
			for (ISample sample : trace.getHotSampleList()) {
				if (sample.getXValue() > xValue) {
					break;
				} else if (previousSampleXValue < sample.getXValue()) {
					previousSampleXValue = sample.getXValue();
					previousSample = sample;
				}
			}

			if (previousSample == null) {
				// Looking for the nearest sample before the plot start
				IDataProvider dp = trace.getDataProvider();
				previousSampleXValue = 0;
				for (int i = dp.getSize() - 1; i >= 0; i--) {
					ISample sample = dp.getSample(i);
					if (sample.getXValue() > previousSampleXValue
							&& sample.getXValue() <= xAxis.getRange()
									.getLower()) {
						previousSampleXValue = sample.getXValue();
						previousSample = sample;
					}
					if (dp.isChronological()
							&& sample.getXValue() < xAxis.getRange().getLower()) {
						break;
					}
				}
			}
			if (previousSample != null) {
				x[j] = previousSample.getXValue();
				y[j] = previousSample.getYValue();
			} else {
				x[j] = Double.NaN;
				y[j] = Double.NaN;
			}
			names.add(trace.getName());
		}
		
		if (useTimeFormat) {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS yyyy-MM-dd");
			List<String> times = new ArrayList<String>();
			for (double d : x) {
				times.add(format.format(new Date((long)d)));
			}
			return ValueFactory.newVTable(
					VTABLE_CLASSES_TIME,VTABLE_NAMES_TIME,	
					Arrays.<Object>asList(names,times, new ArrayDouble(y)));
		} else {
			return ValueFactory.newVTable(
					VTABLE_CLASSES_DOUBLE,VTABLE_NAMES,	
					Arrays.<Object>asList(names,new ArrayDouble(x), new ArrayDouble(y)));
		}		
	}
	
	/**
	 * Creates the selection value writer and writes the value
	 * 
	 * @param selection the value to write
	 */
	private void writeSelectedValue(VTable selection) {
		if (selectionValuePv == null || selectionValuePv.trim().isEmpty())
			return;
		if (selectionValueWriter != null) {
			selectionValueWriter.close();
			selectionValueWriter = null;
		}
		selectionValueWriter = PVManager
				.write(ExpressionLanguage.formula(selectionValuePv))
				.writeListener(new PVWriterListener<Object>() {

					public void pvChanged(PVWriterEvent<Object> event) {
						if (event.isWriteFailed()) {
							Logger.getLogger(SelectionValueExporter.class.getName()).log(
									Level.WARNING, WRITER_FAILED_MSG,
									event.getPvWriter().lastWriteException());
						}
					}
				}).async();

		if (selection != null) {
			selectionValueWriter.write(selection);
		}
	}

	/**
	 * Sets selection value PV.
	 * 
	 * @param selectionValuePv selection value PV name
	 */
	public void setSelectionValuePv(String selectionValuePv) {
		this.selectionValuePv = selectionValuePv;
	}
	
	/**
	 * Sets the flag to use the time format or the normal number format for the x value.
	 * 
	 * @param useTimeFormat true if the time format is used or false if double number format is used
	 */
	public void setUseTimeFormatX(boolean useTimeFormat) {
		this.useTimeFormat = useTimeFormat;
	}

	/**
	 * Adds a property change listener for the property vTable.
	 * 
	 * @param listener the listener which receives notifications when the vTable changes
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}
	
	/**
	 * Remove property change listener from this explorer.
	 *  
	 * @param listener the listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.csstudio.swt.xygraph.figures.IAxisListener#axisRangeChanged(org.csstudio.swt.xygraph.figures.Axis, org.csstudio.swt.xygraph.linearscale.Range, org.csstudio.swt.xygraph.linearscale.Range)
	 */
	@Override
	public void axisRangeChanged(Axis axis, Range old_range, Range new_range) {
		createAndSendVTable();
	}

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.swt.xygraph.figures.IAxisListener#axisRevalidated(org.csstudio.swt.xygraph.figures.Axis)
	 */
	@Override
	public void axisRevalidated(Axis axis) {
		//ignore
	}

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.swt.xygraph.figures.IAxisListener#axisForegroundColorChanged(org.csstudio.swt.xygraph.figures.Axis, org.eclipse.swt.graphics.Color, org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void axisForegroundColorChanged(Axis axis, Color oldColor, Color newColor) {
		//ignore		
	}

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.swt.xygraph.figures.IAxisListener#axisTitleChanged(org.csstudio.swt.xygraph.figures.Axis, java.lang.String, java.lang.String)
	 */
	@Override
	public void axisTitleChanged(Axis axis, String oldTitle, String newTitle) {
		//ignore		
	}

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.swt.xygraph.figures.IAxisListener#axisAutoScaleChanged(org.csstudio.swt.xygraph.figures.Axis, boolean, boolean)
	 */
	@Override
	public void axisAutoScaleChanged(Axis axis, boolean oldAutoScale, boolean newAutoScale) {
		//ignore		
	}

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.swt.xygraph.figures.IAxisListener#axisLogScaleChanged(org.csstudio.swt.xygraph.figures.Axis, boolean, boolean)
	 */
	@Override
	public void axisLogScaleChanged(Axis axis, boolean old, boolean logScale) {
		//ignore		
	}
}
