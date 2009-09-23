package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgets.figures.LabelFigure;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.TextIndicatorModel;
import org.csstudio.opibuilder.widgets.model.TextIndicatorModel.FormatEnum;
import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.IEnumeratedValue;
import org.csstudio.platform.data.ILongValue;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.IValue.Format;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.widgets.Display;

/**The editor for text indicator widget.
 * @author Xihui Chen
 *
 */
public class TextIndicatorEditPart extends AbstractPVWidgetEditPart {

	private static final String HEX_PREFIX = "0x";

	@Override
	protected IFigure doCreateFigure() {
		LabelFigure labelFigure = new LabelFigure();
		labelFigure.setFont(CustomMediaFactory.getInstance().getFont(
				getWidgetModel().getFont().getFontData()));
		labelFigure.setText(getWidgetModel().getText());	
		labelFigure.setOpaque(!getWidgetModel().isTransparent());		
		labelFigure.getLabel().setTextAlignment(
				(int) (8 * Math.pow(2, getWidgetModel().getVerticalAlignment())));
		labelFigure.getLabel().setLabelAlignment(
				(int) (1 * Math.pow(2, getWidgetModel().getHorizontalAlignment())));
		labelFigure.getLabel().setTextPlacement(PositionConstants.WEST);
		labelFigure.setScrollbarVisible(getWidgetModel().isShowScrollbar());
		return labelFigure;
	}
	
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		if(getExecutionMode() == ExecutionMode.EDIT_MODE)
			installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new TextIndicatorDirectEditPolicy());		
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
							performAutoSize(figure);
					}
				});
				
				return true;
			}
		};
		setPropertyChangeHandler(TextIndicatorModel.PROP_TEXT, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					final IFigure figure) {
				if(newValue == null)
					return false;				
				formatValue(newValue, AbstractPVWidgetModel.PROP_PVVALUE, figure);
				return true;
			}		
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, handler);		
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					final IFigure figure) {
				formatValue(newValue, TextIndicatorModel.PROP_FORMAT_TYPE, figure);
				return true;
			}		
		};
		setPropertyChangeHandler(TextIndicatorModel.PROP_FORMAT_TYPE, handler);	
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					final IFigure figure) {
				formatValue(newValue, TextIndicatorModel.PROP_PRECISION, figure);
				return true;
			}		
		};
		setPropertyChangeHandler(TextIndicatorModel.PROP_PRECISION, handler);	
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					final IFigure figure) {
				formatValue(newValue, TextIndicatorModel.PROP_PRECISION_FROM_DB, figure);
				return true;
			}		
		};
		setPropertyChangeHandler(TextIndicatorModel.PROP_PRECISION_FROM_DB, handler);	
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					final IFigure figure) {
				formatValue(newValue, TextIndicatorModel.PROP_SHOW_UNITS, figure);
				return true;
			}		
		};
		setPropertyChangeHandler(TextIndicatorModel.PROP_SHOW_UNITS, handler);	
		
		
		
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((LabelFigure)figure).setOpaque(!(Boolean)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(TextIndicatorModel.PROP_TRANSPARENT, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {				
				if((Boolean)newValue){
					performAutoSize(figure);
				}
				return true;
			}
			
		};
		setPropertyChangeHandler(TextIndicatorModel.PROP_AUTOSIZE, handler);
		
		IWidgetPropertyChangeHandler fontHandler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				figure.setFont(CustomMediaFactory.getInstance().getFont(((OPIFont)newValue).getFontData()));
				return true;
			}
		};		
		setPropertyChangeHandler(LabelModel.PROP_FONT, fontHandler);
			
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					final IFigure figure) {
				if(getWidgetModel().isAutoSize()){
					Display.getCurrent().timerExec(10, new Runnable() {					
						public void run() {							
							performAutoSize(figure);
						}
					});					
				}				
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_FONT, handler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_STYLE, handler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_WIDTH, handler);
		
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
	
	@Override
	public TextIndicatorModel getWidgetModel() {
		return (TextIndicatorModel)getModel();
	}
	
	protected void performDirectEdit(){
		new LabelEditManager(this, new LabelCellEditorLocator((LabelFigure)getFigure())).show();
	}
	
	@Override
	public void performRequest(Request request){
		if (getExecutionMode() == ExecutionMode.EDIT_MODE &&( 
				request.getType() == RequestConstants.REQ_DIRECT_EDIT || 
				request.getType() == RequestConstants.REQ_OPEN))
			performDirectEdit();
	}
	
	
	/**
	 * @param figure
	 */
	private void performAutoSize(IFigure figure) {
		Insets insets = figure.getInsets();
		getWidgetModel().setSize(((LabelFigure)figure).getLabel().
				getPreferredSize().expand(insets.left + insets.bottom, insets.top + insets.bottom));
	}
	
	/**
	 * @param newValue
	 * @return
	 */
	private void formatValue(Object newValue, String propId, IFigure figure) {
		IValue value = getPVValue(AbstractPVWidgetModel.PROP_PVNAME);
		FormatEnum formatEnum = getWidgetModel().getFormat();
		int precision = getWidgetModel().getPrecision();
		if(getWidgetModel().isPrecisionFromDB())
			precision = -1;
		boolean showUnit = getWidgetModel().isShowUnits();
		if(propId.equals(AbstractPVWidgetModel.PROP_PVVALUE))
			value = (IValue)newValue;
		else if(propId.equals(TextIndicatorModel.PROP_FORMAT_TYPE))
			formatEnum = FormatEnum.values()[(Integer)newValue];
		else if(propId.equals(TextIndicatorModel.PROP_PRECISION))
			precision = (Integer)newValue;
		else if(propId.equals(TextIndicatorModel.PROP_PRECISION_FROM_DB)){
			precision = (Boolean)newValue ? -1 : precision;
		}else if(propId.equals(TextIndicatorModel.PROP_SHOW_UNITS)){
			showUnit = (Boolean)newValue;
		}
		
		String text;
		switch (formatEnum) {		
		case DECIAML:
			text = value.format(Format.Decimal, precision);
			break;
		case EXP:
			text = value.format(Format.Exponential, precision);
			break;
		case HEX:	
			if(value instanceof IDoubleValue)
				text = HEX_PREFIX + Long.toHexString((long) ((IDoubleValue)value).getValue());
			else if(value instanceof ILongValue)
				text = HEX_PREFIX + Long.toHexString(((ILongValue)value).getValue());
			else if(value instanceof IEnumeratedValue)
				text = HEX_PREFIX + Integer.toHexString(((IEnumeratedValue)value).getValue());
			else
				text = value.format();			
			break;
		case DEFAULT:
		default:		
			text = value.format(Format.Default, precision);
			break;
		}		
		
		if(showUnit && value.getMetaData() instanceof INumericMetaData)
			text = text + " " + ((INumericMetaData)value.getMetaData()).getUnits(); //$NON-NLS-1$ //$NON-NLS-2$	
		
		//synchronize the property value without fire listeners.
		getWidgetModel().getProperty(
				TextIndicatorModel.PROP_TEXT).setPropertyValue(text, false);
		((LabelFigure)figure).setText(text);		
		
		if(getWidgetModel().isAutoSize())
			performAutoSize(figure);
		
	}
	
}
