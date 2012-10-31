package org.csstudio.common.trendplotter.ui;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.swt.xygraph.figures.Annotation;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.eclipse.swt.graphics.Color;


/**
 * This class is use to add PropertyChangeListener capability to PlotListener.
 * It tests the propertyChangeEvent and transfer the call to the appropriate function of PlotListener
 *
 * For the time being :
 * 	=> BGCOLOR : org.csstudio.swt.xygraph.figures.PlotArea bgColor property
 *  => XY_GRAPH_MEM : org.csstudio.swt.xygraph.figures.XYGraph xyGraphMem property
 *  => ANNOTATION_LIST : org.csstudio.swt.xygraph.figures.PlotArea annotationList property
 *
 * @author L.PHILIPPE (GANIL)
 */
@SuppressWarnings("nls")
public class PlotConfigListener implements PropertyChangeListener {

    public String BG_COLOR= "background_color";
	public String XYGRAPH_CONFIG = "config";
	public String ANNOTATION_LIST = "annotationList";

	private PlotListener listener;

	public PlotConfigListener(PlotListener listener){
		this.listener = listener;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(BG_COLOR)){
			//Background color of the plot changed
			listener.backgroundColorChanged((Color)evt.getNewValue());
			//System.err.println("BGCOLOR CHANGED");
		}

		if(evt.getPropertyName().equals(XYGRAPH_CONFIG)){
			//Configure Graph settings of the plot changed
			listener.xyGraphConfigChanged((XYGraph)evt.getNewValue());
			//System.err.println("**** XYGRAPHMEM CHANGED ****");
		}

		if(evt.getPropertyName().equals(ANNOTATION_LIST)){
			//Configure Graph settings of the plot changed
			if(evt.getNewValue() == null)
				listener.removeAnnotationChanged((Annotation)evt.getOldValue());
			else
				listener.addAnnotationChanged((Annotation)evt.getNewValue());
			//System.err.println("ANNOTATION_LIST CHANGED");
		}
	}

}
