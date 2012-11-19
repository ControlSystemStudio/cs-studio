package org.csstudio.opibuilder.widgets.symbol.bool;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.editparts.AbstractBoolControlEditPart;
import org.csstudio.opibuilder.widgets.symbol.util.ImageOperation;
import org.csstudio.opibuilder.widgets.symbol.util.ImagePermuter;
import org.csstudio.opibuilder.widgets.symbol.util.ImageUtils;
import org.csstudio.opibuilder.widgets.symbol.util.SymbolImageProperties;
import org.csstudio.swt.widgets.datadefinition.IManualValueChangeListener;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * Edit part Controller for a Control Boolean Symbol Image widget based on
 * {@link ControlBoolSymbolModel}.
 * 
 * @author SOPRA Group
 * 
 */
public class ControlBoolSymbolEditpart extends AbstractBoolControlEditPart {

	private int maxAttempts;

	@Override
	protected IFigure doCreateFigure() {
		ControlBoolSymbolFigure figure = new ControlBoolSymbolFigure();
		initializeCommonFigureProperties(figure, getWidgetModel());
		return (IFigure) figure;
	}

	/**
	 * Sets those properties on the figure that are defined in the
	 * {@link ControlBoolSymbolFigure} implementation class. This method is
	 * called by {@link #doCreateFigure()}.
	 * 
	 * @param figure
	 *            the figure.
	 * @param model
	 *            the model.
	 */
	public void initializeCommonFigureProperties(
			final ControlBoolSymbolFigure figure, ControlBoolSymbolModel model) {
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
		sip.setDisposition(model.getDisposition());
		figure.setSymbolProperties(sip);
		
		figure.addManualValueChangeListener(new IManualValueChangeListener() {
			public void manualValueChanged(double newValue) {
				if (getExecutionMode() == ExecutionMode.RUN_MODE) {
					autoSizeWidget(figure);
				}
			}
		});
	}

	/**
	 * Registers symbol image property change handlers for the properties
	 * defined in {@link ControlBoolSymbolModel}.
	 */
	public void registerSymbolImagePropertyHandlers() {
		// symbol image filename property
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
				IPath newImagePath = (IPath) newValue;
				imageFigure.setSymbolImagePath(getWidgetModel(), newImagePath);
				autoSizeWidget(imageFigure);
				return true;
			}
		};
		setPropertyChangeHandler(ControlBoolSymbolModel.PROP_SYMBOL_IMAGE_FILE,
				handler);
	}

	/**
	 * Registers image size property change handlers for the properties defined
	 * in {@link ControlBoolSymbolModel}.
	 */
	public void registerImageSizePropertyHandlers() {
		// image auto-size property
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
				imageFigure.setAutoSize((Boolean) newValue);
				ControlBoolSymbolModel model = (ControlBoolSymbolModel) getModel();
				Dimension d = imageFigure.getAutoSizedDimension();
				if ((Boolean) newValue && !model.getStretch() && d != null) {
					model.setSize(d.width, d.height);
				}
				return false;
			}
		};
		setPropertyChangeHandler(ControlBoolSymbolModel.PROP_AUTOSIZE, handler);

		// image size (height/width) property
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
				imageFigure.resizeImage();
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ControlBoolSymbolModel.PROP_HEIGHT, handler);
		setPropertyChangeHandler(ControlBoolSymbolModel.PROP_WIDTH, handler);
	}

	/**
	 * Registers image border property change handlers for the properties
	 * defined in {@link ControlBoolSymbolModel}.
	 */
	public void registerImageBorderPropertyHandlers() {
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
				imageFigure.resizeImage();
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ControlBoolSymbolModel.PROP_BORDER_WIDTH,
				handler);
		setPropertyChangeHandler(ControlBoolSymbolModel.PROP_BORDER_STYLE,
				handler);
	}

	/**
	 * Registers image stretch property change handlers for the properties
	 * defined in {@link ControlBoolSymbolModel}.
	 */
	public void registerImageStretchPropertyHandlers() {
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
				imageFigure.setStretch((Boolean) newValue);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ControlBoolSymbolModel.PROP_STRETCH, handler);
	}

	/**
	 * Registers image rotation property change handlers for the properties
	 * defined in {@link ControlBoolSymbolModel}.
	 */
	public void registerImageRotationPropertyHandlers() {
		// degree rotation property
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				int newDegree = (Integer) newValue;
				ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
				if (newDegree != 0 && newDegree != 90
						&& newDegree != 180
						&& newDegree != 270) {
					setPropertyValue(ControlBoolSymbolModel.PROP_DEGREE,
							oldValue);
				} else {
					imageFigure.setDegree((Integer) newValue);
					int oldDegree = (Integer) oldValue;
					int direction = ImageUtils.getRotationDirection(oldDegree,newDegree);
					if (direction != -1) {
						ImageOperation IOp = null;
						String disposition = imageFigure.getImageState();
						switch (direction) {
						case SWT.LEFT: // left 90 degrees
							IOp = ImageOperation.RL90;
							break;
						case SWT.RIGHT: // right 90 degrees
							IOp = ImageOperation.RR90;
							break;
						case SWT.DOWN: // 180 degrees
							IOp = ImageOperation.R180;
							break;
						}
						char[] result = ImagePermuter.applyOperation(disposition.toCharArray(), IOp);
						disposition = String.valueOf(result);
						setPropertyValue(
								ControlBoolSymbolModel.PROP_DISPOSITION,
								disposition);
						imageFigure.setImageState(disposition);
					}
					autoSizeWidget(imageFigure);
				}
				return false;
			}
		};
		setPropertyChangeHandler(ControlBoolSymbolModel.PROP_DEGREE, handler);

		// flip horizontal rotation property
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
				// imageFigure.setFlipH((Boolean) newValue);
				String disposition = imageFigure.getImageState();
				char[] result = ImagePermuter.applyOperation(disposition.toCharArray(), ImageOperation.FH);
				disposition = String.valueOf(result);
				setPropertyValue(ControlBoolSymbolModel.PROP_DISPOSITION, disposition);
				imageFigure.setImageState(disposition);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ControlBoolSymbolModel.PROP_FLIP_HORIZONTAL,
				handler);

		// flip vertical rotation property
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
				// imageFigure.setFlipV((Boolean) newValue);
				String disposition = imageFigure.getImageState();
				char[] result = ImagePermuter.applyOperation(disposition.toCharArray(), ImageOperation.FV);
				disposition = String.valueOf(result);
				setPropertyValue(ControlBoolSymbolModel.PROP_DISPOSITION, disposition);
				imageFigure.setImageState(disposition);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ControlBoolSymbolModel.PROP_FLIP_VERTICAL,
				handler);
	}

	/**
	 * Registers image crop property change handlers for the properties defined
	 * in {@link ControlBoolSymbolModel}.
	 */
	public void registerImageCropPropertyHandlers() {
		// top crop property
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				if (newValue == null) {
					return false;
				}
				ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
				imageFigure.setTopCrop((Integer) newValue);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ControlBoolSymbolModel.PROP_TOPCROP, handler);

		// bottom crop property
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
				imageFigure.setBottomCrop((Integer) newValue);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ControlBoolSymbolModel.PROP_BOTTOMCROP,
				handler);

		// left crop property
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
				imageFigure.setLeftCrop((Integer) newValue);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ControlBoolSymbolModel.PROP_LEFTCROP, handler);

		// right crop property
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
				imageFigure.setRightCrop((Integer) newValue);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(ControlBoolSymbolModel.PROP_RIGHTCROP, handler);
	}

	/**
	 * Registers PV value property change handlers for the properties defined in
	 * {@link ControlBoolSymbolModel}.
	 */
	public void registerPVValuePropertyChangeHandlers() {
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				if (newValue == null) {
					return false;
				}
				ControlBoolSymbolFigure figure = (ControlBoolSymbolFigure) refreshableFigure;
				autoSizeWidget(figure);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, handler);
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
		registerPVValuePropertyChangeHandlers();
	}

	/**
	 * Get the control widget model.
	 * 
	 * @return the control widget model.
	 */
	@Override
	public ControlBoolSymbolModel getWidgetModel() {
		return (ControlBoolSymbolModel) super.getWidgetModel();
	}

	@Override
	public void deactivate() {
		super.deactivate();
		((ControlBoolSymbolFigure) getFigure()).dispose();
	}

	public void autoSizeWidget(final ControlBoolSymbolFigure imageFigure) {
		maxAttempts = 10;
		Runnable task = new Runnable() {
			public void run() {
				if (maxAttempts-- > 0 && imageFigure.isLoadingImage()) {
					Display.getDefault().timerExec(100, this);
					return;
				}
				ControlBoolSymbolModel model = (ControlBoolSymbolModel) getModel();
				Dimension d = imageFigure.getAutoSizedDimension();
				if (model.isAutoSize() && !model.getStretch() && d != null) {
					model.setSize(d.width, d.height);
				}
			}
		};
		Display.getDefault().timerExec(100, task);
	}

}
