package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.datadefinition.ColorMap;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.IntensityGraphFigure;
import org.csstudio.opibuilder.widgets.model.IntensityGraphModel;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.eclipse.draw2d.IFigure;

/**The widget editpart of intensity graph widget.
 * @author Xihui Chen
 *
 */
public class IntensityGraphEditPart extends AbstractPVWidgetEditPart {

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
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((IntensityGraphFigure)figure).setShowRamp((Boolean)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(IntensityGraphModel.PROP_SHOW_RAMP, handler);
	}

}
