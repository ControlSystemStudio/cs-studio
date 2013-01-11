/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.ParseException;

import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.IEnumeratedValue;
import org.csstudio.data.values.ILongValue;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.IStringValue;
import org.csstudio.data.values.IValue;
import org.csstudio.opibuilder.commands.SetWidgetPropertyCommand;
import org.csstudio.opibuilder.datadefinition.FormatEnum;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.pvmanager.PMObjectValue;
import org.csstudio.opibuilder.scriptUtil.GUIUtil;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.TextInputModel;
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
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.epics.vtype.Array;
import org.epics.vtype.Scalar;
import org.epics.vtype.VEnum;
import org.epics.vtype.VNumberArray;

/**
 * The editpart for text input widget.)
 * 
 * @author Xihui Chen
 * 
 */
public class TextInputEditpart extends TextUpdateEditPart {

	private static final char SPACE = ' '; //$NON-NLS-1$
	private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat();
	private PVListener pvLoadLimitsListener;
	private INumericMetaData meta = null;

	@Override
	public TextInputModel getWidgetModel() {
		return (TextInputModel) getModel();
	}

	@Override
	protected IFigure doCreateFigure() {
		TextInputFigure textInputFigure = (TextInputFigure) super
				.doCreateFigure();
		textInputFigure.setSelectorType(getWidgetModel().getSelectorType());
		textInputFigure.setDateTimeFormat(getWidgetModel().getDateTimeFormat());
		textInputFigure.setFileSource(getWidgetModel().getFileSource());
		textInputFigure.setFileReturnPart(getWidgetModel().getFileReturnPart());

		textInputFigure
				.addManualValueChangeListener(new IManualStringValueChangeListener() {

					public void manualValueChanged(String newValue) {
						outputText(newValue);
					}

					
				});

		getPVWidgetEditpartDelegate().setUpdateSuppressTime(-1);
		updatePropSheet();
		setPropertiesVisibilities(getWidgetModel().getSelectorType());
		return textInputFigure;
	}

	/**Call this method when user hit Enter or Ctrl+Enter for multiline input.
	 * @param newValue
	 */
	protected void outputText(String newValue) {
		if (getExecutionMode() == ExecutionMode.RUN_MODE) {
			setPVValue(TextInputModel.PROP_PVNAME, newValue);
			getWidgetModel().setPropertyValue(TextInputModel.PROP_TEXT,
					newValue, false);
		} else {
			getViewer()
					.getEditDomain()
					.getCommandStack()
					.execute(
							new SetWidgetPropertyCommand(getWidgetModel(),
									TextInputModel.PROP_TEXT, newValue));
		}
	}
	@Override
	protected TextFigure createTextFigure() {
		return new TextInputFigure(getExecutionMode() == ExecutionMode.RUN_MODE);
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new TextUpdateDirectEditPolicy());
	}

	@Override
	public void activate() {
		markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME,
				AbstractPVWidgetModel.PROP_PVVALUE);
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
		if (getExecutionMode() == ExecutionMode.RUN_MODE) {
			final TextInputModel model = getWidgetModel();
			if (model.isLimitsFromPV()) {
				PV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
				if (pv != null) {
					if (pvLoadLimitsListener == null)
						pvLoadLimitsListener = new PVListener() {
							public void pvValueUpdate(PV pv) {
								IValue value = pv.getValue();
								if (value != null
										&& value.getMetaData() instanceof INumericMetaData) {
									INumericMetaData new_meta = (INumericMetaData) value
											.getMetaData();
									if (meta == null || !meta.equals(new_meta)) {
										meta = new_meta;
										model.setPropertyValue(
												TextInputModel.PROP_MAX,
												meta.getDisplayHigh());
										model.setPropertyValue(
												TextInputModel.PROP_MIN,
												meta.getDisplayLow());
									}
								}
							}

							public void pvDisconnected(PV pv) {
							}
						};
					pv.addListener(pvLoadLimitsListener);
				}
			}
		}
	}

	/**
	 * @param text
	 */
	protected void outputPVValue(String text) {
		if(!getWidgetModel().getConfirmMessage().isEmpty())
			if(!GUIUtil.openConfirmDialog("PV Name: " + getPVName() +
					"\nNew Value: "+ text+ "\n\n"+
					getWidgetModel().getConfirmMessage()))
				return;
		try {			
			Object result;
			if(getWidgetModel().getFormat() != FormatEnum.STRING
					&& text.trim().indexOf(SPACE)!=-1){
				result = parseStringArray(text);
			}else
				result = parseString(text);
			setPVValue(AbstractPVWidgetModel.PROP_PVNAME, result);
		} catch (Exception e) {
			String msg = NLS
					.bind("Failed to write value to PV {0} from widget {1}.\nIllegal input : {2} \n",
							new String[] {
									getPVName(),
									getWidgetModel().getName(),
									text })
					+ e.toString();
			ConsoleService.getInstance().writeError(msg);
		}
	}
	
	@Override
	protected void registerPropertyChangeHandlers() {
		super.registerPropertyChangeHandlers();
		if (getExecutionMode() == ExecutionMode.RUN_MODE) {
			removeAllPropertyChangeHandlers(LabelModel.PROP_TEXT);
			IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
				public boolean handleChange(Object oldValue, Object newValue,
						final IFigure figure) {
					String text = (String) newValue;
					
					if(getPV() == null){
					 setFigureText(text);
					 if(getWidgetModel().isAutoSize()){
							Display.getCurrent().timerExec(10, new Runnable() {
								public void run() {
										performAutoSize();
								}
							});
						}
					}
					//Output pv value even if pv name is empty, so setPVValuelistener can be triggered.
					outputPVValue(text);
					return false;
				}			
			};
			setPropertyChangeHandler(LabelModel.PROP_TEXT, handler);
		}

		IWidgetPropertyChangeHandler pvNameHandler = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				registerLoadLimitsListener();
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME,
				pvNameHandler);
	
		getWidgetModel().getProperty(TextInputModel.PROP_LIMITS_FROM_PV)
			.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				updatePropSheet();
			}
		});
			
		
		
		IWidgetPropertyChangeHandler dateTimeFormatHandler = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((TextInputFigure) figure).setDateTimeFormat((String) newValue);
				return false;
			}
		};
		setPropertyChangeHandler(TextInputModel.PROP_DATETIME_FORMAT,
				dateTimeFormatHandler);

		IWidgetPropertyChangeHandler fileSourceHandler = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((TextInputFigure) figure)
						.setFileSource(FileSource.values()[(Integer) newValue]);
				return false;
			}
		};
		setPropertyChangeHandler(TextInputModel.PROP_FILE_SOURCE,
				fileSourceHandler);

		IWidgetPropertyChangeHandler fileReturnPartHandler = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((TextInputFigure) figure).setFileReturnPart(FileReturnPart
						.values()[(Integer) newValue]);
				return false;
			}
		};
		setPropertyChangeHandler(TextInputModel.PROP_FILE_RETURN_PART,
				fileReturnPartHandler);		
		
		if(getWidgetModel().getProperty(TextInputModel.PROP_SELECTOR_TYPE) != null)
			getWidgetModel().getProperty(TextInputModel.PROP_SELECTOR_TYPE)
				.addPropertyChangeListener(new PropertyChangeListener() {

					public void propertyChange(PropertyChangeEvent evt) {
						SelectorType selectorType = SelectorType.values()[(Integer) evt
								.getNewValue()];
						((TextInputFigure) figure)
								.setSelectorType(selectorType);
						setPropertiesVisibilities(selectorType);
					}

				});
	}

	protected void setPropertiesVisibilities(SelectorType selectorType) {
		switch (selectorType) {
		case NONE:
			getWidgetModel().setPropertyVisible(
					TextInputModel.PROP_DATETIME_FORMAT, false);
			getWidgetModel().setPropertyVisible(
					TextInputModel.PROP_FILE_RETURN_PART, false);
			getWidgetModel().setPropertyVisible(
					TextInputModel.PROP_FILE_SOURCE, false);
			break;
		case DATETIME:
			getWidgetModel().setPropertyVisible(
					TextInputModel.PROP_DATETIME_FORMAT, true);
			getWidgetModel().setPropertyVisible(
					TextInputModel.PROP_FILE_RETURN_PART, false);
			getWidgetModel().setPropertyVisible(
					TextInputModel.PROP_FILE_SOURCE, false);
			break;
		case FILE:
			getWidgetModel().setPropertyVisible(
					TextInputModel.PROP_DATETIME_FORMAT, false);
			getWidgetModel().setPropertyVisible(
					TextInputModel.PROP_FILE_RETURN_PART, true);
			getWidgetModel().setPropertyVisible(
					TextInputModel.PROP_FILE_SOURCE, true);
			break;
		default:
			break;
		}
	}

	@Override
	protected void doDeActivate() {
		super.doDeActivate();
		if (getWidgetModel().isLimitsFromPV()) {
			PV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
			if (pv != null && pvLoadLimitsListener != null) {
				pv.removeListener(pvLoadLimitsListener);
			}
		}

	}
	
	public DragTracker getDragTracker(Request request) {
		if (getExecutionMode() == ExecutionMode.RUN_MODE) {
			return new SelectEditPartTracker(this) {				
				@Override
				protected boolean handleButtonUp(int button) {
					if (button == 1) {
						//make widget in edit mode by single click
						performOpen();
					}
					return super.handleButtonUp(button);
				}
			};
		}else
			return super.getDragTracker(request);
	}

	@Override
	public void performRequest(Request request) {
		if (getFigure().isEnabled()
				&&((request.getType() == RequestConstants.REQ_DIRECT_EDIT &&
				getExecutionMode() != ExecutionMode.RUN_MODE)||
				request.getType() == RequestConstants.REQ_OPEN))
			performDirectEdit();
	}

	protected void performDirectEdit() {
		new TextEditManager(this, new LabelCellEditorLocator(
				(Figure) getFigure()), getWidgetModel().isMultilineInput()).show();
	}
	
	/**If the text has spaces in the string and the PV is numeric array type,
	 * it will return an array of numeric values. 
	 * @param text
	 * @return
	 * @throws ParseException
	 */
	private Object parseStringArray(final String text) throws ParseException{	
		String[] texts = text.split(" +"); //$NON-NLS-1$
		IValue pvValue = getPVValue(AbstractPVWidgetModel.PROP_PVNAME);
		if((pvValue instanceof IDoubleValue && (((IDoubleValue) pvValue).getValues().length > 1)) 
				||(pvValue instanceof ILongValue && (((ILongValue) pvValue).getValues().length > 1))
				||(pvValue instanceof PMObjectValue && 
					((PMObjectValue)pvValue).getLatestValue() instanceof VNumberArray)){
			double[] result = new double[texts.length];
			for (int i = 0; i < texts.length; i++) {
				Object o = parseString(texts[i]);
				if (o instanceof Number)
					result[i] = ((Number) o).doubleValue();
				else
					throw new ParseException(texts[i] + " cannot be parsed as a number!", i);
			}
			return result;
		}
//		if(pvValue instanceof IStringValue && ((IStringValue)pvValue).getValues().length>1){
//			return texts;
//		}
		
		return parseString(text);
	}

	/**
	 * Parse string to a value according PV value type and format
	 * 
	 * @param text
	 * @return value
	 * @throws ParseException
	 */
	private Object parseString(final String text) throws ParseException {
		IValue pvValue = getPVValue(AbstractPVWidgetModel.PROP_PVNAME);
		FormatEnum formatEnum = getWidgetModel().getFormat();
		
		if(pvValue == null)
			return text;
		
		if(pvValue instanceof PMObjectValue){
			return parseStringForPVManagerPV(formatEnum, text, ((PMObjectValue) pvValue).getLatestValue());
		}else
			return parseStringForUtilityPV(formatEnum, text, pvValue);

	}
	
	private Object parseStringForPVManagerPV(FormatEnum formatEnum,
			final String text, Object pvValue) throws ParseException {
		if(pvValue instanceof Scalar){
			Object value = ((Scalar)pvValue).getValue();
			if (value instanceof Number) {
				switch (formatEnum) {
				case HEX:
				case HEX64:
					return parseHEX(text, true);
				case STRING:
					return text;
				case DECIMAL:
				case COMPACT:
				case EXP:
					return parseDouble(text,true);
				case DEFAULT:
				default:
					try {
						return parseDouble(text, true);
					} catch (ParseException e) {
						return text;
					}
				}
			}else if(value instanceof String){
				if(pvValue instanceof VEnum){
					switch (formatEnum) {
					case HEX:
					case HEX64:
						return parseHEX(text, true);
					case STRING:
						return text;
					case DECIMAL:
					case EXP:
					case COMPACT:
						return parseDouble(text, true);
					case DEFAULT:
					default:
						try {
							return parseDouble(text, true);
						} catch (ParseException e) {
							return text;
						}
					}
				}else
					return text;
			}			
		}else if(pvValue instanceof Array){
			if(pvValue instanceof VNumberArray){
				switch (formatEnum) {
				case HEX:
				case HEX64:
					return parseHEX(text, true);
				case STRING:					
					return parseCharArray(text, ((VNumberArray)pvValue).getData().size());					
				case DECIMAL:
				case EXP:
				case COMPACT:
					return parseDouble(text, true);
				case DEFAULT:
				default:
					try {
						return parseDouble(text, true);
					} catch (ParseException e) {
						return text;
					}
				}
			}else {
				return text;
			}
		}
		return text;
	}

	private Object parseStringForUtilityPV(FormatEnum formatEnum,
			final String text, IValue pvValue) throws ParseException {
		if (pvValue instanceof IStringValue) {
			return text;
		}
		if (pvValue instanceof IDoubleValue) {
			switch (formatEnum) {
			case HEX:
			case HEX64:
				return parseHEX(text, true);
			case STRING:
				if (((IDoubleValue) pvValue).getValues().length > 1) {
					return parseCharArray(text, ((IDoubleValue) pvValue).getValues().length);
				} else
					return text;
			case DECIMAL:
			case EXP:
			case COMPACT:
				return parseDouble(text, true);
			case DEFAULT:
			default:
				try {
					return parseDouble(text, true);
				} catch (ParseException e) {
					return text;
				}
			}
		}

		if (pvValue instanceof ILongValue) {
			switch (formatEnum) {
			case HEX:
			case HEX64:
				return parseHEX(text, true);
			case STRING:
				if (((ILongValue) pvValue).getValues().length > 1) {
					return parseCharArray(text,((ILongValue) pvValue).getValues().length );
				} else
					return text;
			case DECIMAL:
			case EXP:
			case COMPACT:
				return parseDouble(text, true);
			case DEFAULT:
			default:
				try {
					return parseDouble(text, true);
				} catch (ParseException e) {
					return text;
				}
			}
		}

		if (pvValue instanceof IEnumeratedValue) {
			switch (formatEnum) {
			case HEX:
			case HEX64:
				return parseHEX(text, true);
			case STRING:
				return text;
			case DECIMAL:
			case EXP:
			case COMPACT:
				return parseDouble(text, true);
			case DEFAULT:
			default:
				try {
					return parseDouble(text, true);
				} catch (ParseException e) {
					return text;
				}
			}
		}

		return text;
	}

	private Integer[] parseCharArray(final String text, int currentLength) {
		Integer[] iString = new Integer[currentLength];
		char[] textChars = text.toCharArray();

		for (int ii = 0; ii < text.length(); ii++) {
			iString[ii] = Integer.valueOf(textChars[ii]);
		}
		for (int ii = text.length(); ii < currentLength; ii++) {
			iString[ii] = 0;
		}
		return iString;
	}

	private double parseDouble(final String text, final boolean coerce)
			throws ParseException {	
		
		double value = DECIMAL_FORMAT.parse(text.replace('e', 'E')).doubleValue(); //$NON-NLS-1$ //$NON-NLS-2$
		if (coerce) {
			double min = getWidgetModel().getMinimum();
			double max = getWidgetModel().getMaximum();
			if (value < min) {
				value = min;
			} else if (value > max)
				value = max;
		}
		return value;

	}

	private int parseHEX(final String text, final boolean coerce) {
		String valueText = text.trim();
		if (text.startsWith(TextUpdateEditPart.HEX_PREFIX)) {
			valueText = text.substring(2);
		}
		if (valueText.contains(" ")) { //$NON-NLS-1$
			valueText = valueText.substring(0, valueText.indexOf(SPACE));
		}
		long i = Long.parseLong(valueText, 16);
		if (coerce) {
			double min = getWidgetModel().getMinimum();
			double max = getWidgetModel().getMaximum();
			if (i < min) {
				i = (long) min;
			} else if (i > max)
				i = (long) max;
		}
		return (int) i; // EPICS_V3_PV doesn't support Long

	}


	@Override
	protected String formatValue(Object newValue, String propId) {
		String text = super.formatValue(newValue, propId);
		getWidgetModel()
				.setPropertyValue(TextInputModel.PROP_TEXT, text, false);
		return text;

	}
	
	/**
	 * @param newValue
	 */
	protected void updatePropSheet() {
		getWidgetModel().setPropertyVisible(
				TextInputModel.PROP_MAX, !getWidgetModel().isLimitsFromPV());
		getWidgetModel().setPropertyVisible(
				TextInputModel.PROP_MIN, !getWidgetModel().isLimitsFromPV());

	}

}
