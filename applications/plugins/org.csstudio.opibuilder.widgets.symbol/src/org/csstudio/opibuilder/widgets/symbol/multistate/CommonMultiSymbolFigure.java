/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.multistate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.widgets.symbol.Activator;
import org.csstudio.opibuilder.widgets.symbol.image.AbstractSymbolImage;
import org.csstudio.opibuilder.widgets.symbol.util.IImageLoadedListener;
import org.csstudio.opibuilder.widgets.symbol.util.ImageUtils;
import org.csstudio.opibuilder.widgets.symbol.util.PermutationMatrix;
import org.csstudio.opibuilder.widgets.symbol.util.SymbolImageProperties;
import org.csstudio.opibuilder.widgets.symbol.util.SymbolLabelPosition;
import org.csstudio.swt.widgets.util.AbstractInputStreamRunnable;
import org.csstudio.swt.widgets.util.IJobErrorHandler;
import org.csstudio.swt.widgets.util.ResourceUtil;
import org.csstudio.swt.widgets.util.TextPainter;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Fred Arnaud (Sopra Group)
 */
public abstract class CommonMultiSymbolFigure extends Figure {

	// delegation pattern
	protected AbstractSymbolImage symbolImage;
	protected SymbolImageProperties symbolProperties;
	protected boolean workingWithSVG = false;
	protected boolean workingWithBool = false;
	
	// symbol label attributes
	protected Label label;
	protected boolean showSymbolLabel = false;
	protected SymbolLabelPosition labelPosition = SymbolLabelPosition.DEFAULT;
	private Point labelLocation;
	 
	/**
	 * The {@link IPath} to the states images.
	 */
	protected IPath symbolImagePath;
	protected IPath originalSymbolImagePath;
	protected Map<String, AbstractSymbolImage> images;
	
	protected String currentState;
	protected String previousState;
	protected List<String> statesStr;
	protected List<Double> statesDbl;
	
	private ExecutionMode executionMode;

	private int remainingImagesToLoad = 0;
	private HashMap<AbstractInputStreamRunnable, String> allowedStateMap = null;

	protected Color onColor = CustomMediaFactory.getInstance().getColor(
			CommonMultiSymbolModel.DEFAULT_ON_COLOR);
	protected Color offColor = CustomMediaFactory.getInstance().getColor(
			CommonMultiSymbolModel.DEFAULT_OFF_COLOR);
	
	private IImageLoadedListener imageLoadedListener;
	
	private Color foregroundColor;
	private boolean useForegroundColor = false;

	public CommonMultiSymbolFigure(boolean runMode) {
		this.executionMode = runMode ? ExecutionMode.RUN_MODE
				: ExecutionMode.EDIT_MODE;
		statesStr = new ArrayList<String>();
		statesDbl = new ArrayList<Double>();
		allowedStateMap = new HashMap<AbstractInputStreamRunnable, String>();
		images = new HashMap<String, AbstractSymbolImage>();
		label = new Label("STATE") {
			@Override
			public boolean containsPoint(int x, int y) {
				return false;
			}
		};
		label.setVisible(showSymbolLabel);
		// Add label to children
		add(label);
	}
	
	protected abstract AbstractSymbolImage createSymbolImage(boolean runMode);
	
	/**
	 * Return the current displayed image. If null, returns an empty image.
	 */
	public AbstractSymbolImage getSymbolImage() {
		if (ExecutionMode.RUN_MODE.equals(executionMode)
				&& currentState != null) {
			symbolImage = images.get(currentState);
		}
		if (symbolImage == null) { // create an empty image
			symbolImage = createSymbolImage(executionMode == ExecutionMode.RUN_MODE);
		}
		return symbolImage;
	}
	
	/**
	 * Return all mapped images.
	 */
	public Collection<AbstractSymbolImage> getAllImages() {
		Collection<AbstractSymbolImage> list = new ArrayList<AbstractSymbolImage>();
		if (isEditMode() && symbolImage != null) {
			list.add(symbolImage);
		}
		if (!isEditMode() && images != null && !images.isEmpty()) {
			list = images.values();
		}
		return list;
	}
	
	/**
	 * Dispose all the resources used by this figure
	 */
	public synchronized void disposeAll() {
		disposeCurrent();
		for (AbstractSymbolImage img : getAllImages()) {
			if (img != null && !img.isDisposed()) {
				img.dispose();
				img = null;
			}
		}
		images.clear();
	}
	
	/**
	 * Dispose the current resource used by this figure
	 */
	public synchronized void disposeCurrent() {
		if (symbolImage != null && !symbolImage.isDisposed()) {
			symbolImage.dispose();
			symbolImage = null;
		}
	}
	
	private void updateProperties() {
		for (AbstractSymbolImage img : getAllImages()) {
			symbolProperties.fillSymbolImage(img);
		}
	}
	
	/**
	 * Associates a given state with an image.
	 */
	public synchronized void setImage(String state, AbstractSymbolImage img) {
		if (images != null) {
			images.put(state, img);
		}
	}

	public void setSymbolProperties(SymbolImageProperties symbolProperties) {
		this.symbolProperties = symbolProperties;
	}

	public void setExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
	}
	
	public boolean isEditMode() {
		return ExecutionMode.EDIT_MODE.equals(executionMode);
	}
	
	// ************************************************************
	// States management
	// ************************************************************
	
	public synchronized void setState(int stateIndex) {
		if (stateIndex >= 0 && stateIndex < statesStr.size()) {
			currentState = statesStr.get(stateIndex);
			if (currentState != null) label.setText(currentState);
			repaint();
		} else {
			// TODO: display alert ?
		}
	}
	
	public synchronized void setState(Double state) {
		int index = statesDbl.indexOf(state);
		if (index < 0) { // search if image exists
			int newIndex = statesDbl.size();
			IPath path = findImage(newIndex);
			String strValue = String.valueOf(state);
			statesDbl.add(state);
			statesStr.add(strValue);
			remainingImagesToLoad = 1;
			loadImageFromFile(path, strValue);
			index = statesDbl.indexOf(state);
		}
		setState(index);
	}

	public synchronized void setState(String state) {
		int index = statesStr.indexOf(state);
		if (index < 0) { // search if image exists
			int newIndex = statesStr.size();
			IPath path = findImage(newIndex);
			try {
				statesDbl.add(Double.valueOf(state));
			} catch (NumberFormatException e) {
				statesDbl.add(null);
			}
			statesStr.add(state);
			remainingImagesToLoad = 1;
			loadImageFromFile(path, state);
			index = statesStr.indexOf(state);
		}
		setState(index);
	}
	
	private IPath findImage(int newIndex) {
		IPath path = null;
		if (workingWithBool) {
			IPath onImagePath, offImagePath = null;
			if (ImageUtils.isOnImage(symbolImagePath)) {
				onImagePath = symbolImagePath;
				offImagePath = ImageUtils.searchOffImage(symbolImagePath);
				if (offImagePath == null) offImagePath = symbolImagePath;
			} else { // Off image
				offImagePath = symbolImagePath;
				onImagePath = ImageUtils.searchOnImage(symbolImagePath);
				if (onImagePath == null) onImagePath = symbolImagePath;
			}
			if (newIndex > 0) path = onImagePath;
			else path = offImagePath;
		} else {
			String imageBasePath = ImageUtils.getMultistateBaseImagePath(symbolImagePath);
			path = ImageUtils.searchStateImage(newIndex, imageBasePath);
			if (path == null) path = symbolImagePath; // default
		}
		return path;
	}
	
	/**
	 * Set all the state string values.
	 * 
	 * @param states the states
	 */
	public void setStates(List<String> states) {
		this.statesStr = states;
		for (String state : states) {
			try {
				this.statesDbl.add(Double.valueOf(state));
			} catch (NumberFormatException e) {
				this.statesDbl.add(null);
			}
		}
		loadAllImages(states);
	}

	private void loadAllImages(List<String> states) {
		disposeAll();
		if (states == null || states.isEmpty()) {
			remainingImagesToLoad = 1;
			loadImageFromFile(symbolImagePath, null);
			return;
		}
		// Set threads variables
		remainingImagesToLoad = states.size();

		// Get base name
		if (workingWithBool) {
			IPath onImagePath, offImagePath = null;
			if (ImageUtils.isOnImage(symbolImagePath)) {
				onImagePath = symbolImagePath;
				offImagePath = ImageUtils.searchOffImage(symbolImagePath);
				if (offImagePath == null) offImagePath = symbolImagePath;
			} else { // Off image
				offImagePath = symbolImagePath;
				onImagePath = ImageUtils.searchOnImage(symbolImagePath);
				if (onImagePath == null) onImagePath = symbolImagePath;
			}
			String state = states.get(0);
			loadImageFromFile(offImagePath, state);
			for (int stateIndex = 1; stateIndex < states.size(); stateIndex++) {
				state = states.get(stateIndex);
				loadImageFromFile(onImagePath, state);
			}
		} else { // Standard behavior
			String imageBasePath = ImageUtils.getMultistateBaseImagePath(symbolImagePath);
			if (imageBasePath == null) { // Image do not match any state
				// TODO: alert state image missing
				for (int stateIndex = 0; stateIndex < states.size(); stateIndex++) {
					String state = states.get(stateIndex);
					// Load default image for all states
					loadImageFromFile(symbolImagePath, state);
				}
				return;
			}
			// Retrieve & set images paths
			for (int stateIndex = 0; stateIndex < states.size(); stateIndex++) {
				String state = states.get(stateIndex);
				IPath path = ImageUtils.searchStateImage(stateIndex, imageBasePath);
				if (path == null) loadImageFromFile(symbolImagePath, state);
				else loadImageFromFile(path, state);
			}
		}
	}

	public String getCurrentState() {
		return currentState;
	}

	public synchronized String getAllowedState(AbstractInputStreamRunnable uiTask) {
		if (allowedStateMap != null) {
			return allowedStateMap.get(uiTask);
		}
		return null;
	}
	
	// ************************************************************
	// Image loading
	// ************************************************************
	
	public boolean isLoadingImage() {
		return remainingImagesToLoad > 0;
	}
	
	public synchronized void decrementLoadingCounter() {
		remainingImagesToLoad--;
	}
	
	public void setImageLoadedListener(IImageLoadedListener listener) {
		this.imageLoadedListener = listener;
	}

	public synchronized void fireImageLoadedListeners() {
		imageLoadedListener.imageLoaded(this);
	}

	/**
	 * Set user selected image path (edit mode)
	 * 
	 * @param model
	 * @param imagePath
	 */
	public synchronized void setSymbolImagePath(CommonMultiSymbolModel model,
			IPath imagePath) {
		if (imagePath == null || imagePath.isEmpty()) {
			return;
		}
		if (!ImageUtils.isExtensionAllowed(imagePath)) {
			Activator.getLogger().log(
					Level.WARNING,
					"ERROR in loading image, extension not allowed "
							+ imagePath);
			return;
		}
		if (!imagePath.isAbsolute()) {
			imagePath = org.csstudio.opibuilder.util.ResourceUtil
					.buildAbsolutePath(model, imagePath);
		}
		symbolImagePath = imagePath;
		if (originalSymbolImagePath == null) originalSymbolImagePath = imagePath;
		if (imagePath.getFileExtension() != null
				&& "svg".compareToIgnoreCase(imagePath.getFileExtension()) == 0)
			workingWithSVG = true;
		else workingWithSVG = false;
		if (ImageUtils.isOffImage(imagePath) || ImageUtils.isOnImage(imagePath)) workingWithBool = true;
		else workingWithBool = false;
		loadAllImages(statesStr);
	}
	
	private synchronized void loadImageFromFile(final IPath imagePath, final String state) {
		if (imagePath != null && !imagePath.isEmpty()) {
			loadImage(imagePath, state, new IJobErrorHandler() {
				private int maxAttempts = 5;

				public void handleError(Exception exception) {
					if (maxAttempts-- > 0) {
						try {
							Thread.sleep(100);
							loadImage(imagePath, state, this);
							return;
						} catch (InterruptedException e) { }
					}
					decrementLoadingCounter();
					Activator.getLogger().log(Level.WARNING,
							"ERROR in loading image " + imagePath, exception);
				}
			});
		}
	}

	private synchronized void loadImage(final IPath imagePath, final String state,
			IJobErrorHandler errorHandler) {
		AbstractInputStreamRunnable uiTask = new AbstractInputStreamRunnable() {
			@Override
			public void runWithInputStream(InputStream stream) {
				synchronized (CommonMultiSymbolFigure.this) {
					Image tempImage = null;
					try {
						switch (executionMode) {
						case RUN_MODE:
							String state = getAllowedState(this);
							if (state != null) {
								AbstractSymbolImage asi = createSymbolImage(true);
								asi.setImagePath(imagePath);
								if (!workingWithSVG) {
									tempImage = new Image(Display.getDefault(), stream);
									ImageData imgData = tempImage.getImageData();
									asi.setOriginalImageData(imgData);
								}
								asi.updateData();
								setImage(state, asi);
							} else {
								symbolImage = createSymbolImage(!isEditMode());
								symbolImage.setImagePath(imagePath);
								if (!workingWithSVG) {
									tempImage = new Image(Display.getDefault(), stream);
									ImageData imgData = tempImage.getImageData();
									symbolImage.setOriginalImageData(imgData);
								}
								symbolImage.updateData();
							}
							break;
						case EDIT_MODE:
							symbolImage = createSymbolImage(false);
							symbolImage.setImagePath(imagePath);
							if (!workingWithSVG) {
								tempImage = new Image(Display.getDefault(), stream);
								ImageData imgData = tempImage.getImageData();
								symbolImage.setOriginalImageData(imgData);
							}
							symbolImage.updateData();
							break;
						}
					} finally {
						try {
							stream.close();
							if (tempImage != null && !tempImage.isDisposed()) {
								tempImage.dispose();
							}
						} catch (IOException exception) {
							Activator.getLogger()
									.log(Level.WARNING,
											"ERROR in closing image stream ",
											exception);
						}
					}
					// WARNING: the order is important
					// => image need to be initialized before size calculation 
					decrementLoadingCounter();
					repaint();
					revalidate();
					fireImageLoadedListeners();
				}
			}
		};
		if (allowedStateMap != null && state != null) {
			allowedStateMap.put(uiTask, state);
		}
		ResourceUtil.pathToInputStreamInJob(imagePath, uiTask,
				"Loading Image...", errorHandler);
	}
	
	// ************************************************************
	// Image color & paint
	// ************************************************************
		
	public Color getCurrentColor() {
		return getSymbolImage().getCurrentColor();
	}

	public void setOffColor(Color offColor) {
		if (this.offColor != null && this.offColor.equals(offColor))
			return;
		this.offColor = offColor;
		repaint();
	}

	public void setOnColor(Color onColor) {
		if (this.onColor != null && this.onColor.equals(onColor))
			return;
		this.onColor = onColor;
		repaint();
	}

	public void setUseForegroundColor(boolean useForegroundColor) {
		this.useForegroundColor = useForegroundColor;
		repaint();
	}

	@Override
	public Color getForegroundColor() {
		return foregroundColor;
	}

	@Override
	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
		repaint();
	}
	
	@Override
	public synchronized void paintFigure(final Graphics gfx) {
		if (isLoadingImage())  return;
		if (!isEditMode()) {
			// if run mode & state does not changes => nothing change
//			if (currentState == null || currentState.equals(previousState))
//				return;
		}
		Rectangle bounds = getBounds().getCopy();
		ImageUtils.crop(bounds, this.getInsets());
//		if (loadingError) {
//			disposeCurrent();
//			if (!symbolImagePath.isEmpty()) {
//				gfx.setBackgroundColor(getBackgroundColor());
//				gfx.setForegroundColor(getForegroundColor());
//				gfx.fillRectangle(bounds);
//				gfx.translate(bounds.getLocation());
//				TextPainter.drawText(gfx, "ERROR in loading image\n"
//						+ symbolImagePath, bounds.width / 2, bounds.height / 2,
//						TextPainter.CENTER);
//			}
//			return;
//		}
		if (getSymbolImage().isEmpty() && isEditMode()) {
			return;
		} else if (getSymbolImage().isEmpty()) {
			gfx.setBackgroundColor(getBackgroundColor());
			gfx.setForegroundColor(getForegroundColor());
			gfx.fillRectangle(bounds);
			gfx.translate(bounds.getLocation());
			TextPainter.drawText(gfx, "??", bounds.width / 2,
					bounds.height / 2, TextPainter.CENTER);
			return;
		}
		getSymbolImage().setBounds(bounds);
		getSymbolImage().setBorder(getBorder());
		int stateIndex = statesStr.indexOf(currentState);
		Color currentcolor = null;
		if (useForegroundColor) currentcolor = getForegroundColor();
		else currentcolor = stateIndex == 0 ? offColor : onColor;
		getSymbolImage().setCurrentColor(currentcolor);
		getSymbolImage().setAbsoluteScale(gfx.getAbsoluteScale());
		getSymbolImage().paintFigure(gfx);
	}
	
	// ************************************************************
	// Label management
	// ************************************************************
	
	protected Point getLabelLocation(final int x, final int y) {
		return getLabelLocation(new Point(x, y));
	}

	/**
	 * @param defaultLocation The default location.
	 * @return the location of the symbol label
	 */
	protected Point getLabelLocation(Point defaultLocation) {
		if (labelLocation == null)
			calculateLabelLocation(defaultLocation);
		return labelLocation;
	}

	public SymbolLabelPosition getLabelPosition() {
		return labelPosition;
	}
	
	protected void calculateLabelLocation(Point defaultLocation) {
		if (labelPosition == SymbolLabelPosition.DEFAULT) {
			labelLocation = defaultLocation;
			return;
		}
		Rectangle textArea = getClientArea();
		Dimension textSize = TextUtilities.INSTANCE.getTextExtents(
				label.getText(), getFont());
		int x = 0;
		if (textArea.width > textSize.width) {
			switch (labelPosition) {
			case CENTER:
			case TOP:
			case BOTTOM:
				x = (textArea.width - textSize.width) / 2;
				break;
			case RIGHT:
			case TOP_RIGHT:
			case BOTTOM_RIGHT:
				x = textArea.width - textSize.width;
				break;
			default:
				break;
			}
		}

		int y = 0;
		if (textArea.height > textSize.height) {
			switch (labelPosition) {
			case CENTER:
			case LEFT:
			case RIGHT:
				y = (textArea.height - textSize.height) / 2;
				break;
			case BOTTOM:
			case BOTTOM_LEFT:
			case BOTTOM_RIGHT:
				y = textArea.height - textSize.height;
				break;
			default:
				break;
			}
		}
		if (useLocalCoordinates())
			labelLocation = new Point(x, y);
		else
			labelLocation = new Point(x + textArea.x, y + textArea.y);
	}
	
	/**
	 * @return the showSymbolLabel
	 */
	public boolean isShowSymbolLabel() {
		return showSymbolLabel;
	}

	public void setSymbolLabelPosition(SymbolLabelPosition labelPosition) {
		this.labelPosition = labelPosition;
		labelPosition = null;
		revalidate();
		repaint();
	}

	/**
	 * @param showSymbolLabel the showSymbolLabel to set
	 */
	public void setShowSymbolLabel(boolean showSymbolLabel) {
		if (this.showSymbolLabel == showSymbolLabel)
			return;
		this.showSymbolLabel = showSymbolLabel;
		label.setVisible(showSymbolLabel);
	}
	
	// ************************************************************
	// Image size calculation delegation
	// ************************************************************
	
	public synchronized void resizeImage() {
		for (AbstractSymbolImage asi : getAllImages()) {
			asi.resizeImage();
		}
		repaint();
	}

	public synchronized void setAutoSize(final boolean autoSize) {
		if (symbolProperties != null) {
			symbolProperties.setAutoSize(autoSize);
			updateProperties();
		}
		repaint();
	}
	
	public synchronized Dimension getAutoSizedDimension() {
		return getSymbolImage().getAutoSizedDimension();
	}
	
	// ************************************************************
	// Image crop calculation delegation
	// ************************************************************
	
	public synchronized void setLeftCrop(final int newval) {
		if (symbolProperties != null) {
			symbolProperties.setLeftCrop(newval);
			updateProperties();
		}
		repaint();
	}
	public synchronized void setRightCrop(final int newval) {
		if (symbolProperties != null) {
			symbolProperties.setRightCrop(newval);
			updateProperties();
		}
		repaint();
	}
	public synchronized void setBottomCrop(final int newval) {
		if (symbolProperties != null) {
			symbolProperties.setBottomCrop(newval);
			updateProperties();
		}
		repaint();
	}
	public synchronized void setTopCrop(final int newval) {
		if (symbolProperties != null) {
			symbolProperties.setTopCrop(newval);
			updateProperties();
		}
		repaint();
	}
	public int getLeftCrop() {
		return getSymbolImage().getLeftCrop();
	}
	public int getRightCrop() {
		return getSymbolImage().getRightCrop();
	}
	public int getBottomCrop() {
		return getSymbolImage().getBottomCrop();
	}
	public int getTopCrop() {
		return getSymbolImage().getTopCrop();
	}
	
	// ************************************************************
	// Image flip & degree & stretch calculation delegation
	// ************************************************************
	
	public synchronized void setStretch(final boolean newval) {
		if (symbolProperties != null) {
			symbolProperties.setStretch(newval);
			updateProperties();
		}
		repaint();
	}
	public synchronized void setFlipV(boolean flipV) {
		if (symbolProperties != null) {
			symbolProperties.setFlipV(flipV);
			updateProperties();
		}
		repaint();
	}
	public synchronized void setFlipH(boolean flipH) {
		if (symbolProperties != null) {
			symbolProperties.setFlipH(flipH);
			updateProperties();
		}
		repaint();
	}
	public synchronized void setDegree(Integer degree) {
		if (symbolProperties != null) {
			symbolProperties.setDegree(degree);
			updateProperties();
		}
		repaint();
	}
	public void setPermutationMatrix(PermutationMatrix permutationMatrix) {
		if (symbolProperties != null) {
			symbolProperties.setMatrix(permutationMatrix);
			updateProperties();
		}
		repaint();
	}
	public PermutationMatrix getPermutationMatrix() {
		return getSymbolImage().getPermutationMatrix();
	}
	public boolean isStretch() {
		return getSymbolImage().isStretch();
	}
	public synchronized boolean isFlipV() {
		return getSymbolImage().isFlipV();
	}
	public synchronized boolean isFlipH() {
		return getSymbolImage().isFlipH();
	}
	public synchronized Integer getDegree() {
		return getSymbolImage().getDegree();
	}
	
	// ************************************************************
	// Override Figure class methods 
	// ************************************************************
	
	/**
	 * We want to have local coordinates here.
	 * 
	 * @return True if here should used local coordinates
	 */
	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}

	@Override
	public void setFont(Font f) {
		super.setFont(f);
		label.setFont(f);
		revalidate();
	}
	
	@Override
	public void invalidate() {
		labelLocation = null;
		super.invalidate();
	}
	
	@Override
	protected void layout() {
		Rectangle clientArea = getClientArea().getCopy();
		if (label.isVisible()) {
			Dimension labelSize = label.getPreferredSize();
			label.setBounds(new Rectangle(getLabelLocation(clientArea.x
					+ clientArea.width / 2 - labelSize.width / 2, clientArea.y
					+ clientArea.height / 2 - labelSize.height / 2),
					new Dimension(labelSize.width, labelSize.height)));
		}
		super.layout();
	}
}
