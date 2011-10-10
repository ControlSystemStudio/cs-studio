/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.csstudio.swt.widgets.Activator;
import org.csstudio.swt.widgets.util.AbstractInputStreamRunnable;
import org.csstudio.swt.widgets.util.IJobErrorHandler;
import org.csstudio.swt.widgets.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * The image boolean button figure.
 * 
 * @author Xihui Chen
 * 
 */
public class ImageBoolButtonFigure extends AbstractBoolControlFigure {

	/**
	 * The image itself.
	 */
	private Image onImage, offImage;

	private boolean stretch;
	
	private boolean indicatorMode = false;

	private IPath onImagePath;

	private IPath offImagePath;
	
	private volatile boolean loadingImage;

	public ImageBoolButtonFigure(){
		this(false);
	}
	
	/**
	 * @param indicatorMode 
	 */
	public ImageBoolButtonFigure(boolean indicatorMode) {
		this.indicatorMode = indicatorMode;
		if(!indicatorMode)
			addMouseListener(buttonPresser);
		add(boolLabel);
	}

	public void dispose() {
		if (onImage != null) {
			onImage.dispose();
			onImage = null;
		}

		if (offImage != null) {
			offImage.dispose();
			offImage = null;
		}

	}

	public Dimension getAutoSizedDimension() {
		Image temp = booleanValue ? onImage : offImage;

		if (temp != null)
			return new Dimension(temp.getBounds().width + getInsets().left
					+ getInsets().right, temp.getBounds().height
					+ getInsets().bottom + getInsets().top);
		return null;

	}

	/**
	 * @return the offImagePath
	 */
	public IPath getOffImagePath() {
		return offImagePath;
	}

	/**
	 * @return the onImagePath
	 */
	public IPath getOnImagePath() {
		return onImagePath;
	}

	public boolean isLoadingImage() {
		return loadingImage;
	}
	
	/**
	 * @return the stretch
	 */
	public boolean isStretch() {
		return stretch;
	}

	@Override
	protected void layout() {
		Rectangle clientArea = getClientArea().getCopy();
		if (boolLabel.isVisible()) {
			Dimension labelSize = boolLabel.getPreferredSize();
			boolLabel.setBounds(new Rectangle(getLabelLocation(clientArea.x + clientArea.width
					/ 2 - labelSize.width / 2, clientArea.y + clientArea.height
					/ 2 - labelSize.height / 2), new Dimension(labelSize.width,
					labelSize.height)));
		}
		super.layout();
	}

	private void loadImageFromIPath(final IPath path,
			AbstractInputStreamRunnable uiTask) {
		if (path == null || path.isEmpty())
			return;

		ResourceUtil.pathToInputStreamInJob(path, uiTask, "Loading Image...",
				new IJobErrorHandler() {

					public void handleError(Exception exception) {
						Activator.getLogger().log(Level.WARNING,
								"Failed to load image " + path, exception);
						loadingImage = false;
					}
				});

		return;

	}

	@Override
	protected void paintClientArea(Graphics graphics) {
		if(loadingImage)
			return;
		Rectangle clientArea = getClientArea();
		Image temp;
		if (booleanValue)
			temp = onImage;
		else
			temp = offImage;
		if (temp != null)
			if (stretch)
				graphics.drawImage(temp, new Rectangle(temp.getBounds()),
						clientArea);
			else
				graphics.drawImage(temp, clientArea.getLocation());
		if (!isEnabled() && !indicatorMode) {
			graphics.setAlpha(DISABLED_ALPHA);
			graphics.setBackgroundColor(DISABLE_COLOR);
			graphics.fillRectangle(bounds);
		}
		super.paintClientArea(graphics);
	}

	@Override
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		if (!indicatorMode && runMode && value) 			
			setCursor(Cursors.HAND);	
	}

	public synchronized void setOffImagePath(IPath offImagePath) {
		loadingImage = true;
		this.offImagePath = offImagePath;
		if (offImage != null) {
			offImage.dispose();
			offImage = null;
		}

		AbstractInputStreamRunnable uiTask = new AbstractInputStreamRunnable() {

			@Override
			public void runWithInputStream(InputStream inputStream) {
				try {
					offImage = new Image(Display.getDefault(), inputStream);
				} finally {
					try {
						inputStream.close();
					} catch (IOException e) {						
					}
				}
				loadingImage = false;
				revalidate();
				repaint();				
			}
		};

		loadImageFromIPath(offImagePath, uiTask);

	}

	public synchronized void setOnImagePath(IPath onImagePath) {
		loadingImage = true;
		this.onImagePath = onImagePath;
		if (onImage != null) {
			onImage.dispose();
			onImage = null;
		}
		AbstractInputStreamRunnable uiTask = new AbstractInputStreamRunnable() {

			@Override
			public void runWithInputStream(InputStream inputStream) {
				try {
					onImage = new Image(Display.getDefault(), inputStream);
				} finally {
					try {
						inputStream.close();
					} catch (IOException e) {						
					}
				}
				loadingImage = false;
				revalidate();
				repaint();
			}
		};

		loadImageFromIPath(onImagePath, uiTask);
	}

	@Override
	public void setRunMode(boolean runMode) {
		super.setRunMode(runMode);
		
		setCursor((runMode && !indicatorMode) ? Cursors.HAND : null);
	}

	public void setStretch(boolean strech) {
		this.stretch = strech;
		repaint();
	}

	@Override
	public void setValue(double value) {
		super.setValue(value);
		revalidate();
	}

}
