package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgets.figures.LabelFigure;
import org.csstudio.opibuilder.widgets.figures.SpinnerFigure;
import org.csstudio.opibuilder.widgets.figures.LabelFigure.H_ALIGN;
import org.csstudio.opibuilder.widgets.figures.LabelFigure.V_ALIGN;
import org.csstudio.opibuilder.widgets.figures.SpinnerFigure.ISpinnerListener;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.SpinnerModel;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;

/**The editpart for spinner widget.
 * @author Xihui Chen
 *
 */
public class SpinnerEditpart extends AbstractPVWidgetEditPart {
	
	private PVListener pvLoadLimitsListener;
	private INumericMetaData meta = null;
	@Override
	protected IFigure doCreateFigure() {
		SpinnerFigure spinner = new SpinnerFigure(getExecutionMode());
		LabelFigure labelFigure = spinner.getLabelFigure();
		labelFigure.setFont(CustomMediaFactory.getInstance().getFont(
				getWidgetModel().getFont().getFontData()));
		labelFigure.setOpaque(!getWidgetModel().isTransparent());
		labelFigure.setH_alignment(getWidgetModel().getHorizontalAlignment());
		labelFigure.setV_alignment(getWidgetModel().getVerticalAlignment());
		spinner.setMax(getWidgetModel().getMaximum());
		spinner.setMin(getWidgetModel().getMinimum());
		spinner.setStepIncrement(getWidgetModel().getStepIncrement());
		spinner.setPageIncrement(getWidgetModel().getPageIncrement());
		if(getExecutionMode() == ExecutionMode.RUN_MODE){
			spinner.addManualValueChangeListener(new ISpinnerListener() {
				
				public void manualValueChanged(double newValue) {
					setPVValue(SpinnerModel.PROP_PVNAME, newValue);
				}
			});
		}
		
		return spinner;
	}
	
	
	@Override
	public SpinnerModel getWidgetModel() {
		return (SpinnerModel)getModel();
	}
	
	
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new SpinnerDirectEditPolicy());		
	}
	
	@Override
	public void activate() {
		markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME, AbstractPVWidgetModel.PROP_PVVALUE);
		super.activate();
	}
	
	@Override
	protected void doActivate() {
		super.doActivate();
		if(getExecutionMode() == ExecutionMode.RUN_MODE){
			final SpinnerModel model = getWidgetModel();
			if(model.isLimitsFromPV()){
				PV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
				if(pv != null){	
					pvLoadLimitsListener = new PVListener() {				
						public void pvValueUpdate(PV pv) {
							IValue value = pv.getValue();
							if (value != null && value.getMetaData() instanceof INumericMetaData){
								INumericMetaData new_meta = (INumericMetaData)value.getMetaData();
								if(meta == null || !meta.equals(new_meta)){
									meta = new_meta;
									model.setPropertyValue(SpinnerModel.PROP_MAX,	meta.getDisplayHigh());
									model.setPropertyValue(SpinnerModel.PROP_MIN,	meta.getDisplayLow());								
								}
							}
						}					
						public void pvDisconnected(PV pv) {}
					};
					pv.addListener(pvLoadLimitsListener);				
				}
			}
		}
	}
	
	@Override
	protected void registerPropertyChangeHandlers() {
			//text
			IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler(){
				public boolean handleChange(Object oldValue, Object newValue,
						IFigure figure) {
					String text = (String)newValue;						
					try {						
						double value = Double.parseDouble(text);
						//coerce value in range
						value = Math.max(((SpinnerFigure)figure).getMin(), 
								Math.min(((SpinnerFigure)figure).getMax(), value));

						if(getExecutionMode() == ExecutionMode.RUN_MODE){
	
							if(getWidgetModel().getPVName().trim().length() == 0)
								((SpinnerFigure)figure).setValue(value);
							else
								setPVValue(AbstractPVWidgetModel.PROP_PVNAME, value);						
							return false;
						}else
							((SpinnerFigure)figure).setValue(value);
						return false;
					} catch (NumberFormatException e) {
						return false;
					}					
				}
			};			
			setPropertyChangeHandler(SpinnerModel.PROP_TEXT, handler);
			
			//pv value
			handler = new IWidgetPropertyChangeHandler() {				
				public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
					if(newValue == null || !(newValue instanceof IValue))
						return false;
					double value = ValueUtil.getDouble((IValue) newValue);
					((SpinnerFigure)figure).setDisplayValue(value);
					return false;
				}
			};
			setPropertyChangeHandler(SpinnerModel.PROP_PVVALUE, handler);
			
			//min			
			handler = new IWidgetPropertyChangeHandler() {
				public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
					((SpinnerFigure)figure).setMin((Double)newValue);					
					return false;
				}
			};
			setPropertyChangeHandler(SpinnerModel.PROP_MIN, handler);
			
			//max			
			handler = new IWidgetPropertyChangeHandler() {
				public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
					((SpinnerFigure)figure).setMax((Double)newValue);					
					return false;
				}
			};
			setPropertyChangeHandler(SpinnerModel.PROP_MAX, handler);
			
			//step increment			
			handler = new IWidgetPropertyChangeHandler() {
				public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
					((SpinnerFigure)figure).setStepIncrement((Double)newValue);					
					return false;
				}
			};
			setPropertyChangeHandler(SpinnerModel.PROP_STEP_INCREMENT, handler);
			
			//page increment			
			handler = new IWidgetPropertyChangeHandler() {
				public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
					((SpinnerFigure)figure).setPageIncrement((Double)newValue);					
					return false;
				}
			};
			setPropertyChangeHandler(SpinnerModel.PROP_PAGE_INCREMENT, handler);
			
			//font
			IWidgetPropertyChangeHandler fontHandler = new IWidgetPropertyChangeHandler(){
				public boolean handleChange(Object oldValue, Object newValue,
						IFigure figure) {
					((SpinnerFigure)figure).getLabelFigure().
						setFont(CustomMediaFactory.getInstance().getFont(
							((OPIFont)newValue).getFontData()));
					return true;
				}
			};		
			setPropertyChangeHandler(LabelModel.PROP_FONT, fontHandler);
			

			handler = new IWidgetPropertyChangeHandler(){
				public boolean handleChange(Object oldValue, Object newValue,
						IFigure figure) {
					((SpinnerFigure)figure).getLabelFigure().setH_alignment(H_ALIGN.values()[(Integer)newValue]);
					return true;
				}
			};
			setPropertyChangeHandler(LabelModel.PROP_ALIGN_H, handler);
			
			handler = new IWidgetPropertyChangeHandler(){
				public boolean handleChange(Object oldValue, Object newValue,
						IFigure figure) {
					((SpinnerFigure)figure).getLabelFigure().setV_alignment(V_ALIGN.values()[(Integer)newValue]);
					return true;
				}
			};
			setPropertyChangeHandler(LabelModel.PROP_ALIGN_V, handler);
			

			handler = new IWidgetPropertyChangeHandler(){
				public boolean handleChange(Object oldValue, Object newValue,
						IFigure figure) {
					((SpinnerFigure)figure).getLabelFigure().setOpaque(!(Boolean)newValue);
					return true;
				}
			};
			setPropertyChangeHandler(LabelModel.PROP_TRANSPARENT, handler);
			
			
			
			
	}
	
	
	@Override
	public void performRequest(Request request){
		if (getFigure().isEnabled() && (request.getType() == RequestConstants.REQ_DIRECT_EDIT || 
				request.getType() == RequestConstants.REQ_OPEN))
			performDirectEdit();
	}
	
	protected void performDirectEdit(){
		new LabelEditManager(this, 
				new LabelCellEditorLocator(
						((SpinnerFigure)getFigure()).getLabelFigure()), false).show();
	}

	@Override
	protected void doDeActivate() {
		super.doDeActivate();
		if(getWidgetModel().isLimitsFromPV()){
			PV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
			if(pv != null){	
				pv.removeListener(pvLoadLimitsListener);
			}
		}
		
	}
	
	@Override
	public Object getValue() {
		return ((SpinnerFigure)getFigure()).getValue();
	}


	@Override
	public void setValue(Object value) {
		if(value instanceof Double || value instanceof Integer)
			((SpinnerFigure)getFigure()).setValue((Double) value);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class key) {
		if(key == LabelFigure.class)
			return ((SpinnerFigure)getFigure()).getLabelFigure();

		return super.getAdapter(key);
	}

	
}
