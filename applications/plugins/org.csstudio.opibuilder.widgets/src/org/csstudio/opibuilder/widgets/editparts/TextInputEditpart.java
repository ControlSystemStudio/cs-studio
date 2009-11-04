package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.LabelFigure;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.TextInputModel;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;

/**The editpart for text input widget.
 * @author Xihui Chen
 *
 */
public class TextInputEditpart extends TextIndicatorEditPart {

	
	@Override
	public TextInputModel getWidgetModel() {
		return (TextInputModel)getModel();
	}
	
	
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new TextIndicatorDirectEditPolicy());		
	}
	
	@Override
	public void activate() {
		markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME);
		super.activate();
	}
	
	@Override
	protected void registerPropertyChangeHandlers() {
		super.registerPropertyChangeHandlers();
		if(getExecutionMode() == ExecutionMode.RUN_MODE){
			removeAllPropertyChangeHandlers(LabelModel.PROP_TEXT);
			IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler(){
				public boolean handleChange(Object oldValue, Object newValue,
						IFigure figure) {
					String text = (String)newValue;
					/*IValue value = getPVValue(AbstractPVWidgetModel.PROP_PVNAME);
					if(value instanceof IDoubleValue){
						try {
							Double d = Double.parseDouble(text);
							setPVValue(AbstractPVWidgetModel.PROP_PVNAME, d);
						} catch (NumberFormatException e) {
							
						}
					}else if(value instanceof ILongValue || value instanceof IEnumeratedValue){
						try {
							Integer l = Integer.parseInt(text);
							setPVValue(AbstractPVWidgetModel.PROP_PVNAME, l);
						} catch (NumberFormatException e) {							
						}
					}else if(value instanceof IStringValue){
					*/	
					//}
					if(getWidgetModel().getPVName().trim().length() == 0)
						((LabelFigure)figure).setText(text);
					else
						setPVValue(AbstractPVWidgetModel.PROP_PVNAME, text);
					
					return false;
				}
			};			
			setPropertyChangeHandler(LabelModel.PROP_TEXT, handler);
		}
	
	}
	
	
	@Override
	public void performRequest(Request request){
		if (getFigure().isEnabled() && (request.getType() == RequestConstants.REQ_DIRECT_EDIT || 
				request.getType() == RequestConstants.REQ_OPEN))
			performDirectEdit();
	}
	
	protected void performDirectEdit(){
		new LabelEditManager(this, 
				new LabelCellEditorLocator((LabelFigure)getFigure()), false).show();
	}
}
