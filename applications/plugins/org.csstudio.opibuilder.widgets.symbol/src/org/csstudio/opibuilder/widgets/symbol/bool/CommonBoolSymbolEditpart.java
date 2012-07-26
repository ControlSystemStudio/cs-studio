package org.csstudio.opibuilder.widgets.symbol.bool;

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.editparts.AbstractBoolEditPart;
import org.csstudio.opibuilder.widgets.symbol.util.ImageOperation;
import org.csstudio.opibuilder.widgets.symbol.util.ImagePermuter;
import org.csstudio.opibuilder.widgets.symbol.util.ImageUtils;
import org.csstudio.opibuilder.widgets.symbol.util.SymbolImageProperties;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

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
		sip.setDisposition(model.getDisposition());
		figure.setSymbolProperties(sip);
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
		setPropertyChangeHandler(
				CommonBoolSymbolModel.PROP_SYMBOL_IMAGE_FILE, handler);
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
				if (newDegree != 0 && newDegree != 90 && newDegree != 180
						&& newDegree != 270) { // Reset with previous value
					setPropertyValue(CommonBoolSymbolModel.PROP_DEGREE,
							oldValue);
				} else {
					// imageFigure.setDegree(newDegree);
					int oldDegree = (Integer) oldValue;
					int direction = ImageUtils.getRotationDirection(oldDegree, newDegree);
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
								CommonBoolSymbolModel.PROP_DISPOSITION,
								disposition);
						imageFigure.setImageState(disposition);
					}
					autoSizeWidget(imageFigure);
				}
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
				String disposition = imageFigure.getImageState();
				char[] result = ImagePermuter.applyOperation(disposition.toCharArray(), ImageOperation.FH);
				disposition = String.valueOf(result);
				setPropertyValue(CommonBoolSymbolModel.PROP_DISPOSITION, disposition);
				imageFigure.setImageState(disposition);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(CommonBoolSymbolModel.PROP_FLIP_HORIZONTAL,
				handler);

		// flip vertical rotation property
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
				// imageFigure.setFlipV((Boolean) newValue);
				String disposition = imageFigure.getImageState();
				char[] result = ImagePermuter.applyOperation(disposition.toCharArray(), ImageOperation.FV);
				disposition = String.valueOf(result);
				setPropertyValue(CommonBoolSymbolModel.PROP_DISPOSITION, disposition);
				imageFigure.setImageState(disposition);
				autoSizeWidget(imageFigure);
				return false;
			}
		};
		setPropertyChangeHandler(CommonBoolSymbolModel.PROP_FLIP_VERTICAL,
				handler);
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
