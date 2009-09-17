package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.LabelFigure;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.widgets.Display;

public class LabelEditPart extends AbstractWidgetEditPart {

	
	@Override
	protected IFigure doCreateFigure() {
		LabelFigure labelFigure = new LabelFigure();
		labelFigure.setFont(CustomMediaFactory.getInstance().getFont(
				getWidgetModel().getFont()));
		labelFigure.setText(getWidgetModel().getText());		
		labelFigure.setOpaque(!getWidgetModel().isTransparent());
		labelFigure.getLabel().setTextAlignment(
				(int) (8 * Math.pow(2, getWidgetModel().getVerticalAlignment())));
		labelFigure.getLabel().setLabelAlignment(
				(int) (1 * Math.pow(2, getWidgetModel().getHorizontalAlignment())));
		labelFigure.getLabel().setTextPlacement(PositionConstants.WEST);
		return labelFigure;
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		if(getExecutionMode() == ExecutionMode.EDIT_MODE)
			installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new LabelDirectEditPolicy());
		
	}
	
	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					final IFigure figure) {
				((LabelFigure)figure).setText((String)newValue);
				Display.getCurrent().timerExec(10, new Runnable() {					
					public void run() {
						if(getWidgetModel().isAutoSize())
							getWidgetModel().setSize(((LabelFigure)figure).getLabel().getPreferredSize());
					}
				});
				
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_TEXT, handler);
		
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					final IFigure figure) {
				Display.getCurrent().timerExec(10, new Runnable() {					
					public void run() {
						if(getWidgetModel().isAutoSize())
							getWidgetModel().setSize(((LabelFigure)figure).getLabel().getPreferredSize());
					}
				});
				
				return true;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_FONT, handler);		
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_STYLE, handler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_WIDTH, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((LabelFigure)figure).setOpaque(!(Boolean)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_TRANSPARENT, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {				
				if((Boolean)newValue)
					getWidgetModel().setSize(((LabelFigure)figure).getLabel().getPreferredSize());
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_AUTOSIZE, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((LabelFigure)figure).getLabel().setLabelAlignment(
						(int) (1 * Math.pow(2, (Integer)newValue)));
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_ALIGN_H, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((LabelFigure)figure).getLabel().setTextAlignment(
						(int) (8 * Math.pow(2, (Integer)newValue)));
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_ALIGN_V, handler);
		
	}

	private void performDirectEdit(){
		new LabelEditManager(this, new LabelCellEditorLocator((LabelFigure)getFigure())).show();
	}
	
	@Override
	public void performRequest(Request request){
		if (getExecutionMode() == ExecutionMode.EDIT_MODE &&( 
				request.getType() == RequestConstants.REQ_DIRECT_EDIT || 
				request.getType() == RequestConstants.REQ_OPEN))
			performDirectEdit();
	}
	
	
	@Override
	public LabelModel getWidgetModel() {
		return (LabelModel)getModel();
	}

	
}
