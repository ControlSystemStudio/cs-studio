package org.csstudio.opibuilder.widgets.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.opibuilder.datadefinition.ColorMap;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.IntensityGraphFigure;
import org.csstudio.opibuilder.widgets.model.IntensityGraphModel;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;

/**The widget editpart of intensity graph widget.
 * @author Xihui Chen
 *
 */
public class IntensityGraphEditPart extends AbstractPVWidgetEditPart {

	
	private boolean innerTrig;
	
	@Override
	protected IFigure doCreateFigure() {
		IntensityGraphModel model = getWidgetModel();
		IntensityGraphFigure graph = new IntensityGraphFigure();
		graph.setMin(model.getMinimum());
		graph.setMax(model.getMaximum());
		graph.setDataWidth(model.getDataWidth());
		graph.setDataHeight(model.getDataHeight());
		graph.setColorMap(model.getColorMap());
		graph.setShowRamp(model.isShowRamp());
		return graph;
	}
	
	@Override
	public IntensityGraphModel getWidgetModel() {
		return (IntensityGraphModel)getModel();
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		initGraphAreaSizeProperty();
		
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
		
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				if(newValue == null || !(newValue instanceof IValue))
					return false;
				IValue value = (IValue)newValue;
				//if(ValueUtil.getSize(value) > 1){
					((IntensityGraphFigure)figure).setDataArray(ValueUtil.getDoubleArray(value));
				//}else
				//	((IntensityGraphFigure)figure).setDataArray(ValueUtil.getDouble(value));			
				return false;
			}
		};
		
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((IntensityGraphFigure)figure).setMin((Double)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(IntensityGraphModel.PROP_MIN, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((IntensityGraphFigure)figure).setMax((Double)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(IntensityGraphModel.PROP_MAX, handler);
		
			
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((IntensityGraphFigure)figure).setDataWidth((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(IntensityGraphModel.PROP_DATA_WIDTH, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((IntensityGraphFigure)figure).setDataHeight((Integer)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(IntensityGraphModel.PROP_DATA_HEIGHT, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((IntensityGraphFigure)figure).setColorMap((ColorMap)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(IntensityGraphModel.PROP_COLOR_MAP, handler);	
		
		
		getWidgetModel().getProperty(IntensityGraphModel.PROP_SHOW_RAMP).addPropertyChangeListener(
				new PropertyChangeListener() {				
					public void propertyChange(PropertyChangeEvent evt) {
						((IntensityGraphFigure)getFigure()).setShowRamp((Boolean)evt.getNewValue());
						Dimension d = ((IntensityGraphFigure)getFigure()).getGraphAreaInsets();	
						innerTrig = true;
						getWidgetModel().setPropertyValue(IntensityGraphModel.PROP_GRAPH_AREA_WIDTH, 
								getWidgetModel().getWidth() - d.width);
					}
		});
		
		getWidgetModel().getProperty(IntensityGraphModel.PROP_WIDTH).addPropertyChangeListener(
				new PropertyChangeListener() {				
					public void propertyChange(PropertyChangeEvent evt) {
						if(!innerTrig){ // if it is not triggered from inner
							innerTrig = true;
							Dimension d = ((IntensityGraphFigure)getFigure()).getGraphAreaInsets();	
							getWidgetModel().setPropertyValue(IntensityGraphModel.PROP_GRAPH_AREA_WIDTH, 
								((Integer)evt.getNewValue()) - d.width);							
						}else //if it is triggered from inner, do nothing
							innerTrig = false;
					}
		});				
		
		getWidgetModel().getProperty(IntensityGraphModel.PROP_GRAPH_AREA_WIDTH).addPropertyChangeListener(
				new PropertyChangeListener() {				
					public void propertyChange(PropertyChangeEvent evt) {
						if(!innerTrig){
							innerTrig = true;
							Dimension d = ((IntensityGraphFigure)getFigure()).getGraphAreaInsets();				
							getWidgetModel().setPropertyValue(IntensityGraphModel.PROP_WIDTH, 
									((Integer)evt.getNewValue()) + d.width);							
						}else
							innerTrig = false;
					}
		});	
				
		
		getWidgetModel().getProperty(IntensityGraphModel.PROP_HEIGHT).addPropertyChangeListener(
				new PropertyChangeListener() {				
					public void propertyChange(PropertyChangeEvent evt) {
						if(!innerTrig){
							innerTrig = true;
							Dimension d = ((IntensityGraphFigure)getFigure()).getGraphAreaInsets();				
							getWidgetModel().setPropertyValue(IntensityGraphModel.PROP_GRAPH_AREA_HEIGHT, 
								((Integer)evt.getNewValue()) - d.height);
						}else
							innerTrig = false;
					}
		});				
		
		getWidgetModel().getProperty(IntensityGraphModel.PROP_GRAPH_AREA_HEIGHT).addPropertyChangeListener(
				new PropertyChangeListener() {				
					public void propertyChange(PropertyChangeEvent evt) {
						if(!innerTrig){
							innerTrig = true;	
							Dimension d = ((IntensityGraphFigure)getFigure()).getGraphAreaInsets();				
							getWidgetModel().setPropertyValue(IntensityGraphModel.PROP_HEIGHT, 
								((Integer)evt.getNewValue()) + d.height);
						}else
							innerTrig = false;
					}
		});	
	}
	
	private void initGraphAreaSizeProperty(){
		Dimension d = ((IntensityGraphFigure)figure).getGraphAreaInsets();				
		getWidgetModel().setPropertyValue(IntensityGraphModel.PROP_GRAPH_AREA_WIDTH, 
				getWidgetModel().getSize().width - d.width);
		getWidgetModel().setPropertyValue(IntensityGraphModel.PROP_GRAPH_AREA_HEIGHT, 
				getWidgetModel().getSize().height - d.height);
	}

	@Override
	public double[] getValue() {
		return ((IntensityGraphFigure)getFigure()).getDataArray();
	}

	@Override
	public void setValue(Object value) {
		if(value instanceof double[] || value instanceof Double[]){
			((IntensityGraphFigure)getFigure()).setDataArray((double[]) value);
		}
	}

}
