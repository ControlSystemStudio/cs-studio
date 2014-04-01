/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.bool;

import org.csstudio.opibuilder.editparts.AlarmSeverityListener;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.editparts.AbstractBoolEditPart;
import org.csstudio.opibuilder.widgets.symbol.util.PermutationMatrix;
import org.csstudio.opibuilder.widgets.symbol.util.SymbolImageProperties;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.widgets.Display;
import org.epics.vtype.AlarmSeverity;

/**
 * Base edit part controller for a Boolean Symbol Image widget based on
 * {@link CommonBoolSymbolModel}.
 * 
 * @author SOPRA Group
 * 
 */
public abstract class CommonBoolSymbolEditpart extends AbstractBoolEditPart {

	private int maxAttempts;

	/**
	 * Sets those properties on the figure that are defined in the
	 * {@link CommonBoolSymbolFigure} base class. This method is called by
	 * {@link #doCreateFigure()}.
	 * 
	 * @param figure
	 *            the figure.
	 * @param model
	 *            the model.
	 */
	public void initializeCommonFigureProperties(
			CommonBoolSymbolFigure figure, CommonBoolSymbolModel model) {
		super.initializeCommonFigureProperties(figure, model);
		figure.setExecutionMode(getExecutionMode());
		figure.setSymbolImagePath(model, model.getSymbolImagePath());

		// Image default parameters
		SymbolImageProperties sip = new SymbolImageProperties();
		sip.setTopCrop(model.getTopCrop());
		sip.setBottomCrop(model.getBottomCrop());
		sip.setLeftCrop(model.getLeftCrop());
		sip.setRightCrop(model.getRightCrop());
		sip.setStretch(model.getStretch());
		sip.setAutoSize(model.isAutoSize());
		sip.setDegree(model.getDegree());
		sip.setFlipH(model.isFlipHorizontal());
		sip.setFlipV(model.isFlipVertical());
		sip.setMatrix(model.getPermutationMatrix());
		figure.setSymbolProperties(sip);

		if (model.getPVName() == null || model.getPVName().isEmpty())
			figure.setUseForegroundColor(true);
	}

	/**
	 * Registers symbol image property change handlers for the properties
	 * defined in {@link MonitorBoolSymbolModel}.
	 */
	public void registerSymbolImagePropertyHandlers() {
		// symbol image filename property
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
				IPath newImagePath = (IPath) newValue;
				imageFigure.setSymbolImagePath(getWidgetModel(), newImagePath);
				autoSizeWidget(imageFigure);
				return true;
			}
		};
		setPropertyChangeHandler(CommonBoolSymbolModel.PROP_SYMBOL_IMAGE_FILE, handler);
		
		// PV Name ForeColor color rule
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				if (newValue == null || ((String) newValue).isEmpty())
					((CommonBoolSymbolFigure) figure).setUseForegroundColor(true);
				else ((CommonBoolSymbolFigure) figure).setUseForegroundColor(false);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, handler);
		
		// ForeColor Alarm Sensitive
		getPVWidgetEditpartDelegate().addAlarmSeverityListener(new AlarmSeverityListener() {
			@Override
			public boolean severityChanged(AlarmSeverity severity,
					IFigure refreshableFigure) {
				CommonBoolSymbolFigure figure = (CommonBoolSymbolFigure) refreshableFigure;
				if (!getWidgetModel().isForeColorAlarmSensitve()) {
					figure.setUseForegroundColor(false);
				} else {
					if (severity.equals(AlarmSeverity.NONE))
						figure.setUseForegroundColor(false);
					else figure.setUseForegroundColor(true);
				}
				return true;
			}
		});
	}

	/**
	 * Registers image size property change handlers for the properties defined
	 * in {@link MonitorBoolSymbolModel}.
	 */
	public void registerImageSizePropertyHandlers() {
		// image auto-size property
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
				imageFigure.setAutoSize((Boolean) newValue);
				CommonBoolSymbolModel model = getWidgetModel();
				Dimension d = imageFigure.getAutoSizedDimension();
				if ((Boolean) newValue && !model.getStretch() && d != null) {
					model.setSize(d.width, d.height);
				}
				return false;
			}
		};
		setPropertyChangeHandler(CommonBoolSymbolModel.PROP_AUTOSIZE, handler);

		// image size (height/width) property
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
				imageFigure.resizeImage();
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(CommonBoolSymbolModel.PROP_HEIGHT, handler);
		setPropertyChangeHandler(CommonBoolSymbolModel.PROP_WIDTH, handler);
	}

	/**
	 * Registers image border property change handlers for the properties
	 * defined in {@link MonitorBoolSymbolModel}.
	 */
	public void registerImageBorderPropertyHandlers() {
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
				imageFigure.resizeImage();
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(CommonBoolSymbolModel.PROP_BORDER_WIDTH,
				handler);
		setPropertyChangeHandler(CommonBoolSymbolModel.PROP_BORDER_STYLE,
				handler);
	}

	/**
	 * Registers image stretch property change handlers for the properties
	 * defined in {@link MonitorBoolSymbolModel}.
	 */
	public void registerImageStretchPropertyHandlers() {
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
				imageFigure.setStretch((Boolean) newValue);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(CommonBoolSymbolModel.PROP_STRETCH, handler);
	}

	/**
	 * Registers image rotation property change handlers for the properties
	 * defined in {@link MonitorBoolSymbolModel}.
	 */
	public void registerImageRotationPropertyHandlers() {
		// degree rotation property
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
				int newDegree = (Integer) newValue;
				int oldDegree = (Integer) oldValue;
				
				PermutationMatrix oldMatrix = new PermutationMatrix(
						(double[][]) getPropertyValue(CommonBoolSymbolModel.PERMUTATION_MATRIX));
				PermutationMatrix newMatrix = PermutationMatrix.generateRotationMatrix(newDegree - oldDegree);
				PermutationMatrix result = newMatrix.multiply(oldMatrix);
				setPropertyValue(CommonBoolSymbolModel.PERMUTATION_MATRIX, result.getMatrix());
				
//				if (newDegree != 0 && newDegree != 90 && newDegree != 180
//						&& newDegree != 270) { // Reset with previous value
//					setPropertyValue(CommonBoolSymbolModel.PROP_DEGREE, oldValue);
//					Activator.getLogger().log(
//							Level.WARNING,
//							"ERROR in value of old degree " + oldDegree
//									+ ". The degree can only be 0, 90, 180 or 270");
//				} else {
					setPropertyValue(CommonBoolSymbolModel.PROP_DEGREE, newDegree);
					// imageFigure.setDegree(newDegree);
					imageFigure.setPermutationMatrix(result);
					autoSizeWidget(imageFigure);
//				}
				return false;
			}
		};
		setPropertyChangeHandler(CommonBoolSymbolModel.PROP_DEGREE, handler);

		// flip horizontal rotation property
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
				// imageFigure.setFlipH((Boolean) newValue);
				PermutationMatrix newMatrix = PermutationMatrix.generateFlipHMatrix();
				PermutationMatrix oldMatrix = imageFigure.getPermutationMatrix();
				PermutationMatrix result = newMatrix.multiply(oldMatrix);
				
				setPropertyValue(CommonBoolSymbolModel.PERMUTATION_MATRIX, result.getMatrix());
				setPropertyValue(CommonBoolSymbolModel.PROP_FLIP_HORIZONTAL, (Boolean) newValue);
				imageFigure.setPermutationMatrix(result);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(CommonBoolSymbolModel.PROP_FLIP_HORIZONTAL, handler);

		// flip vertical rotation property
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
				// imageFigure.setFlipV((Boolean) newValue);
				PermutationMatrix newMatrix = PermutationMatrix.generateFlipVMatrix();
				PermutationMatrix oldMatrix = imageFigure.getPermutationMatrix();
				PermutationMatrix result = newMatrix.multiply(oldMatrix);
				
				setPropertyValue(CommonBoolSymbolModel.PERMUTATION_MATRIX, result.getMatrix());
				setPropertyValue(CommonBoolSymbolModel.PROP_FLIP_VERTICAL, (Boolean) newValue);
				imageFigure.setPermutationMatrix(result);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(CommonBoolSymbolModel.PROP_FLIP_VERTICAL, handler);
	}

	/**
	 * Registers image crop property change handlers for the properties defined
	 * in {@link MonitorBoolSymbolModel}.
	 */
	public void registerImageCropPropertyHandlers() {
		// top crop property
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				if (newValue == null) {
					return false;
				}
				CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
				imageFigure.setTopCrop((Integer) newValue);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(CommonBoolSymbolModel.PROP_TOPCROP, handler);

		// bottom crop property
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
				imageFigure.setBottomCrop((Integer) newValue);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(CommonBoolSymbolModel.PROP_BOTTOMCROP,
				handler);

		// left crop property
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
				imageFigure.setLeftCrop((Integer) newValue);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(CommonBoolSymbolModel.PROP_LEFTCROP, handler);

		// right crop property
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
				imageFigure.setRightCrop((Integer) newValue);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(CommonBoolSymbolModel.PROP_RIGHTCROP,
				handler);
	}

	@Override
	public void registerPropertyChangeHandlers() {
		registerCommonPropertyChangeHandlers();
		registerSymbolImagePropertyHandlers();
		registerImageSizePropertyHandlers();
		registerImageStretchPropertyHandlers();
		registerImageRotationPropertyHandlers();
		registerImageBorderPropertyHandlers();
		registerImageCropPropertyHandlers();
	}

	@Override
	public CommonBoolSymbolModel getWidgetModel() {
		return (CommonBoolSymbolModel) getModel();
	}

	public void autoSizeWidget(final CommonBoolSymbolFigure imageFigure) {
		maxAttempts = 10;
		Runnable task = new Runnable() {
			public void run() {
				if (maxAttempts-- > 0 && imageFigure.isLoadingImage()) {
					Display.getDefault().timerExec(100, this);
					return;
				}
				CommonBoolSymbolModel model = getWidgetModel();
				Dimension d = imageFigure.getAutoSizedDimension();
				if (model.isAutoSize() && !model.getStretch() && d != null) {
					model.setSize(d.width, d.height);
				}
			}
		};
		Display.getDefault().timerExec(100, task);
	}

}
