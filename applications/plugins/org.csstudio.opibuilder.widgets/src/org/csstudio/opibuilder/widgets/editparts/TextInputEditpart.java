package org.csstudio.opibuilder.widgets.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.ParseException;

import org.csstudio.opibuilder.commands.SetWidgetPropertyCommand;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.TextInputModel;
import org.csstudio.opibuilder.widgets.model.TextIndicatorModel.FormatEnum;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.IValue;
import org.csstudio.swt.widgets.datadefinition.IManualStringValueChangeListener;
import org.csstudio.swt.widgets.figures.TextFigure;
import org.csstudio.swt.widgets.figures.TextInputFigure;
import org.csstudio.swt.widgets.figures.TextInputFigure.FileReturnPart;
import org.csstudio.swt.widgets.figures.TextInputFigure.FileSource;
import org.csstudio.swt.widgets.figures.TextInputFigure.SelectorType;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;

/**The editpart for text input widget.)
 * @author Xihui Chen
 *
 */
public class TextInputEditpart extends TextIndicatorEditPart {

	private PVListener pvLoadLimitsListener;
	private INumericMetaData meta = null;
	
	@Override
	public TextInputModel getWidgetModel() {
		return (TextInputModel)getModel();
	}
	
	@Override
	protected IFigure doCreateFigure() {
		TextInputFigure textInputFigure = (TextInputFigure)super.doCreateFigure();
		textInputFigure.setSelectorType(getWidgetModel().getSelectorType());
		textInputFigure.setDateTimeFormat(getWidgetModel().getDateTimeFormat());
		textInputFigure.setFileSource(getWidgetModel().getFileSource());
		textInputFigure.setFileReturnPart(getWidgetModel().getFileReturnPart());
		
		textInputFigure
				.addManualValueChangeListener(new IManualStringValueChangeListener() {

					@Override
					public void manualValueChanged(String newValue) {
						if(getExecutionMode() == ExecutionMode.RUN_MODE){
							setPVValue(TextInputModel.PROP_PVNAME, newValue);
							getWidgetModel().setPropertyValue(
								TextInputModel.PROP_TEXT, newValue, false);
						}else{
							getViewer().getEditDomain().getCommandStack().execute(
									new SetWidgetPropertyCommand(
											getWidgetModel(), TextInputModel.PROP_TEXT, newValue));
						}
					}
				});
		
		return textInputFigure;
	}
	
	
	@Override
	protected TextFigure createTextFigure() {
		return new TextInputFigure(getExecutionMode() == ExecutionMode.RUN_MODE);
	}
	
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new TextIndicatorDirectEditPolicy());		
	}
	
	@Override
	public void activate() {
		markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME, AbstractPVWidgetModel.PROP_PVVALUE);
		super.activate();
	}
	
	@Override
	protected void doActivate() {	
		super.doActivate();
		registerLoadLimitsListener();
	}


	/**
	 * 
	 */
	private void registerLoadLimitsListener() {
		if(getExecutionMode() == ExecutionMode.RUN_MODE){
			final TextInputModel model = getWidgetModel();
			if(model.isLimitsFromPV()){
				PV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
				if(pv != null){	
					if(pvLoadLimitsListener == null)
						pvLoadLimitsListener = new PVListener() {				
							public void pvValueUpdate(PV pv) {
								IValue value = pv.getValue();
								if (value != null && value.getMetaData() instanceof INumericMetaData){
									INumericMetaData new_meta = (INumericMetaData)value.getMetaData();
									if(meta == null || !meta.equals(new_meta)){
										meta = new_meta;
										model.setPropertyValue(TextInputModel.PROP_MAX,	meta.getDisplayHigh());
										model.setPropertyValue(TextInputModel.PROP_MIN,	meta.getDisplayLow());								
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
					((TextFigure)figure).setText(text);
					FormatEnum formatEnum = getWidgetModel().getFormat();
						switch (formatEnum){						
						case DECIAML:						
							try {
								DecimalFormat format = new DecimalFormat();
								double value = format.parse(text).doubleValue();
								double min = getWidgetModel().getMinimum();
								double max = getWidgetModel().getMaximum();
								double coValue = Math.max(min, Math.min(value, max));
								if(coValue != value)
									((TextFigure)figure).setText(format.format(coValue));
								setPVValue(AbstractPVWidgetModel.PROP_PVNAME, coValue);
							} catch (ParseException e) {
								setPVValue(AbstractPVWidgetModel.PROP_PVNAME, text);
							}
							break;
						case HEX:
							Integer[] iString = new Integer[text.length()];
							char[] textChars = text.toCharArray();
							
							for (int ii = 0; ii< text.length(); ii++){
								iString[ii] = Integer.valueOf(textChars[ii]);
							}
							setPVValue(AbstractPVWidgetModel.PROP_PVNAME, iString);
							break;
						case DEFAULT:					
						case STRING:
						default:
							setPVValue(AbstractPVWidgetModel.PROP_PVNAME, text);
							break;
						
						}
					return false;
				}
			};			
			setPropertyChangeHandler(LabelModel.PROP_TEXT, handler);
		}
		
		IWidgetPropertyChangeHandler pvNameHandler = new IWidgetPropertyChangeHandler() {
			
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				registerLoadLimitsListener();
				return false;
			}
		};		
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, pvNameHandler);
		
		IWidgetPropertyChangeHandler dateTimeFormatHandler = new IWidgetPropertyChangeHandler() {
			
			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				((TextInputFigure)figure).setDateTimeFormat((String)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(TextInputModel.PROP_DATETIME_FORMAT, dateTimeFormatHandler);
		
		IWidgetPropertyChangeHandler fileSourceHandler = new IWidgetPropertyChangeHandler() {
			
			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				((TextInputFigure)figure).setFileSource(FileSource.values()[(Integer)newValue]);
				return false;
			}
		};
		setPropertyChangeHandler(TextInputModel.PROP_FILE_SOURCE, fileSourceHandler);
		
		IWidgetPropertyChangeHandler fileReturnPartHandler = new IWidgetPropertyChangeHandler() {
			
			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				((TextInputFigure)figure).setFileReturnPart(FileReturnPart.values()[(Integer)newValue]);
				return false;
			}
		};
		setPropertyChangeHandler(TextInputModel.PROP_FILE_RETURN_PART, fileReturnPartHandler);
		
		
		getWidgetModel().getProperty(TextInputModel.PROP_SELECTOR_TYPE).
			addPropertyChangeListener(new PropertyChangeListener() {
			
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					SelectorType selectorType = SelectorType.values()[(Integer)evt.getNewValue()];
					((TextInputFigure)figure).setSelectorType(selectorType);
					switch (selectorType) {
					case NONE:
						getWidgetModel().setPropertyVisible(TextInputModel.PROP_DATETIME_FORMAT, false);
						getWidgetModel().setPropertyVisible(TextInputModel.PROP_FILE_RETURN_PART, false);
						getWidgetModel().setPropertyVisible(TextInputModel.PROP_FILE_SOURCE, false);						
						break;
					case DATETIME:
						getWidgetModel().setPropertyVisible(TextInputModel.PROP_DATETIME_FORMAT, true);
						getWidgetModel().setPropertyVisible(TextInputModel.PROP_FILE_RETURN_PART, false);
						getWidgetModel().setPropertyVisible(TextInputModel.PROP_FILE_SOURCE, false);
						break;
					case FILE:
						getWidgetModel().setPropertyVisible(TextInputModel.PROP_DATETIME_FORMAT, false);
						getWidgetModel().setPropertyVisible(TextInputModel.PROP_FILE_RETURN_PART, true);
						getWidgetModel().setPropertyVisible(TextInputModel.PROP_FILE_SOURCE, true);
						break;
					default:
						break;
					}					
				}
		});
		
		
		
	
	}
	
	@Override
	protected void doDeActivate() {
		super.doDeActivate();
		if(getWidgetModel().isLimitsFromPV()){
			PV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
			if(pv != null && pvLoadLimitsListener != null){	
				pv.removeListener(pvLoadLimitsListener);
			}
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
				new LabelCellEditorLocator((Figure)getFigure()), false).show();
	}
	
	
}
