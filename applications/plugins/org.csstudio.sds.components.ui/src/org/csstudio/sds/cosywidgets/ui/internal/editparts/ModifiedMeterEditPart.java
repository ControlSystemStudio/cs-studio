package org.csstudio.sds.cosywidgets.ui.internal.editparts;

import java.util.Map;

import org.csstudio.sds.cosywidgets.models.MeterModel;
import org.csstudio.sds.cosywidgets.models.ModifiedMeterModel;
import org.csstudio.sds.cosywidgets.ui.internal.figures.ModifiedMeterFigure;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.properties.IPropertyChangeListener;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;

/**
 * The controller.
 * 
 * @author jbercic
 * 
 */
public final class ModifiedMeterEditPart extends AbstractWidgetEditPart {
	/**
	 * Returns the casted model. This is just for convenience.
	 * 
	 * @return the casted {@link MeterModel}
	 */
	protected ModifiedMeterModel getCastedModel() {
		return (ModifiedMeterModel) getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		ModifiedMeterModel model = getCastedModel();
		// create AND initialize the view properly
		final ModifiedMeterFigure figure = new ModifiedMeterFigure();
		figure.setAngle(model.getAngle());
		figure.setInnerAngle(model.getInnerAngle());
		figure.setPointerColor(model.getPointerColor());
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

		model.getProperty(ModifiedMeterModel.PROP_VALUE)
				.addPropertyChangeListener(new IPropertyChangeListener() {
					public void propertyValueChanged(Object oldValue,
							Object newValue) {
					}

					public void propertyManualValueChanged(Object manualValue) {
					}

					public void dynamicsDescriptorChanged(
							DynamicsDescriptor dynamicsDescriptor) {
						figure.setDynamicValue(dynamicsDescriptor);
					}
				});
		figure.setDynamicValue(model
				.getDynamicsDescriptor(ModifiedMeterModel.PROP_VALUE));
		figure.setAliases(model.getAliases());
		return figure;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// register a handler that deals with updates of the "angle" property
		IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setAngle((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_ANGLE, handle);

		// register a handler that deals with updates of the "inner angle"
		// property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setInnerAngle((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_INNANGLE, handle);

		// register a handler that deals with updates of the "pointer color"
		// property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setPointerColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_POINTERCOLOR, handle);

		// register a handler that deals with updates of the "visible radius"
		// property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setVisibleRadius((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_RADIUS, handle);

		// register a handler that deals with updates of the "scale radius"
		// property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setScaleRadius((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_SCALERADIUS, handle);

		// register a handler that deals with updates of the "minor step"
		// property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setMinorStep((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_MINSTEP, handle);

		// register a handler that deals with updates of the "major step"
		// property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setMajorStep((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_MAJSTEP, handle);

		// register a handler that deals with updates of the "minimum value"
		// property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setMinValue((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_MINVAL, handle);

		// register a handler that deals with updates of the "maximum value"
		// property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setMaxValue((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_MAXVAL, handle);

		// register a handler that deals with updates of the "value" property
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setValue((Double) newValue);
				return false;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_VALUE, handle);

		// border color change handler
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setBorderColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_BORDER_COLOR, handle);
		// scale color change handler
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setScaleColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_SCALECOLOR, handle);
		// border width change handler
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setBorderWidth((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_BORDER_WIDTH, handle);
		// scale line width change handler
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setScaleWidth((Integer) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_SCALEWIDTH, handle);
		// scale text area radius change handler
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setTextRadius((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_TEXTRADIUS, handle);
		// transparency change handler
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setTransparent((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_TRANSPARENT, handle);

		// handlers for changes to the background color properties
		// M area background color
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setMColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_MCOLOR, handle);
		// LOLO area background color
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setLOLOColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_LOLOCOLOR, handle);
		// LO area background color
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setLOColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_LOCOLOR, handle);
		// HI area background color
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setHIColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_HICOLOR, handle);
		// HIHI area background color
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setHIHIColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_HIHICOLOR, handle);

		// boundaries for the five levels
		// M area boundary
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setMBound((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_MBOUND, handle);
		// LOLO area boundary
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setLOLOBound((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_LOLOBOUND, handle);
		// LO area boundary
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setLOBound((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_LOBOUND, handle);
		// HI area boundary
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setHIBound((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_HIBOUND, handle);
		// HIHI area boundary
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setHIHIBound((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_HIHIBOUND, handle);

		// alias change listener
		handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ModifiedMeterFigure meterFigure = (ModifiedMeterFigure) figure;
				meterFigure.setAliases((Map<String, String>) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ModifiedMeterModel.PROP_ALIASES, handle);
	}

}
