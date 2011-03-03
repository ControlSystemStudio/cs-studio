/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import java.io.InputStream;
import java.util.logging.Level;

import org.csstudio.swt.widgets.Activator;
import org.csstudio.swt.widgets.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**The image boolean button figure.
 * @author Xihui Chen
 *
 */
public class ImageBoolButtonFigure extends AbstractBoolControlFigure {

	/**
	 * The image itself.
	 */
	private Image onImage, offImage;

	private boolean stretch;

	private Cursor cursor;

	private IPath onImagePath;

	private IPath offImagePath;

	public ImageBoolButtonFigure() {
		cursor = Cursors.HAND;
		addMouseListener(buttonPresser);
		add(boolLabel);
	}

	public void dispose() {
		if(onImage !=null){
			onImage.dispose();
			onImage = null;
		}

		if(offImage !=null){
			offImage.dispose();
			offImage = null;
		}

	}


	public Dimension getAutoSizedDimension() {
		Image temp = booleanValue ? onImage : offImage;

		if(temp != null)
			return new Dimension(temp.getBounds().width + getInsets().left + getInsets().right,
					temp.getBounds().height + getInsets().bottom + getInsets().top);
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

	/**
	 * @return the stretch
	 */
	public boolean isStretch() {
		return stretch;
	}

	@Override
	protected void layout() {
		Rectangle clientArea = getClientArea().getCopy();
		if(boolLabel.isVisible()){
			Dimension labelSize = boolLabel.getPreferredSize();
			boolLabel.setBounds(new Rectangle(clientArea.x + clientArea.width/2 - labelSize.width/2,
					clientArea.y + clientArea.height/2 - labelSize.height/2,
					labelSize.width, labelSize.height));
		}
		super.layout();
	}

	private Image loadImageFromIPath(IPath path){
		if(path == null || path.isEmpty())
			return null;
		try {
			InputStream input = ResourceUtil.pathToInputStream(path);
			return new Image(Display.getDefault(), input);
		} catch (Exception e) {
		    Activator.getLogger().log(Level.WARNING, "Failed to load image " + path, e);
		}
		return null;

	}

	@Override
	protected void paintClientArea(Graphics graphics) {
		Rectangle clientArea = getClientArea();
		Image temp;
		if(booleanValue)
			temp = onImage;
		else
			temp = offImage;
		if(temp !=null)
			if(stretch)
				graphics.drawImage(temp, new Rectangle(temp.getBounds()), clientArea);
			else
				graphics.drawImage(temp, clientArea.getLocation());
		if(!isEnabled()) {
			graphics.setAlpha(DISABLED_ALPHA);
			graphics.setBackgroundColor(DISABLE_COLOR);
			graphics.fillRectangle(bounds);
		}
		super.paintClientArea(graphics);
	}

	@Override
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		if(runMode){
			if(value){
				if(cursor == null || cursor.isDisposed())
					cursor = Cursors.HAND;
			}else {
				cursor = null;
			}
		}
		setCursor(runMode? cursor : null);
	}

	public void setOffImagePath(IPath offImagePath) {
		this.offImagePath = offImagePath;
		if(offImage != null){
			offImage.dispose();
			offImage = null;
		}
		offImage = loadImageFromIPath(offImagePath);
		revalidate();
	}

	public void setOnImagePath(IPath onImagePath) {
		this.onImagePath = onImagePath;
		if(onImage != null){
			onImage.dispose();
			onImage = null;
		}
		onImage = loadImageFromIPath(onImagePath);
		revalidate();
	}

	@Override
	public void setRunMode(boolean runMode) {
		super.setRunMode(runMode);
		setCursor(runMode? cursor : null);
	}

	public void setStretch(boolean strech) {
		this.stretch = strech;
	}

	@Override
	public void setValue(double value) {
		super.setValue(value);
		revalidate();
	}



}
