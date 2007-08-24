package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.MeterModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableMeterFigure;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.properties.IPropertyChangeListener;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * The controller.
 * 
 * @author jbercic
 * 
 */
public final class MeterEditPart extends AbstractWidgetEditPart {
	/**
	 * Returns the casted model. This is just for convenience.
	 * 
	 * @return the casted {@link MeterModel}
	 */
	protected MeterModel getCastedModel() {
		return (MeterModel) getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		MeterModel model = getCastedModel();
		// create AND initialize the view properly
		final RefreshableMeterFigure figure = new RefreshableMeterFigure();
		figure.setAngle(model.getAngle());
		figure.setInnerAngle(model.getInnerAngle());
		figure.setNeedleColor(model.getNeedleColor());
		figure.setVisibleRadius(model.getVisibleRadius());
		figure.setScaleRadius(model.getScaleRadius());
		figure.setMinorStep(model.getMinorStep());
		figure.setMajorStep(model.getMajorStep());
		figure.setMinValue(model.getMinValue());
		figure.setMaxValue(model.getMaxValue());
		figure.setValue(model.getValue());
		figure.setBorderColor(model.getBorderColor());
		figure.setScaleColor(model.getScaleColor());
		figure.setBorderWidth(model.getBorderWidth());
		figure.setScaleWidth(model.getScaleWidth());
		figure.setTextRadius(model.getTextRadius());
		figure.setTransparent(model.getTransparent());
		//figure.setFormat(model.getFormat());
		//figure.setScaleFormat(model.getScaleFormat());
		
		figure.setMColor(model.getMColor());
		figure.setLOLOColor(model.getLOLOColor());
		figure.setLOColor(model.getLOColor());
		figure.setHIColor(model.getHIColor());
		figure.setHIHIColor(model.getHIHIColor());
		
		figure.setMBound(model.getMBound());
		figure.setLOLOBound(model.getLOLOBound());
		figure.setLOBound(model.getLOBound());
		figure.setHIBound(model.getHIBound());
		figure.setHIHIBound(model.getHIHIBound());
		
		figure.setValuesFont(model.getValuesFont());
		figure.setChannelFont(model.getChannelFont());
		
		figure.setDecimalPlaces(model.getPrecision());
	
		model.getProperty(MeterModel.PROP_VALUE).addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyValueChanged(Object oldValue, Object newValue) {}
			public void propertyManualValueChanged(Object manualValue) {}
			public void dynamicsDescriptorChanged(DynamicsDescriptor dynamicsDescriptor) {
				figure.setDynamicValue(dynamicsDescriptor);
			}
		});
		figure.setDynamicValue(model.getDynamicsDescriptor(MeterModel.PROP_VALUE));
		return figure;
	}
	
	/**
	 * Registers color property change handlers.
	 */
	protected void registerColorPropertyHandlers() {
		// needle
		IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setNeedleColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_NEEDLECOLOR, handle);
		
		// border
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setBorderColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_BORDER_COLOR, handle);
		
		// scale
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setScaleColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_SCALECOLOR, handle);
		
		//M area
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setMColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_MCOLOR, handle);
		
		//LOLO area
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setLOLOColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_LOLOCOLOR, handle);
		
		//LO area
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setLOColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_LOCOLOR, handle);
		
		//HI area
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setHIColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_HICOLOR, handle);
		
		//HIHI area
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setHIHIColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_HIHICOLOR, handle);
		
		// precision
		IWidgetPropertyChangeHandler precisionHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableMeterFigure meter = (RefreshableMeterFigure) refreshableFigure;
				meter.setDecimalPlaces((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_PRECISION, precisionHandler);
	}
	
	/**
	 * Registers boundary property change handlers for the five levels.
	 */
	protected void registerBoundaryPropertyHandlers() {
		//M
		IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setMBound((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_MBOUND, handle);
		
		//LOLO
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setLOLOBound((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_LOLOBOUND, handle);
		
		//LO
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setLOBound((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_LOBOUND, handle);
		
		//HI
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setHIBound((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_HIBOUND, handle);
		
		//HIHI
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setHIHIBound((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_HIHIBOUND, handle);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// register handlers to deal with resizes
		IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.invalidateBackground();
				meterFigure.invalidateNeedle();
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_HEIGHT, handle);
		setPropertyChangeHandler(MeterModel.PROP_WIDTH, handle);
		
		// register a handler that deals with updates of the "angle" property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setAngle((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_ANGLE, handle);
		
		// register a handler that deals with updates of the "inner angle" property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setInnerAngle((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_INNANGLE, handle);

		// register a handler that deals with updates of the "visible radius" property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setVisibleRadius((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_RADIUS, handle);
		
		// register a handler that deals with updates of the "scale radius" property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setScaleRadius((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_SCALERADIUS, handle);
		
		// register a handler that deals with updates of the "minor step" property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setMinorStep((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_MINSTEP, handle);
		
		// register a handler that deals with updates of the "major step" property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setMajorStep((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_MAJSTEP, handle);
		
		// register a handler that deals with updates of the "minimum value" property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setMinValue((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_MINVAL, handle);
		
		// register a handler that deals with updates of the "maximum value" property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setMaxValue((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_MAXVAL, handle);
		
		// register a handler that deals with updates of the "value" property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setValue((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_VALUE, handle);
		
		//border width change handler
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setBorderWidth((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_BORDER_WIDTH, handle);
		//scale line width change handler
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setScaleWidth((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_SCALEWIDTH, handle);
		//scale text area radius change handler
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setTextRadius((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_TEXTRADIUS, handle);
		//transparency change handler
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setTransparent((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_TRANSPARENT, handle);
		
		//values font change handler
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setValuesFont((FontData) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_VALFONT, handle);
		
		//channel font change handler
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
				meterFigure.setChannelFont((FontData) newValue); 
				return true;
			}
		};
		setPropertyChangeHandler(MeterModel.PROP_CHANFONT, handle);
		
//		//format change handler
//		handle = new IWidgetPropertyChangeHandler() {
//			public boolean handleChange(final Object oldValue, final Object newValue,
//					final IFigure figure) {
//				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
//				meterFigure.setFormat((String) newValue);
//				return true;
//			}
//		};
//		setPropertyChangeHandler(MeterModel.PROP_FORMAT, handle);
		
//		//scale format change handler
//		handle = new IWidgetPropertyChangeHandler() {
//			public boolean handleChange(final Object oldValue, final Object newValue,
//					final IFigure figure) {
//				RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
//				meterFigure.setScaleFormat((String) newValue);
//				return true;
//			}
//		};
//		setPropertyChangeHandler(MeterModel.PROP_SCALEFORMAT, handle);
		
		registerColorPropertyHandlers();
		registerBoundaryPropertyHandlers();
	}

}
